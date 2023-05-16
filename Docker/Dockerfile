FROM ubuntu:latest
ARG DEBIAN_FRONTEND=noninteractive
ADD . application

RUN  apt-get update \
  && apt-get install -y openjdk-17-jdk \
  && apt-get -y install maven && apt-get install sudo
RUN apt update
RUN apt install postgresql postgresql-contrib -f -y

WORKDIR /application
USER root
RUN chmod +x src/main/dockerbuild/dockerbuild.sh
CMD ["/bin/bash", "src/main/dockerbuild/dockerbuild.sh"]