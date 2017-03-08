package cn.dreamtobe.grpc.client.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, InitialActivity::class.java))
        finish()
   }
}
