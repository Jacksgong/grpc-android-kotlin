package cn.dreamtobe.grpc.client.logic

import android.util.Log

/**
 * Created by Jacksgong on 26/02/2017.
 */
class Logger {

    private val tag: String

    constructor(javaClass: Class<*>) {
        tag = javaClass.simpleName
    }

    fun info(msg: String) {
        Log.i(tag, msg)
    }
}