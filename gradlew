#!/bin/bash
#
# Gradle wrapper script for Unix/macOS
#

# Determine the location of the script
APP_HOME="`dirname \"$0\"`"
cd "$APP_HOME"
APP_HOME="`pwd -P`"

# Find Java
if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Execute Gradle
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
exec "$JAVACMD" -Xmx64m -Xms64m -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
