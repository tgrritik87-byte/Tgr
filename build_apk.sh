#!/usr/bin/env bash
set -e

echo "=========================================="
echo " Building Spark Dating App - Debug APK    "
echo "=========================================="

# Check if Gradle Wrapper or system Gradle is available
if [ -f "./gradlew" ]; then
    echo "Using ./gradlew..."
    chmod +x ./gradlew
    ./gradlew :app:assembleDebug
elif command -v gradle &> /dev/null; then
    echo "Using system gradle command..."
    gradle :app:assembleDebug
else
    echo "Error: Neither ./gradlew nor 'gradle' command was found in PATH."
    exit 1
fi

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

if [ -f "$APK_PATH" ]; then
    echo "=========================================="
    echo " Build SUCCESSFUL!                        "
    echo " APK generated at: $APK_PATH              "
    echo "=========================================="
    
    # Also copy to root for quick access
    cp "$APK_PATH" ./Spark-debug.apk
    echo "Copied to: ./Spark-debug.apk ($(ls -lh ./Spark-debug.apk | awk '{print $5}'))"
else
    echo "Error: APK not found at $APK_PATH"
    exit 1
fi
