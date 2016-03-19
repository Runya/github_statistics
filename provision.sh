#!/usr/bin/env bash

apt-get update
apt-get -y install python-software-properties
add-apt-repository -y ppa:webupd8team/java
apt-get update
echo debconf shared/accepted-oracle-license-v1-1 select true | sudo debconf-set-selections
echo debconf shared/accepted-oracle-license-v1-1 seen true | sudo debconf-set-selections
apt-get -y install oracle-java8-installer
sudo dpkg --configure -a

add-apt-repository -y ppa:natecarlson/maven3
apt-get update
apt-get -y install maven3
apt-get -y install git

cd /uwc9
mvn3 spring-boot:run

