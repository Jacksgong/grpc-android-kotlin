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
