package cn.dreamtobe.grpc.client.logic

import de.mkammerer.grpcchat.protocol.*
import io.grpc.ManagedChannelBuilder



class TokenMissingException : Exception("Token is missing. Call login() first")

object ServerApi {

    var port: Int = 5001
    var host: String = "10.15.128.171"

    private val logger = Logger(javaClass)
    private val connector: ChatGrpc.ChatBlockingStub
    private var token: String? = null

    init {
        val channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true)
                .build()
        connector = ChatGrpc.newBlockingStub(channel)
    }

    fun createRoom() {
        if (token == null) throw TokenMissingException()

        val request = CreateRoomRequest.newBuilder().setToken(token).setName("Room #1").build()
        val response = connector.createRoom(request)

        if (response.created) {
            logger.info("Room created")
        } else {
            logger.info("Room creation failed, error: ${response.error}")
        }
    }

    fun register(username: String, password: String) {
        val request = RegisterRequest.newBuilder().setUsername(username).setPassword(password).build()
        val response = connector.register(request)

        if (response.registered) {
            logger.info("Register successful")
        } else {
            logger.info("Register failed, error: ${response.error}")
        }
    }

    fun login(username: String, password: String) : Boolean{
        val request = LoginRequest.newBuilder().setUsername(username).setPassword(password).build()
        val response = connector.login(request)

        if (response.loggedIn) {
            token = response.token
            logger.info("Login successful, token is $token")
            return true
        } else {
            logger.info("Login failed, error: ${response.error}")
            return false
        }
    }

    fun listRooms() {
        if (token == null) throw TokenMissingException()

        val request = ListRoomsRequest.newBuilder().setToken(token).build()
        val response = connector.listRooms(request)

        if (response.error.code == Codes.SUCCESS) {
            logger.info("Rooms on server:")
            response.roomsList.forEach { it -> logger.info(it) }
        } else {
            logger.info("List rooms failed, error: ${response.error}")
        }
    }
}
