package cn.dreamtobe.grpc.client.tools

import android.app.ProgressDialog
import android.content.Context
import rx.Subscriber

/**
 * Created by Jacksgong on 07/03/2017.
 */
open class ProgressSubscriber<T> : Subscriber<T> {

    val progressDialog: ProgressDialog

    constructor(context: Context) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("loading...")
    }

    override fun onError(e: Throwable?) {
        progressDialog.dismiss()
    }

    override fun onCompleted() {
        progressDialog.dismiss()
    }

    override fun onStart() {
        super.onStart()
        progressDialog.show()
    }

    override fun onNext(t: T) {
    }
}