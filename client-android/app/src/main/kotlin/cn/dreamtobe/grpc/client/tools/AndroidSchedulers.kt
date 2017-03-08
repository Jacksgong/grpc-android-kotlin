package cn.dreamtobe.grpc.client.tools

import android.os.Handler
import android.os.Looper
import rx.Scheduler
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

    fun mainThread() = HandlerThreadScheduler(Handler(Looper.getMainLooper()))
}
