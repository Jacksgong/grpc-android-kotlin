#!/bin/bash

./gradlew clean
./gradlew extractDebugProto
./gradlew assembleDebug
