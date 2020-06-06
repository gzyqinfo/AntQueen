#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`

PIDS=`ps -ef | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`

kill $PIDS
echo "AntQueen stopped!"