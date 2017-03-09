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

package cn.dreamtobe.grpc.client.presenter

import cn.dreamtobe.grpc.client.AndroidTest
import cn.dreamtobe.grpc.client.GrpcClientApplication
import cn.dreamtobe.grpc.client.R
import cn.dreamtobe.grpc.client.model.ServerApi
import cn.dreamtobe.grpc.client.tools.AndroidSchedulers
import cn.dreamtobe.grpc.client.view.LoginMvpView
import com.nhaarman.mockito_kotlin.*
import de.mkammerer.grpcchat.protocol.Error
import de.mkammerer.grpcchat.protocol.LoginOrRegisterResponse
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.robolectric.RuntimeEnvironment
import rx.functions.Func1
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers

/**
 * Created by Jacksgong on 09/03/2017.
 */
class LoginPresenterTest : AndroidTest() {

    lateinit var loginPresenter: LoginPresenter
    lateinit var loginMvpView: LoginMvpView
    lateinit var serverApi: ServerApi

    @Before
    fun setup() {
        val application = RuntimeEnvironment.application as GrpcClientApplication

        serverApi = mock()
        application.setServerApi(serverApi)

        loginPresenter = LoginPresenter()
        loginMvpView = mock {
            on { getContext() } doReturn application
        }
        loginPresenter.attachView(loginMvpView)
    }

    @After
    fun tearDown() {
        loginPresenter.detachView()
    }

    @Test
    fun attemptLoginOrRegisterInvalidParamsRaiseError() {
        val emptyUserName = ""
        val emptyPassword = ""
        val invalidUserName = "12"
        val shortPassword = "1234"
        val correctUserName = "abc@iiii.com"
        val correctPassword = "12345"

        loginPresenter.attemptLoginOrRegister(invalidUserName, correctPassword)
        verify(loginMvpView).showUserNameError(R.string.error_invalid_email)

        loginPresenter.attemptLoginOrRegister(correctUserName, shortPassword)
        verify(loginMvpView).showPasswordError(R.string.error_invalid_password)

        loginPresenter.attemptLoginOrRegister(emptyUserName, correctPassword)
        verify(loginMvpView).showUserNameError(R.string.error_field_required)

        loginPresenter.attemptLoginOrRegister(correctUserName, emptyPassword)
        verify(loginMvpView).showPasswordError(R.string.error_field_required)

        RxJavaHooks.reset()
        RxJavaHooks.setOnIOScheduler { Schedulers.immediate() }
        AndroidSchedulers.Hook.setOnMainScheduler(Func1 { Schedulers.immediate() })

        whenever(serverApi.loginOrRegister(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(CORRECT_LOGIN_OR_REGISTER_RESPONSE)
        loginPresenter.attemptLoginOrRegister(correctUserName, correctPassword)
        verify(loginMvpView).showLoading()
        verify(loginMvpView).loggedIn(true)

        whenever(serverApi.loginOrRegister(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(ERROR_LOGIN_OR_REGISTER_RESPONSE)
        loginPresenter.attemptLoginOrRegister(correctUserName, correctPassword)
        verify(loginMvpView, times(2)).showLoading()
        verify(loginMvpView).showError(MOCK_ERROR)

        RxJavaHooks.reset()
        AndroidSchedulers.Hook.reset()
    }

    companion object {
        val CORRECT_LOGIN_OR_REGISTER_RESPONSE =
                LoginOrRegisterResponse.newBuilder()
                        .setPerformedRegister(true)
                        .setToken("tempToken")
                        .setLoggedIn(true)
                        // we have to declare type cast, if not, it will raise Type mismatch
                        .build() as LoginOrRegisterResponse

        val MOCK_ERROR = mock<Error>()
        val ERROR_LOGIN_OR_REGISTER_RESPONSE =
                LoginOrRegisterResponse.newBuilder()
                        .setError(MOCK_ERROR)
                        .build() as LoginOrRegisterResponse
    }


}