package cn.dreamtobe.grpc.client.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import cn.dreamtobe.grpc.client.R
import cn.dreamtobe.grpc.client.adapter.ConversationListAdapter
import cn.dreamtobe.grpc.client.presenter.ConversationPresenter
import cn.dreamtobe.grpc.client.view.ConversationMvpView
import de.mkammerer.grpcchat.protocol.Error
import de.mkammerer.grpcchat.protocol.RoomMessage

/**
 * Created by Jacksgong on 07/03/2017.
 */
class ConversationActivity : AppCompatActivity(), ConversationMvpView {


    lateinit var presenter: ConversationPresenter
    lateinit var progressView: ProgressBar
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ConversationListAdapter
    lateinit var refreshBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Conversation"
        setContentView(R.layout.activity_conversation)

        presenter = ConversationPresenter()
        presenter.attachView(this)

        progressView = findViewById(R.id.progressBar) as ProgressBar
        recyclerView = findViewById(R.id.recycler_view) as RecyclerView
        val adapter = ConversationListAdapter()
        adapter.callback = object : ConversationListAdapter.Callback {
            override fun onItemClick(roomMessage: RoomMessage) {
                Snackbar.make(recyclerView, "jump to chat page: ${roomMessage.title}, ${roomMessage.desc}", Snackbar.LENGTH_LONG).show()
            }
        }
        this.adapter = adapter
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        refreshBtn = findViewById(R.id.refresh_btn) as Button
        refreshBtn.setOnClickListener { presenter.listRooms() }
    }

    override fun onStart() {
        super.onStart()
        presenter.listRooms()
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun loading() {
        recyclerView.visibility = View.GONE
        progressView.visibility = View.VISIBLE
        refreshBtn.visibility = View.GONE
    }

    override fun showError(error: Error) {
        refreshBtn.visibility = View.VISIBLE
        progressView.visibility = View.GONE
        Snackbar.make(recyclerView, "occur error code: ${error.code} message: ${error.message}",
                Snackbar.LENGTH_LONG).show()
    }

    override fun showConversations(roomMessageList: List<RoomMessage>) {
        refreshBtn.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        progressView.visibility = View.GONE

        adapter.conversationList = roomMessageList.toMutableList()
        adapter.notifyDataSetChanged()
        recyclerView.requestFocus()

        Snackbar.make(recyclerView, "load ${roomMessageList.size} rooms from server",
                Snackbar.LENGTH_LONG).show()
    }

}