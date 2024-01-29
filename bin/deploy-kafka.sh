#!/bin/bash

. bin/common.sh

kafka_version=3.6.1
kafka_path_name=kafka_2.13-${kafka_version}
kafka_tgz=${kafka_path_name}.tgz
kafka_download_url=https://downloads.apache.org/kafka/${kafka_version}/${kafka_tgz}
dir_root=/data/kafka/
dir_path=${dir_root}${kafka_path_name}/
app=kafka-server
image="${app}:${kafka_version}"

function install_kafka() {
  host1=$1
  host2=$2
  host3=$3

  prepare_files $host1
  prepare_files $host2
  prepare_files $host3

  # 生成22位UUID，大小写字母组成
  # 理论上应该用 "bin/kafka-storage.sh random-uuid" 去生成，比较麻烦，这里就直接用shell自己搞了
  run $host1 "head -n 5 /dev/urandom | tr -dc 'a-zA-Z' | head -c 22" cluster_id
  p "create uuid for kafka cluster id: "$cluster_id

  run_docker $host1 $cluster_id
  run_docker $host2 $cluster_id
  run_docker $host3 $cluster_id
}


function prepare_files() {
  host=$1

  p "check if kafka directory already exists"
  if run $host "ls "${dir_path}
  then
    echo "kafka directory already exists"
  else
    echo "kafka directory not exists, try to download and unzip"
    run $host "cd "${dir_root}" && wget "${kafka_download_url}" && tar -xzf "${kafka_tgz}
  fi

#  run $host "mkdir -p /data/kafka/logs"

  # 查询服务器配置
  get_host_info $host "ip_inner" host_inner
  get_host_info $host "node_id" node_id
  get_host_info $host "voters" voters

#  # 拷贝文件
#  p "copy config files"
#  run $host "cd "${dir_root}" && cp -r ./"${kafka_path_name}"/config/* ./config"

  p "copy Docker file and config file to host"
  copy $host "config/kafka/Dockerfile" "/data/kafka/Dockerfile"
  copy $host "config/kafka/server.properties" ${dir_path}"config/kraft/server.properties"

  # 修改文件
  p "replace variables to real value in docker file"
  replace $host "/data/kafka/Dockerfile" "@KAFKA_PATH_NAME@" $kafka_path_name
  replace $host "/data/kafka/Dockerfile" "@APP_NAME@" ${app}

  p "replace variables to real value in config file"
  replace $host ${dir_path}"config/kraft/server.properties" "@NODE_ID@" ${node_id}
  replace $host ${dir_path}"config/kraft/server.properties" "@VOTERS@" ${voters}
  replace $host ${dir_path}"config/kraft/server.properties" "@ADV_LISTENERS@" "PLAINTEXT://"${host_inner}":9092"

  run $host "mkdir -p "${dir_path}"logs"
}

function run_docker() {
  host=$1
  cluster_id=$2

  p "replace variables to real value in docker file"
  replace $host "/data/kafka/Dockerfile" "@CLUSTER_ID@" ${cluster_id}

  p "check existing container"
  rmc $host "${app}"

  p "check if image already exists"
  if run $host "docker image inspect "$image" >/dev/null 2>&1";
  then
    echo "image already exists, remove it"
    run $host "docker images -f label=type="$app" -q | xargs docker rmi"
  else
    echo "image does not exit"
  fi

  run $host "docker image prune -f"

  pf "build docker image <"$image">..."
  run $host "docker build -f /data/kafka/Dockerfile -t "$image" /data/kafka"

  pf "run container <"$app">..."
  run $host "docker run -d -e TZ=Asia/Shanghai \
  -p 9092:9092 \
  -p 9093:9093 \
  --name "$app" \
  --network mynet --network-alias "$app" "$image

  p "sleep 10s for kafka starting"
  sleep 10s
  check_d $host $app
}

usage()
{
  echo "\nUSAGE:"
  echo "-h\t<host>"
}

while getopts "h:" opt; do
    case $opt in
        h) host=${OPTARG} ;;
        *) ;;
    esac
done

not_empty "-h" ${host} || (usage && exit 1)

#设置分隔符
OLD_IFS="$IFS"
IFS=","
host_array=(${host})
#恢复原来的分隔符
IFS="$OLD_IFS"

if [ ${#host_array[@]} -ne 3 ]
then
  echo "\033[31mFATAL: need 3 hosts in one cluster! \033[0m" && exit 1
fi

install_kafka ${host_array[@]}
