#!/bin/bash

# .git repo가 존재하는 곳으로 이동
cd /home/ubuntu/backend/CS-Quiz-BE || { echo "Failed to change directory to git root"; exit 1; }

# 현재 브랜치 이름 가져오기
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

# 기본 환경 변수 세팅
PROJECT_NAME="CS-Quiz-BE"

# 브랜치에 따른 설정 값 분리
if [ "$CURRENT_BRANCH" == "main" ]; then
  PROFILE="prod"
  DIRECTORY="production"
  PORT=8080
  echo "현재 브랜치: '$CURRENT_BRANCH'"
elif [ "$CURRENT_BRANCH" == "release" ]; then
  PROFILE="stage"
  DIRECTORY="release"
  PORT=8082
  echo "현재 브랜치: '$CURRENT_BRANCH'"
else
  echo "현재 브랜치: '$CURRENT_BRANCH'"
  echo "지원되지 않는 브랜치입니다: $CURRENT_BRANCH" >&2
  exit 1
fi

JAR_PATH="/home/ubuntu/backend/$PROJECT_NAME/build/libs/*$PROFILE.jar"
DEPLOY_PATH=/home/ubuntu/backend/$DIRECTORY/$PROJECT_NAME/ #jar 파일이 복사되고 실행될 경로
DEPLOY_LOG_PATH="/home/ubuntu/backend/$DIRECTORY/log/deploy/$PROJECT_NAME/deploy_$(date +%Y%m%d).log"
DEPLOY_ERR_LOG_PATH="/home/ubuntu/backend/$DIRECTORY/log/deploy/$PROJECT_NAME/deploy_err_$(date +%Y%m%d).log"
APPLICATION_LOG_PATH="/home/ubuntu/backend/$DIRECTORY/log/$PROJECT_NAME/application_$(date +%Y%m%d).log"
BUILD_JAR=$(ls $JAR_PATH)
JAR_NAME=$(basename $BUILD_JAR)


echo "=========== 배포 시작 : $(date +%c) ===========" >> $DEPLOY_LOG_PATH

echo "> build 파일명: $JAR_NAME" >> $DEPLOY_LOG_PATH
echo "> build 파일 복사" >> $DEPLOY_LOG_PATH
cp $BUILD_JAR $DEPLOY_PATH

echo "> 현재 동작중인 어플리케이션 pid 체크" >> $DEPLOY_LOG_PATH
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]; then
  echo "> 현재 동작중인 어플리케이션 존재 X" >> $DEPLOY_LOG_PATH
else
  echo "> 현재 동작중인 어플리케이션 존재 O" >> $DEPLOY_LOG_PATH
  echo "> 현재 동작중인 어플리케이션 강제 종료 진행" >> $DEPLOY_LOG_PATH
  echo "> kill -9 $CURRENT_PID" >> $DEPLOY_LOG_PATH
  kill -9 $CURRENT_PID
  sleep 5
fi

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo "> DEPLOY_JAR 배포" >> $DEPLOY_LOG_PATH

nohup java -jar -Dspring.profiles.active=$PROFILE $DEPLOY_JAR --server.port=$PORT  >> $APPLICATION_LOG_PATH 2> $DEPLOY_ERR_LOG_PATH &

sleep 3
echo "> 배포 종료 : $(date +%c)" >> $DEPLOY_LOG_PATH
