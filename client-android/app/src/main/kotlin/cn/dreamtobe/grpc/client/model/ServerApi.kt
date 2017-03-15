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

package cn.dreamtobe.grpc.client.model

import cn.dreamtobe.grpc.client.tools.Logger
import de.mkammerer.grpcchat.protocol.*
import io.grpc.ManagedChannelBuilder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


interface ServerApi {

    fun getPort(): Int
    fun setPort(port: Int)
    fun getHost(): String
    fun setHost(host: String)

    fun createRoom(): CreateRoomResponse
    fun register(username: String, password: String): Boolean
    fun loginOrRegister(username: String, password: String): LoginOrRegisterResponse
    fun login(username: String, password: String): Boolean
    fun listRooms(): ListRoomsResponse

    class TokenMissingException : Exception("Token is missing. Call login() first")

    class Channel {
        internal var port: Int = 5351
        internal var host: String = "119.29.88.253"

        private var connector: ChatGrpc.ChatBlockingStub? = null

        fun setHost(host: String) {
            if (host != this.host) {
                this.host = host
                reset()
            }
        }

        fun setPort(port: Int) {
            if (port != this.port) {
                this.port = port
                reset()
            }
        }

        private fun reset() {
            connector = null
        }

        internal fun stub(): ChatGrpc.ChatBlockingStub {
            if (connector == null) {
                val channel = ManagedChannelBuilder.forAddress(host, port)
                        .usePlaintext(true)
                        .build()
                connector = ChatGrpc.newBlockingStub(channel)
            }
            return connector!!
        }

    }

    class Factory {
        companion object {
            fun create(): ServerApi {
                return object : ServerApi {


                    private var mToken: String? = null
                    private val mDateFormat: DateFormat
                    private var mLoggedInUser: String? = null
                    private val mChannel: Channel = Channel()

                    init {
                        mDateFormat = SimpleDateFormat("MM-dd hh:mm:ss", Locale.CHINA)
                    }

                    override fun getPort() = mChannel.port
                    override fun setPort(port: Int) {
                        mChannel.setPort(port)
                    }

                    override fun getHost() = mChannel.host
                    override fun setHost(host: String) {
                        mChannel.setHost(host)
                    }

                    override fun createRoom(): CreateRoomResponse {
                        if (mToken == null) throw TokenMissingException()

                        val name = mDateFormat.format(Date())
                        val request = CreateRoomRequest.newBuilder().setToken(mToken).setName(name).setDesc("create by $mLoggedInUser").build()
                        val response = mChannel.stub().createRoom(request)

                        if (response.created) {
                            Logger.log(javaClass, "Room created: $name")
                        } else {
                            Logger.log(javaClass, "Room creation failed: $name, error: ${response.error}")
                        }

                        return response
                    }

                    override fun register(username: String, password: String): Boolean {
                        val request = RegisterRequest.newBuilder().setUsername(username).setPassword(password).build()
                        val response = mChannel.stub().register(request)

                        if (response.registered) {
                            Logger.log(javaClass, "Register successful")
                            mLoggedInUser = username
                        } else {
                            Logger.log(javaClass, "Register failed, error: ${response.error}")
                            mLoggedInUser = null
                        }

                        return response.registered
                    }

                    override fun loginOrRegister(username: String, password: String): LoginOrRegisterResponse {
                        val request = LoginRequest.newBuilder().setUsername(username).setPassword(password).build()
                        val response = mChannel.stub().loginOrRegister(request)

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

                    override fun login(username: String, password: String): Boolean {
                        val request = LoginRequest.newBuilder().setUsername(username).setPassword(password).build()
                        val response = mChannel.stub().login(request)

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

                    override fun listRooms(): ListRoomsResponse {
                        if (mToken == null) throw TokenMissingException()

                        val request = ListRoomsRequest.newBuilder().setToken(mToken).build()
                        val response = mChannel.stub().listRooms(request)

                        if (response.error.code == Codes.SUCCESS) {
                            Logger.log(javaClass, "Rooms on server:")
                            response.roomsList.forEach { it -> Logger.log(javaClass, it.title) }
                        } else {
                            Logger.log(javaClass, "List rooms failed, error: ${response.error}")
                        }

                        return response
                    }
                }
            }

        }
    }
}
