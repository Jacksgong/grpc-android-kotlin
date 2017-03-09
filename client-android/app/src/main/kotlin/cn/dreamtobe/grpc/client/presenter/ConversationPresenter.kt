package cn.dreamtobe.grpc.client.presenter

import android.content.Context
import cn.dreamtobe.grpc.client.logic.Codes
import cn.dreamtobe.grpc.client.logic.ServerApi
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
class ConversationPresenter(private var mContext: Context?) : Presenter<ConversationMvpView> {

    private var mView: ConversationMvpView? = null

    override fun attachView(view: ConversationMvpView) {
        mView = view
    }

    override fun detachView() {
        mView = null
        mContext = null
    }

    fun createRoom() {
        this.mView ?: return

        Observable.create(Observable.OnSubscribe<CreateRoomResponse> { subscriber ->
            try {
                subscriber.onNext(ServerApi.createRoom())
                subscriber.onCompleted()
            } catch (ex: Throwable) {
                subscriber.onError(ex)
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressSubscriber<CreateRoomResponse>(mContext!!) {
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