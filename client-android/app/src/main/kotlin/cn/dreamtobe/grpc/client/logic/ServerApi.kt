package cn.dreamtobe.grpc.client.logic

import cn.dreamtobe.grpc.client.tools.Logger
import de.mkammerer.grpcchat.protocol.*
import io.grpc.ManagedChannelBuilder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


object ServerApi {

    class TokenMissingException : Exception("Token is missing. Call login() first")

    var port: Int = 5001
    var host: String = "10.15.128.171"

    private val connector: ChatGrpc.ChatBlockingStub
    private var token: String? = null
    private val dateFormat: DateFormat
    private var loggedInUser : String? = null

    init {
        val channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true)
                .build()
        connector = ChatGrpc.newBlockingStub(channel)
        dateFormat = SimpleDateFormat("MM-dd hh:mm:ss", Locale.CHINA)
    }


    fun createRoom(): Boolean {
        if (token == null) throw TokenMissingException()

        val name = dateFormat.format(Date())
        val request = CreateRoomRequest.newBuilder().setToken(token).setName(name).setDesc("create by $loggedInUser").build()
        val response = connector.createRoom(request)

        if (response.created) {
            Logger.log(javaClass, "Room created: $name")
        } else {
            Logger.log(javaClass, "Room creation failed: $name, error: ${response.error}")
        }

        return response.created
    }

    fun register(username: String, password: String): Boolean {
        val request = RegisterRequest.newBuilder().setUsername(username).setPassword(password).build()
        val response = connector.register(request)

        if (response.registered) {
            Logger.log(javaClass, "Register successful")
            loggedInUser = username
        } else {
            Logger.log(javaClass, "Register failed, error: ${response.error}")
            loggedInUser = null
        }

        return response.registered
    }

    fun loginOrRegister(username: String, password: String): LoginOrRegisterResponse {
        val request = LoginRequest.newBuilder().setUsername(username).setPassword(password).build()
        val response = connector.loginOrRegister(request)

        if (response.loggedIn) {
            token = response.token
            Logger.log(javaClass, "Login successful, token is $token")
            loggedInUser = username
        } else {
            Logger.log(javaClass, "Login failed, error: ${response.error}")
            loggedInUser = null
        }

        return response
    }

    fun login(username: String, password: String): Boolean {
        val request = LoginRequest.newBuilder().setUsername(username).setPassword(password).build()
        val response = connector.login(request)

        if (response.loggedIn) {
            token = response.token
            loggedInUser = username
            Logger.log(javaClass, "Login successful, token is $token")
        } else {
            Logger.log(javaClass, "Login failed, error: ${response.error}")
            loggedInUser = null
        }

        return response.loggedIn
    }

    fun listRooms(): ListRoomsResponse {
        if (token == null) throw TokenMissingException()

        val request = ListRoomsRequest.newBuilder().setToken(token).build()
        val response = connector.listRooms(request)

        if (response.error.code == Codes.SUCCESS) {
            Logger.log(javaClass, "Rooms on server:")
            response.roomsList.forEach { it -> Logger.log(javaClass, it.title) }
        } else {
            Logger.log(javaClass, "List rooms failed, error: ${response.error}")
        }

        return response
    }
}
