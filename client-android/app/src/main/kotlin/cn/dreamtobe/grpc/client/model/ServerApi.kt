/*
 * This file is special, under LGPLv3 license.
 */

package cn.dreamtobe.grpc.client.model

import cn.dreamtobe.grpc.client.tools.Logger
import de.mkammerer.grpcchat.protocol.*
import io.grpc.ManagedChannelBuilder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


object ServerApi {

    class TokenMissingException : Exception("Token is missing. Call login() first")

    var PORT: Int = 5001
    var HOST: String = "10.15.128.171"

    private val mConnector: ChatGrpc.ChatBlockingStub
    private var mToken: String? = null
    private val mDateFormat: DateFormat
    private var mLoggedInUser: String? = null

    init {
        val channel = ManagedChannelBuilder.forAddress(HOST, PORT)
                .usePlaintext(true)
                .build()
        mConnector = ChatGrpc.newBlockingStub(channel)
        mDateFormat = SimpleDateFormat("MM-dd hh:mm:ss", Locale.CHINA)
    }


    fun createRoom(): CreateRoomResponse {
        if (mToken == null) throw TokenMissingException()

        val name = mDateFormat.format(Date())
        val request = CreateRoomRequest.newBuilder().setToken(mToken).setName(name).setDesc("create by $mLoggedInUser").build()
        val response = mConnector.createRoom(request)

        if (response.created) {
            Logger.log(javaClass, "Room created: $name")
        } else {
            Logger.log(javaClass, "Room creation failed: $name, error: ${response.error}")
        }

        return response
    }

    fun register(username: String, password: String): Boolean {
        val request = RegisterRequest.newBuilder().setUsername(username).setPassword(password).build()
        val response = mConnector.register(request)

        if (response.registered) {
            Logger.log(javaClass, "Register successful")
            mLoggedInUser = username
        } else {
            Logger.log(javaClass, "Register failed, error: ${response.error}")
            mLoggedInUser = null
        }

        return response.registered
    }

    fun loginOrRegister(username: String, password: String): LoginOrRegisterResponse {
        val request = LoginRequest.newBuilder().setUsername(username).setPassword(password).build()
        val response = mConnector.loginOrRegister(request)

        if (response.loggedIn) {
            mToken = response.token
            Logger.log(javaClass, "Login successful, token is $mToken")
            mLoggedInUser = username
        } else {
            Logger.log(javaClass, "Login failed, error: ${response.error}")
            mLoggedInUser = null
        }

        return response
    }

    fun login(username: String, password: String): Boolean {
        val request = LoginRequest.newBuilder().setUsername(username).setPassword(password).build()
        val response = mConnector.login(request)

        if (response.loggedIn) {
            mToken = response.token
            mLoggedInUser = username
            Logger.log(javaClass, "Login successful, token is $mToken")
        } else {
            Logger.log(javaClass, "Login failed, error: ${response.error}")
            mLoggedInUser = null
        }

        return response.loggedIn
    }

    fun listRooms(): ListRoomsResponse {
        if (mToken == null) throw TokenMissingException()

        val request = ListRoomsRequest.newBuilder().setToken(mToken).build()
        val response = mConnector.listRooms(request)

        if (response.error.code == Codes.SUCCESS) {
            Logger.log(javaClass, "Rooms on server:")
            response.roomsList.forEach { it -> Logger.log(javaClass, it.title) }
        } else {
            Logger.log(javaClass, "List rooms failed, error: ${response.error}")
        }

        return response
    }
}
