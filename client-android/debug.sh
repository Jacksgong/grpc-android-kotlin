#!/bin/bash

./gradlew extractDebugProto
./gradlew installDebug
adb shell am start -n "cn.dreamtobe.grpc.client/cn.dreamtobe.grpc.client.activity.LaunchActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
