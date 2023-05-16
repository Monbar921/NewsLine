#!/bin/bash

docker build --tag 'newsline' -f Docker/Dockerfile .
docker run -p 8585:8080 -it --detach 'newsline'