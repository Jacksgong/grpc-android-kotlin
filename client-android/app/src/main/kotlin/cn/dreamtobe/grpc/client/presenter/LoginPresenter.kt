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

import android.app.LoaderManager
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import cn.dreamtobe.grpc.client.GrpcClientApplication
import cn.dreamtobe.grpc.client.R
import cn.dreamtobe.grpc.client.model.Codes
import cn.dreamtobe.grpc.client.model.ServerApi
import cn.dreamtobe.grpc.client.tools.AndroidSchedulers
import cn.dreamtobe.grpc.client.view.LoginMvpView
import de.mkammerer.grpcchat.protocol.Error
import de.mkammerer.grpcchat.protocol.LoginOrRegisterResponse
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by Jacksgong on 09/03/2017.
 */
class LoginPresenter : Presenter<LoginMvpView>, LoaderManager.LoaderCallbacks<Cursor> {

    private var mView: LoginMvpView? = null
    private lateinit var mServerApi: ServerApi

    override fun attachView(view: LoginMvpView) {
        mView = view
        mServerApi = GrpcClientApplication.get(view.getContext()).getServerApi()
    }

    override fun detachView() {
        mView = null
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    fun attemptLoginOrRegister(username: String, password: String) {
        mView?.resetError()

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mView?.showPasswordError(R.string.error_invalid_password)
            return
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mView?.showUserNameError(R.string.error_field_required)
            return
        }

        if (!isEmailValid(username)) {
            mView?.showUserNameError(R.string.error_invalid_email)
            return
        }

        mView?.showLoading()
        Observable.create(Observable.OnSubscribe<LoginOrRegisterResponse> { subscriber ->
            try {
                subscriber.onNext(mServerApi.loginOrRegister(username, password))
                subscriber.onCompleted()
            } catch (ex: Throwable) {
                subscriber.onError(ex)
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response ->
                            if (response.loggedIn) {
                                mView?.loggedIn(response.performedRegister)
                            } else {
                                mView?.showError(response.error)
                            }
                        },

                        { e ->
                            mView?.showError(Error.newBuilder().setCode(Codes.LOCAL_ERROR).setMessage(e.toString()).build())
                        }
                )
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>?, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        mView?.addEmailsToAutoComplete(emails)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(mView!!.getContext(),
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    private interface ProfileQuery {
        companion object {
            val PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY)

            val ADDRESS = 0
            val IS_PRIMARY = 1
        }
    }
}