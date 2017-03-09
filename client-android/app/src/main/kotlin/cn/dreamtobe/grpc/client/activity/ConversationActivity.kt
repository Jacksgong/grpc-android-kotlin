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

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import cn.dreamtobe.grpc.client.R
import cn.dreamtobe.grpc.client.adapter.ConversationListAdapter
import cn.dreamtobe.grpc.client.presenter.ConversationPresenter
import cn.dreamtobe.grpc.client.tools.Logger
import cn.dreamtobe.grpc.client.view.ConversationMvpView
import de.mkammerer.grpcchat.protocol.Error
import de.mkammerer.grpcchat.protocol.RoomMessage

/**
 * Created by Jacksgong on 07/03/2017.
 */
class ConversationActivity : AppCompatActivity(), ConversationMvpView {
    override fun getContext(): Context {
        return this
    }

    private lateinit var mToolbar: Toolbar

    private lateinit var mPresenter: ConversationPresenter
    private lateinit var mProgressView: ProgressBar
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ConversationListAdapter
    private lateinit var mRefreshBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_conversation)

        mPresenter = ConversationPresenter()
        mPresenter.attachView(this)

        mToolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Conversation"

        mProgressView = findViewById(R.id.progressBar) as ProgressBar
        mRecyclerView = findViewById(R.id.recycler_view) as RecyclerView
        val adapter = ConversationListAdapter()
        adapter.callback = object : ConversationListAdapter.Callback {
            override fun onItemClick(roomMessage: RoomMessage) {
                Snackbar.make(mRecyclerView, "jump to chat page: ${roomMessage.title}, ${roomMessage.desc}", Snackbar.LENGTH_LONG).show()
            }
        }
        this.mAdapter = adapter
        mRecyclerView.adapter = adapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mRefreshBtn = findViewById(R.id.refresh_btn) as Button
        mRefreshBtn.setOnClickListener { mPresenter.listRooms() }
    }

    override fun onStart() {
        super.onStart()
        mPresenter.listRooms()
    }

    override fun onDestroy() {
        mPresenter.detachView()
        super.onDestroy()
    }

    override fun showLoading() {
        mRecyclerView.visibility = View.GONE
        mProgressView.visibility = View.VISIBLE
        mRefreshBtn.visibility = View.GONE
    }

    override fun showError(error: Error) {
        mRefreshBtn.visibility = View.VISIBLE
        mProgressView.visibility = View.GONE
        Snackbar.make(mRecyclerView, "occur error code: ${error.code} message: ${error.message}",
                Snackbar.LENGTH_LONG).show()
        Logger.log(javaClass, error.message)
    }

    override fun showConversations(roomMessageList: List<RoomMessage>) {
        mRefreshBtn.visibility = View.GONE
        mRecyclerView.visibility = View.VISIBLE
        mProgressView.visibility = View.GONE

        mAdapter.conversationList = roomMessageList.toMutableList()
        mAdapter.notifyDataSetChanged()
        mRecyclerView.requestFocus()

        Snackbar.make(mRecyclerView, "load ${roomMessageList.size} rooms from server",
                Snackbar.LENGTH_LONG).show()
    }

    override fun createdNewRoom() {
        Snackbar.make(mRecyclerView, "create one new room",
                Snackbar.LENGTH_LONG).show()
        mPresenter.listRooms()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_refresh -> mPresenter.listRooms()
            R.id.menu_create -> mPresenter.createRoom()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_conversation, menu)
        return super.onCreateOptionsMenu(menu)
    }

}