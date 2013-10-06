#!/bin/bash
if [ -e "`dirname $0`/sbt-launch.jar" ]
then
  echo ""
else
  echo "Downloading sbt-launch.jar..."
  wget "http://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/0.13.0-RC4/sbt-launch.jar" > /dev/null 2>&1
  if [ $? != 0 ]
  then
    curl -O "http://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/0.13.0-RC4/sbt-launch.jar"
    if [ $? != 0 ]
    then
      echo "Failed to get sbt-launch.jar, please install wget or curl"
    fi
  fi
  java -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=384M -jar `dirname $0`/sbt-launch.jar "project odc" download-libs
fi

java -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=384M -jar `dirname $0`/sbt-launch.jar "$@"
