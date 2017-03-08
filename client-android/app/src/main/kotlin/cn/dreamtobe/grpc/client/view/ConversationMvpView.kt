package cn.dreamtobe.grpc.client.view

import de.mkammerer.grpcchat.protocol.Error
import de.mkammerer.grpcchat.protocol.RoomMessage

/**
 * Created by Jacksgong on 08/03/2017.
 */
interface ConversationMvpView {
    fun loading()
    fun showConversations(roomMessageList: List<RoomMessage>)
    fun showError(error: Error)
}