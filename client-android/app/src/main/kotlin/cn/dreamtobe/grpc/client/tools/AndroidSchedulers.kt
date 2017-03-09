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

import android.os.Handler
import android.os.Looper
import rx.Scheduler
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Jacksgong on 07/03/2017.
 */
object AndroidSchedulers {
    fun singleTaskThread(): Scheduler {
        val executor = ThreadPoolExecutor(1, 1, 5, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>(), object : ThreadFactory {

            private val threadNumber = AtomicInteger(1)
            private val group = Thread.currentThread().threadGroup

            override fun newThread(r: Runnable?): Thread {
                val t = Thread(group, r, "singleTaskThread ${threadNumber.getAndIncrement()}")

                if (t.isDaemon) {
                    t.isDaemon = false
                }

                if (t.priority != Thread.NORM_PRIORITY) {
                    t.priority = Thread.NORM_PRIORITY
                }
                return t
            }
        }, ThreadPoolExecutor.DiscardPolicy())

        executor.allowCoreThreadTimeOut(true)

        return Schedulers.from(executor)
    }


    private val mainThreadScheduler by lazy { HandlerThreadScheduler(Handler(Looper.getMainLooper())) }
    fun mainThread(): Scheduler {
        if (Hook.onMainScheduler != null) return Hook.onMainScheduler!!.call(mainThreadScheduler)

        return mainThreadScheduler
    }

    object Hook {
        internal var onMainScheduler: Func1<Scheduler, Scheduler>? = null
        fun reset() {
            onMainScheduler = null
        }

        //Func1<Scheduler, Scheduler> onIOScheduler
        fun setOnMainScheduler(scheduler: Func1<Scheduler, Scheduler>) {
            onMainScheduler = scheduler
        }
    }
}
