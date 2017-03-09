package cn.dreamtobe.grpc.client.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import cn.dreamtobe.grpc.client.R
import cn.dreamtobe.grpc.client.logic.ServerApi

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