#!/bin/bash

mvn clean package -DskipTests

if [ $? -ne 0 ];then
echo "Build failed."
exit $?; fi

mvn spring-boot:run

if [ $? -ne 0 ];then
echo "Test failed."; fi