#!/bin/bash

docker build --tag 'newsline' -f Docker/Dockerfile .
docker run -e POSTGRES_USER='newsline' -e POSTGRES_PASSWORD='newsline' -p 8585:8080 -it --detach 'newsline'