package cn.dreamtobe.grpc.client.presenter

/**
 * Created by Jacksgong on 08/03/2017.
 */
interface Presenter<in V> {
    fun attachView(view: V)
    fun detachView()
}