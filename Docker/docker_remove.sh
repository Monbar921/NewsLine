#!/bin/bash

CONTAINER=$(docker ps | grep 'newsline' | awk '{print $1}')
docker stop $CONTAINER
docker rm $CONTAINER
docker rmi newsline