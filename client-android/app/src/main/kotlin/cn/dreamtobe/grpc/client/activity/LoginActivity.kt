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

package cn.dreamtobe.grpc.client.activity

import android.Manifest.permission.READ_CONTACTS
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import cn.dreamtobe.grpc.client.R
import cn.dreamtobe.grpc.client.presenter.LoginPresenter
import cn.dreamtobe.grpc.client.view.LoginMvpView
import de.mkammerer.grpcchat.protocol.Error

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoginMvpView {
    override fun getContext(): Context {
        return this
    }

    // UI references.
    private lateinit var mUserNameView: AutoCompleteTextView
    private lateinit var mPasswordView: EditText
    private lateinit var mProgressView: View
    private lateinit var mLoginFormView: View

    private lateinit var mPresenter: LoginPresenter

    // the mock value isn't coupling with back-end logic
    private val mMockUserName = "jacks@dreamtobe.cn"
    private val mMockPassword = "dreamtobe"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Login"
        setContentView(R.layout.activity_login)

        mPresenter = LoginPresenter()
        mPresenter.attachView(this)

        // Set up the login form.
        mUserNameView = findViewById(R.id.username) as AutoCompleteTextView
        mUserNameView.setText(mMockUserName)
        populateAutoComplete()

        mPasswordView = findViewById(R.id.password) as EditText
        mPasswordView.setText(mMockPassword)
        mPasswordView.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                mPresenter.attemptLoginOrRegister(mUserNameView.text.toString(), mPasswordView.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        val signInOrRegisterBtn = findViewById(R.id.sign_in_or_register_btn) as Button
        signInOrRegisterBtn.setOnClickListener {
            mPresenter.attemptLoginOrRegister(mUserNameView.text.toString(), mPasswordView.text.toString())
        }

        mLoginFormView = findViewById(R.id.login_form)
        mProgressView = findViewById(R.id.login_progress)
    }

    override fun onDestroy() {
        mPresenter.detachView()
        super.onDestroy()
    }

    override fun resetError() {
        mUserNameView.error = null
        mPasswordView.error = null
    }

    override fun showPasswordError(tipsId: Int) {
        mPasswordView.error = getString(tipsId)
        mPasswordView.requestFocus()
    }

    override fun showUserNameError(tipsId: Int) {
        mUserNameView.error = getString(tipsId)
        mUserNameView.requestFocus()
    }

    override fun showLoading() {
        showProgress(true)
    }

    override fun loggedIn(performedRegister: Boolean) {
        showProgress(false)
        Snackbar.make(mLoginFormView, "complete login with performed register: $performedRegister",
                Snackbar.LENGTH_LONG).show()
        startActivity(Intent(this, ConversationActivity::class.java))
        finish()
    }

    override fun showError(error: Error) {
        showProgress(false)
        Snackbar.make(mLoginFormView, "request loginOrRegister error: ${error.code} with ${error.message}",
                Snackbar.LENGTH_LONG).show()
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

            mLoginFormView.visibility = if (show) View.GONE else View.VISIBLE
            mLoginFormView.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mLoginFormView.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

            mProgressView.visibility = if (show) View.VISIBLE else View.GONE
            mProgressView.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mProgressView.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.visibility = if (show) View.VISIBLE else View.GONE
            mLoginFormView.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(this@LoginActivity,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        mUserNameView.setAdapter(adapter)
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }
    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }

        loaderManager.initLoader(0, Bundle(), mPresenter)
    }

    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mUserNameView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) { requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) }
        } else {
            requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }


    companion object {

        /**
         * Id to identity READ_CONTACTS permission request.
         */
        private val REQUEST_READ_CONTACTS = 0
    }
}

