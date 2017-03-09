/*
 * Copyright (C) 2017 Jacksgong(blog.dreamtobe.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.dreamtobe.grpc.client.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import cn.dreamtobe.grpc.client.R
import cn.dreamtobe.grpc.client.model.ServerApi

/**
 * Created by Jacksgong on 07/03/2017.
 */
class InitialActivity : AppCompatActivity() {

    lateinit var mHostEdt: EditText
    lateinit var mPortEdt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Initial"
        setContentView(R.layout.activity_initial)

        mHostEdt = findViewById(R.id.host_edt) as EditText
        mPortEdt = findViewById(R.id.port_edt) as EditText

        mHostEdt.setText(ServerApi.HOST)
        mPortEdt.setText(ServerApi.PORT.toString())
    }

    fun onClickConfirm(view: View) {
        ServerApi.HOST = mHostEdt.text.toString()
        ServerApi.PORT = mPortEdt.text.toString().toInt()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}