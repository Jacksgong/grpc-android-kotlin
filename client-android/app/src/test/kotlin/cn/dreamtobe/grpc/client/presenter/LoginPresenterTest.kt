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

import cn.dreamtobe.grpc.client.GrpcClientTest
import cn.dreamtobe.grpc.client.R
import cn.dreamtobe.grpc.client.view.LoginMvpView
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import de.mkammerer.grpcchat.protocol.Error
import de.mkammerer.grpcchat.protocol.LoginOrRegisterResponse
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

/**
 * Created by Jacksgong on 09/03/2017.
 */
class LoginPresenterTest : GrpcClientTest<LoginMvpView, LoginPresenter>() {


    @Before
    fun setup() {
        create<LoginMvpView, LoginPresenter>()
    }

    @After
    fun tearDown() {
        destroy()
    }

    @Test
    fun attemptLoginOrRegister_success_invokeLoggedIn() {
        whenever(serverApi.loginOrRegister(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(errorLoginOrRegisterResponse)
        presenter.attemptLoginOrRegister(correctUserName, correctPassword)
        verify(mvpView).showLoading()
        verify(mvpView).showError(mockError)
    }

    @Test
    fun attemptLoginOrRegister_error_invokeShowError() {
        whenever(serverApi.loginOrRegister(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(correctLoginOrRegisterResponse)
        presenter.attemptLoginOrRegister(correctUserName, correctPassword)
        verify(mvpView).showLoading()
        verify(mvpView).loggedIn(true)
    }

    @Test
    fun attemptLoginOrRegister_InvalidParams_invokeError() {
        presenter.attemptLoginOrRegister(invalidUserName, correctPassword)
        verify(mvpView).showUserNameError(R.string.error_invalid_email)

        presenter.attemptLoginOrRegister(correctUserName, shortPassword)
        verify(mvpView).showPasswordError(R.string.error_invalid_password)

        presenter.attemptLoginOrRegister(emptyUserName, correctPassword)
        verify(mvpView).showUserNameError(R.string.error_field_required)

        presenter.attemptLoginOrRegister(correctUserName, emptyPassword)
        verify(mvpView).showPasswordError(R.string.error_field_required)
    }

    companion object {
        val emptyUserName = ""
        val emptyPassword = ""
        val invalidUserName = "12"
        val shortPassword = "1234"
        val correctUserName = "abc@iiii.com"
        val correctPassword = "12345"

        val correctLoginOrRegisterResponse =
                LoginOrRegisterResponse.newBuilder()
                        .setPerformedRegister(true)
                        .setToken("tempToken")
                        .setLoggedIn(true)
                        // we have to declare type cast, if not, it will raise Type mismatch
                        .build() as LoginOrRegisterResponse

        val mockError = mock<Error>()
        val errorLoginOrRegisterResponse =
                LoginOrRegisterResponse.newBuilder()
                        .setError(mockError)
                        .build() as LoginOrRegisterResponse
    }


}