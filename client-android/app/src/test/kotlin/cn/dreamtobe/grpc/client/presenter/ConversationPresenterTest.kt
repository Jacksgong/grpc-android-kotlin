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
import cn.dreamtobe.grpc.client.model.Codes
import cn.dreamtobe.grpc.client.view.ConversationMvpView
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import de.mkammerer.grpcchat.protocol.CreateRoomResponse
import de.mkammerer.grpcchat.protocol.Error
import de.mkammerer.grpcchat.protocol.ListRoomsResponse
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Jacksgong on 09/03/2017.
 */
class ConversationPresenterTest : GrpcClientTest<ConversationMvpView, ConversationPresenter>() {

    @Before
    fun setup() {
        create<ConversationMvpView, ConversationPresenter>()
    }

    @After
    fun tearDown() {
        destroy()
    }

    @Test
    fun createRoom_Success_invokeCreatedNewRoom() {
        whenever(serverApi.createRoom()).thenReturn(successCreateRoomResponse)
        presenter.createRoom()
        verify(mvpView).createdNewRoom()
    }

    @Test
    fun createRoom_Error_invokeShowError() {
        whenever(serverApi.createRoom()).thenReturn(errorCreateRoomResponse)
        presenter.createRoom()
        verify(mvpView).showError(mockError)
    }

    @Test
    fun listRooms_Success_invokeShowConversations() {
        whenever(serverApi.listRooms()).thenReturn(successListRoomsResponse)
        presenter.listRooms()
        verify(mvpView).showLoading()
        verify(mvpView).showConversations(any())
    }

    @Test
    fun listRooms_Error_invokeShowError() {
        whenever(serverApi.listRooms()).thenReturn(errorListRoomsResponse)
        presenter.listRooms()
        verify(mvpView).showLoading()
        verify(mvpView).showError(mockFailed)
    }

    companion object {
        val successCreateRoomResponse =
                CreateRoomResponse.newBuilder()
                        .setCreated(true)
                        // we have to declare type cast, if not, it will raise Type mismatch
                        .build() as CreateRoomResponse

        val mockError = mock<Error>()
        val errorCreateRoomResponse =
                CreateRoomResponse.newBuilder()
                        .setCreated(false)
                        .setError(mockError)
                        // we have to declare type cast, if not, it will raise Type mismatch
                        .build() as CreateRoomResponse

        val mockSuccess = Error.newBuilder().setCode(Codes.SUCCESS).build() as Error
        val mockFailed = Error.newBuilder().setCode(500).build() as Error
        val successListRoomsResponse =
                ListRoomsResponse.newBuilder()
                        .setError(mockSuccess)
                        .build() as ListRoomsResponse
        val errorListRoomsResponse =
                ListRoomsResponse.newBuilder()
                        .setError(mockFailed)
                        .build() as ListRoomsResponse
    }
}

