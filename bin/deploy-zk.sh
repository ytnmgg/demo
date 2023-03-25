#!/bin/bash

. src/main/resources/bin/common.sh

function install_zk(){
  host=$1
  zkName=$2
  myid=$3

  run $host "docker pull zookeeper:3.8.1"
  rmc $host "${zkName}"

  # 准备配置文件
  pf "prepare zk config file"
  run $host "mkdir -p /data/zk"
  p "copy config file to host"
  copy $host "src/main/resources/config/zoo.cfg" "/data/zk/zoo.cfg"

  # 准备zk运行文件夹
  run $host "mkdir -p /data/zk/data"
  run $host "mkdir -p /data/zk/logs"
  run $host "chmod -R 777 /data/zk"

  # 设置myId
  run $host "touch /data/zk/data/myid"
  run $host "echo "${myid}" > /data/zk/data/myid"

  run $host "docker run -d -p 2181:2181 -p 2881:2881 -p 3881:3881 \
    -v /data/zk/zoo.cfg:/conf/zoo.cfg:ro \
    -v /data/zk/data:/var/data \
    -v /data/zk/logs:/logs \
    --name ${zkName} --network mynet --network-alias ${zkName} \
    zookeeper:3.8.1"

  # --restart always \

  p "sleep 10s for es starting"
  sleep 10s
  check_d $host $zkName
}

install_zk 139.224.72.37 "zk01" 1
install_zk 106.14.208.194 "zk01" 2
install_zk 139.196.230.113 "zk01" 3
