# Grpc Android Kotlin

> Simple [GRPC](https://github.com/grpc/grpc-java) Server/Android written in kotlin, protobuf generated java files

- [中文文档](https://github.com/Jacksgong/grpc-android-kotlin/blob/master/README-zh.md)

## Kickoff

> Because of I'm running the server-side code on my testing-vps, If you just want to testing the Android, you can download and install the [release-v1.apk](https://raw.githubusercontent.com/Jacksgong/grpc-android-kotlin/master/arts/release-v1.apk) or the Android-side project, then set the Ip to `119.29.88.253` and set port to `5351` on the application, so you can test through touching the server-side code on my testing-vps, Have Fun!

#### Android

![](https://raw.githubusercontent.com/Jacksgong/grpc-android-kotlin/master/arts/demo.gif)

#### Back-end(Logs)

```
20:51:16.650 [main] INFO  de.mkammerer.grpcchat.server.Server - Server running on port 5001
20:51:32.803 [grpc-default-executor-0] INFO  de.mkammerer.grpcchat.server.Chat - jacks@dreamtobe.cn isn't exist, so register for it first
20:51:32.804 [grpc-default-executor-0] INFO  de.mkammerer.grpcchat.server.Chat - User jacks@dreamtobe.cn registered
20:51:32.807 [grpc-default-executor-0] INFO  de.mkammerer.grpcchat.server.Chat - User jacks@dreamtobe.cn logged in. Access token is qGNmE0/sBn3yO3scx1SRCA==
20:51:32.946 [grpc-default-executor-0] INFO  de.mkammerer.grpcchat.server.Chat - list rooms: 0
20:51:34.957 [grpc-default-executor-0] INFO  de.mkammerer.grpcchat.server.Chat - list rooms: 0
20:51:36.511 [grpc-default-executor-0] INFO  de.mkammerer.grpcchat.server.Chat - create room successfully
20:51:36.560 [grpc-default-executor-0] INFO  de.mkammerer.grpcchat.server.Chat - list rooms: 1
20:51:37.811 [grpc-default-executor-0] INFO  de.mkammerer.grpcchat.server.Chat - create room successfully
20:51:37.862 [grpc-default-executor-0] INFO  de.mkammerer.grpcchat.server.Chat - list rooms: 2
20:51:39.164 [grpc-default-executor-0] INFO  de.mkammerer.grpcchat.server.Chat - list rooms: 2
> Building 93% > :server:run
```

## Run

#### 1. Fetch the back-end codes([grpc-chat-kotlin](https://github.com/Jacksgong/grpc-chat-kotlin)):

```bash
git submodule init
git submodule update
```

#### 2. Run back-end codes

```bash
# generate protocol buffers for back-end
bash refresh-backend-proto.sh

# run server
bash run-server.sh
```

#### 3. Run Android codes

```bash
# run Android application
./gradlew installDebug
```

If your proto is changed or you android project has been cleaned don't forget refresh protocol buffers for Android manually(because compile-kotlin doesn't depence on compile-protocol, so we have to do that manually)

```bash
# generate protocol buffers for Android
bash refresh-android-proto.sh
```

If you want to run unit-test for Android project

```bash
# run unit-test on Android project
cd client-android
./gradlew test
```

## LICENSE

```
Copyright (C) 2017 Jacksgong(blog.dreamtobe.cn)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
