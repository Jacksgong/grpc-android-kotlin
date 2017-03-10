# Grpc Android Kotlin

> 通过[GRPC](https://github.com/grpc/grpc-java)，包含后台，Android端，都是用kotlin编写。

- [English](https://github.com/Jacksgong/grpc-android-kotlin)

## 快速预览

#### Android

![](https://raw.githubusercontent.com/Jacksgong/grpc-android-kotlin/master/arts/demo.gif)

#### 后端(日志)

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

## 运行

#### 1. 获取后端代码([grpc-chat-kotlin](https://github.com/Jacksgong/grpc-chat-kotlin)):

```bash
git submodule init
git submodule update
```

#### 2. 运行后端

```bash
# generate protocol buffers for back-end
bash refresh-backend-proto.sh

# run server
bash run-server.sh
```

#### 3. 运行Android端

```bash
# run Android application
./gradlew installDebug
```

由于`compile-kotlin`并没有在`compile-proto`之后，因此如果你修改了proto文件，或者clean了Android项目，记得运行下面的脚本，手动刷新生成下对应的proto文件，否则会有找不到proto代码的问题。

```bash
# generate protocol buffers for Android
bash refresh-android-proto.sh
```

如果你想要运行Android项目的单元测试(这也是很好的一个kotlin Android项目单元测试案例)

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
