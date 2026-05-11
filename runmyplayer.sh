#!/bin/bash

# Arguments:
# $1 : Role (WHITE or BLACK)
# $2 : Timeout in seconds
# $3 : Server IP

# Execute the AI using java directly.
# The classpath contains the two required jars and the bin folder with the compiled classes.
# Note: Linux uses ':' as the classpath separator.
java -cp "lib/gson-2.2.2.jar:lib/commons-cli-1.4.jar:lib/aima-core-3.0.0.jar:build:bin" tablut.client.TablutAI "$1" "$2" "$3"
