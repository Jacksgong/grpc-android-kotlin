package cn.dreamtobe.grpc.client.tools

import android.util.Log

/**
 * Created by Jacksgong on 07/03/2017.
 */
class Logger {
    companion object {
        fun log(o: Object, msg: String?, priority: Int = Log.DEBUG) {
            log(o.javaClass, msg, priority)
        }

        fun log(javaClass: Class<*>, msg: String?, priority: Int = Log.DEBUG) {
            msg ?: return

            Log.println(priority, javaClass.simpleName, msg)
        }
    }

}
