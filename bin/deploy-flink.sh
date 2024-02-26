#!/bin/bash

. bin/common.sh

# Dockerfile 地址
# https://github.com/apache/flink-docker/blob/master/1.18/scala_2.12-java8-ubuntu/Dockerfile

image_name=flink

function install_fink() {
  jmr=$1
  tmrs=$2

  #设置分隔符
  OLD_IFS="$IFS"
  IFS=","
  tmr_array=(${tmrs})
  #恢复原来的分隔符
  IFS="$OLD_IFS"

  get_host_info $jmr "ip_inner" jmr_ip
  install_fink_jmr $jmr $jmr_ip

  for tmr in ${tmr_array[@]};
  do
    install_fink_tmr $tmr $jmr_ip
  done
}

function install_fink_jmr() {
  host=$1
  jmr_ip=$2
  container_name="flink_jmr"

  run $host "docker pull ${image_name}"
  rmc $host $container_name

  run $host "docker run -d \
  -e TZ=Asia/Shanghai \
  -e JOB_MANAGER_RPC_ADDRESS=${jmr_ip} \
  -e TASK_MANAGER_NUMBER_OF_TASK_SLOTS=3 \
  -p 8081:8081 \
  -p 6123:6123 \
  -p 6124:6124 \
  -p 6125:6125 \
  --name ${container_name} \
  --network mynet --network-alias ${container_name} ${image_name} jobmanager"

  p "sleep 5s for flink starting"
  sleep 5s
  check_d $host $container_name
}

function install_fink_tmr() {
  host=$1
  jmr_ip=$2
  container_name="flink_tmr"
  data_local_dir=/data/flink

  # 创建本地数据文件夹
  run $host "rm -rf $data_local_dir >/dev/null 2>&1 && mkdir -p ${data_local_dir}"

  # 复制配置文件
  p "copy template config file to host"
  copy $host "config/flink/flink-conf.yaml" "${data_local_dir}/flink-conf.yaml"

  # 获取服务器配置
  get_host_info $host "ip_inner" ip_inner

  # 模板文件内容变量处理（设置了这个，dashboard上面注册的taskmanager地址才会显示正常）
  replace $host "${data_local_dir}/flink-conf.yaml" "@TASKMANAGER_HOST@" $ip_inner

  run $host "docker pull ${image_name}"
  rmc $host $container_name

  run $host "docker run -d \
  -e TZ=Asia/Shanghai \
  -e JOB_MANAGER_RPC_ADDRESS=${jmr_ip} \
  -e TASK_MANAGER_NUMBER_OF_TASK_SLOTS=3 \
  -p 6121:6121 \
  -p 6122:6122 \
  -p 50100:50100 \
  -p 50101:50101 \
  -p 50102:50102 \
  --volume=${data_local_dir}/flink-conf.yaml:/opt/flink/conf/flink-conf.yaml \
  --name ${container_name} \
  --network mynet --network-alias ${container_name} ${image_name} taskmanager"

  p "sleep 5s for flink starting"
  sleep 5s
  check_d $host $container_name
}

usage()
{
  echo "\nUSAGE:"
  echo "-j\t<jobmanager>"
  echo "-t\t<taskmanager list>"
}

while getopts "j:t:" opt; do
    case $opt in
        j) jmr=${OPTARG} ;;
        t) tmrs=${OPTARG} ;;
        *) ;;
    esac
done

not_empty "jmr" ${jmr} || (usage && exit 1)
not_empty "tmrs" ${tmrs} || (usage && exit 1)

install_fink ${jmr} ${tmrs}
