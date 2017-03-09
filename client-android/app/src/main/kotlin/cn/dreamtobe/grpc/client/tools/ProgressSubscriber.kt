package cn.dreamtobe.grpc.client.tools

import android.app.ProgressDialog
import android.content.Context
import rx.Subscriber

/**
 * Created by Jacksgong on 07/03/2017.
 */
open class ProgressSubscriber<T> : Subscriber<T> {

    val mProgressDialog: ProgressDialog

    constructor(context: Context) {
        mProgressDialog = ProgressDialog(context)
        mProgressDialog.setTitle("loading...")
    }

    override fun onError(e: Throwable?) {
        mProgressDialog.dismiss()
    }

    override fun onCompleted() {
        mProgressDialog.dismiss()
    }

    override fun onStart() {
        super.onStart()
        mProgressDialog.show()
    }

    override fun onNext(t: T) {
    }
}