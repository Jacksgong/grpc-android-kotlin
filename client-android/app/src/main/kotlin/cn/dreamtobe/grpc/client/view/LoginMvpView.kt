package cn.dreamtobe.grpc.client.view

import android.app.LoaderManager
import de.mkammerer.grpcchat.protocol.Error

/**
 * Created by Jacksgong on 09/03/2017.
 */
interface LoginMvpView {
    fun showLoading()
    fun loggedIn(performedRegister: Boolean)
    fun showError(error: Error)
    fun showUserNameError(tipsId: Int)
    fun showPasswordError(tipsId: Int)
    fun resetError()
    fun addEmailsToAutoComplete(emailAddressCollection: List<String>)
    fun getLoaderManager(): LoaderManager
}