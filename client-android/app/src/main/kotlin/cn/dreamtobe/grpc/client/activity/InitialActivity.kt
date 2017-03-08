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

    lateinit var hostEdt: EditText
    lateinit var portEdt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Initial"
        setContentView(R.layout.activity_initial)

        hostEdt = findViewById(R.id.host_edt) as EditText
        portEdt = findViewById(R.id.port_edt) as EditText

        hostEdt.setText(ServerApi.host)
        portEdt.setText(ServerApi.port.toString())
    }

    fun onClickConfirm(view: View) {
        ServerApi.host = hostEdt.text.toString()
        ServerApi.port = portEdt.text.toString().toInt()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}