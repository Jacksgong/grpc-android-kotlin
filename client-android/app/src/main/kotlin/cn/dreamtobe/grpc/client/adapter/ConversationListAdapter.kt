package cn.dreamtobe.grpc.client.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.dreamtobe.grpc.client.R
import de.mkammerer.grpcchat.protocol.RoomMessage

/**
 * Created by Jacksgong on 08/03/2017.
 */
class ConversationListAdapter : RecyclerView.Adapter<ConversationListAdapter.ConversationListViewHolder>() {

    private var mConversationList = mutableListOf<RoomMessage>()
    private var mCallback: Callback? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ConversationListViewHolder {
        val itemView = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.item_conversation, parent, false)
        val viewHolder = ConversationListViewHolder(itemView)
        viewHolder.contentLayout.setOnClickListener {
            mCallback?.onItemClick(viewHolder.roomMessage)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ConversationListViewHolder, position: Int) {
        val roomMessage = mConversationList[position]
        holder.roomMessage = roomMessage

        holder.titleTv.text = roomMessage.title
        holder.descTv.text = roomMessage.desc
    }

    override fun getItemCount() = mConversationList.size

    class ConversationListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentLayout = itemView.findViewById(R.id.content_layout)!!
        val titleTv: TextView = itemView.findViewById(R.id.title_tv) as TextView
        val descTv: TextView = itemView.findViewById(R.id.desc_tv) as TextView

        lateinit var roomMessage : RoomMessage
    }

    interface Callback {
        fun onItemClick(roomMessage: RoomMessage)
    }
}