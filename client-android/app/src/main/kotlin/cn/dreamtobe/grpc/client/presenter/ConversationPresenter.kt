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

import cn.dreamtobe.grpc.client.GrpcClientApplication
import cn.dreamtobe.grpc.client.model.Codes
import cn.dreamtobe.grpc.client.model.ServerApi
import cn.dreamtobe.grpc.client.tools.AndroidSchedulers
import cn.dreamtobe.grpc.client.tools.ProgressSubscriber
import cn.dreamtobe.grpc.client.view.ConversationMvpView
import de.mkammerer.grpcchat.protocol.CreateRoomResponse
import de.mkammerer.grpcchat.protocol.Error
import de.mkammerer.grpcchat.protocol.ListRoomsResponse
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by Jacksgong on 08/03/2017.
 */
class ConversationPresenter : Presenter<ConversationMvpView> {

    private var mView: ConversationMvpView? = null
    private lateinit var mServerApi: ServerApi

    override fun attachView(view: ConversationMvpView) {
        mView = view
        mServerApi = GrpcClientApplication.get(view.getContext()).getServerApi()
    }

    override fun detachView() {
        mView = null
    }

    fun createRoom() {
        this.mView ?: return

        Observable.create(Observable.OnSubscribe<CreateRoomResponse> { subscriber ->
            try {
                subscriber.onNext(mServerApi.createRoom())
                subscriber.onCompleted()
            } catch (ex: Throwable) {
                subscriber.onError(ex)
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressSubscriber<CreateRoomResponse>(mView!!.getContext()) {
                    override fun onNext(response: CreateRoomResponse) {
                        super.onNext(response)
                        if (response.created) {
                            mView?.createdNewRoom()
                        } else {
                            mView?.showError(response.error)
                        }
                    }

                    override fun onError(e: Throwable?) {
                        super.onError(e)
                        mView?.showError(Error.newBuilder().setCode(Codes.LOCAL_ERROR).setMessage(e.toString()).build())
                    }
                })
    }

    fun listRooms() {
        this.mView ?: return

        mView?.showLoading()
        Observable.create(Observable.OnSubscribe<ListRoomsResponse> { subscriber ->
            try {
                subscriber.onNext(mServerApi.listRooms())
                subscriber.onCompleted()
            } catch (ex: Throwable) {
                subscriber.onError(ex)
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response ->
                            if (response.error.code == Codes.SUCCESS) {
                                mView?.showConversations(response.roomsList)
                            } else {
                                mView?.showError(response.error)
                            }
                        },

                        { e ->
                            mView?.showError(Error.newBuilder().setCode(400).setMessage(e.toString()).build())
                        }
                )
    }
}