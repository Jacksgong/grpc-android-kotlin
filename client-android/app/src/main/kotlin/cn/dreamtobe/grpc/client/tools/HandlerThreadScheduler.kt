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

/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dreamtobe.grpc.client.tools

import android.os.Handler
import rx.Scheduler
import rx.Subscription
import rx.functions.Action0
import rx.internal.schedulers.ScheduledAction
import rx.subscriptions.CompositeSubscription
import rx.subscriptions.Subscriptions
import java.util.concurrent.TimeUnit

/**
 * Refer to RxAndroid
 */
class HandlerThreadScheduler(private val handler: Handler) : Scheduler() {

    override fun createWorker(): Scheduler.Worker {
        return InnerHandlerThreadScheduler(handler)
    }

    private class InnerHandlerThreadScheduler(private val handler: Handler) : Scheduler.Worker() {

        private val compositeSubscription = CompositeSubscription()

        override fun unsubscribe() {
            compositeSubscription.unsubscribe()
        }

        override fun isUnsubscribed(): Boolean {
            return compositeSubscription.isUnsubscribed
        }

        override fun schedule(action: Action0, delayTime: Long, unit: TimeUnit): Subscription {
            val scheduledAction = ScheduledAction(action)
            scheduledAction.add(Subscriptions.create { handler.removeCallbacks(scheduledAction) })
            scheduledAction.addParent(compositeSubscription)
            compositeSubscription.add(scheduledAction)

            handler.postDelayed(scheduledAction, unit.toMillis(delayTime))

            return scheduledAction
        }

        override fun schedule(action: Action0): Subscription {
            return schedule(action, 0, TimeUnit.MILLISECONDS)
        }

    }
}
