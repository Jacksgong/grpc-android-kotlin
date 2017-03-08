package cn.dreamtobe.grpc.client.presenter

import cn.dreamtobe.grpc.client.logic.Codes
import cn.dreamtobe.grpc.client.logic.ServerApi
import cn.dreamtobe.grpc.client.tools.AndroidSchedulers
import cn.dreamtobe.grpc.client.view.ConversationMvpView
import de.mkammerer.grpcchat.protocol.Error
import de.mkammerer.grpcchat.protocol.ListRoomsResponse
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by Jacksgong on 08/03/2017.
 */
class ConversationPresenter : Presenter<ConversationMvpView> {

    var view: ConversationMvpView? = null

    override fun attachView(view: ConversationMvpView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    fun createRoom() {
    }

    fun listRooms() {
        view?.loading()
        Observable.create(Observable.OnSubscribe<ListRoomsResponse> { subscriber ->
            try {
                subscriber.onNext(ServerApi.listRooms())
                subscriber.onCompleted()
            } catch (ex: Throwable) {
                subscriber.onError(ex)
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response ->
                            if (response.error.code == Codes.SUCCESS) {
                                view?.showConversations(response.roomsList)
                            } else {
                                view?.showError(response.error)
                            }
                        },

                        { e ->
                            view?.showError(Error.newBuilder().setCode(400).setMessage(e.toString()).build())
                        }
                )
    }
}