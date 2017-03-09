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

package cn.dreamtobe.grpc.client

import android.app.Application
import android.content.Context
import cn.dreamtobe.grpc.client.model.ServerApi

/**
 * Created by Jacksgong on 09/03/2017.
 */
class GrpcClientApplication : Application() {
    private var mServerApi: ServerApi? = null

    fun getServerApi(): ServerApi {
        if (mServerApi == null) mServerApi = ServerApi.Factory.create()
        return mServerApi!!
    }

    fun setServerApi(serverApi: ServerApi) {
        mServerApi = serverApi
    }

    companion object {
        fun get(context: Context): GrpcClientApplication = context.applicationContext as GrpcClientApplication
    }

}