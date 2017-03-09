package cn.dreamtobe.grpc.client.presenter

import android.content.Context
import cn.dreamtobe.grpc.client.logic.Codes
import cn.dreamtobe.grpc.client.logic.ServerApi
import cn.dreamtobe.grpc.client.tools.AndroidSchedulers
import cn.dreamtobe.grpc.client.tools.ProgressSubscriber
import cn.dreamtobe.grpc.client.view.ConversationMvpView
import de.mkammerer.grpcchat.protocol.Error
import de.mkammerer.grpcchat.protocol.ListRoomsResponse
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by Jacksgong on 08/03/2017.
 */
class ConversationPresenter(var context: Context?) : Presenter<ConversationMvpView> {

    var mView: ConversationMvpView? = null

    override fun attachView(view: ConversationMvpView) {
        this.mView = view
    }

    override fun detachView() {
        this.mView = null
        this.context = null
    }

    fun createRoom() {
        this.mView ?: return

        Observable.create(Observable.OnSubscribe<Boolean> { subscriber ->
            try {
                subscriber.onNext(ServerApi.createRoom())
                subscriber.onCompleted()
            } catch (ex: Throwable) {
                subscriber.onError(ex)
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressSubscriber<Boolean>(context!!) {
                    override fun onNext(t: Boolean) {
                        super.onNext(t)
                        mView?.createdNewRoom()
                    }

                    override fun onError(e: Throwable?) {
                        super.onError(e)
                        mView?.showError(Error.newBuilder().setCode(400).setMessage(e.toString()).build())
                    }
                })
    }

    fun listRooms() {
        this.mView ?: return

        mView?.loading()
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