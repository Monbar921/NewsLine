#!/bin/bash

CONTAINERS=($(docker ps -a | grep 'newsline' | awk '{print $1}'))


for i in "${CONTAINERS[@]}"
do
	docker stop $i
	docker rm $i
done


APP_IMAGE=($(docker images | grep 'newsline-front' | awk '{print $3}'))
docker rmi $APP_IMAGE

# IMAGES=($(docker images | grep 'newsline' | awk '{print $3}'))
# for i in "${IMAGES[@]}"
# do
# 	docker rmi $i
# done

#POSTGRES_IMAGE=($(docker images | grep 'POSTGRES' | awk '{print $3}'))
#LIQUIBASE_IMAGE=($(docker images | grep 'liquibase/liquibase' | awk '{print $3}'))

#docker rmi $POSTGRES_IMAGE
#docker rmi $LIQUIBASE_IMAGE