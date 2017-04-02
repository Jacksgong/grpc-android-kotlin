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

package cn.dreamtobe.grpc.client

import cn.dreamtobe.grpc.client.model.ServerApi
import cn.dreamtobe.grpc.client.presenter.Presenter
import cn.dreamtobe.grpc.client.tools.AndroidSchedulers
import cn.dreamtobe.grpc.client.view.MvpView
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.robolectric.RuntimeEnvironment
import rx.functions.Func1
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers

/**
 * Created by Jacksgong on 09/03/2017.
 */
abstract class GrpcClientTest<V : MvpView, P : Presenter<V>> : AndroidTest() {

    lateinit var presenter: P
    lateinit var mvpView: V
    lateinit var serverApi: ServerApi

    inline fun <reified VR : V, reified PR : P> create() {
        val application = RuntimeEnvironment.application as GrpcClientApplication

        serverApi = mock()
        application.setServerApi(serverApi)

        presenter = PR::class.java.newInstance()
        mvpView = mock<VR> {
            on { getContext() } doReturn application
        }
        presenter.attachView(mvpView)

        RxJavaHooks.reset()
        RxJavaHooks.setOnIOScheduler { Schedulers.immediate() }
        AndroidSchedulers.Hook.setOnMainScheduler(Func1 { Schedulers.immediate() })

    }

    fun destroy() {
        presenter.detachView()

        RxJavaHooks.reset()
        AndroidSchedulers.Hook.reset()
    }
}
