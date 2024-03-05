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

  voters="1@ecs01:9093,2@ecs02:9093,3@ecs03:9093"

  prepare_files $host1 "1" $voters
  prepare_files $host2 "2" $voters
  prepare_files $host3 "3" $voters

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
  node_id=$2
  voters=$3

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
  get_host_info $host "host_name" host_name

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
  replace $host ${dir_path}"config/kraft/server.properties" "@ADV_LISTENERS@" "PLAINTEXT://"${host_name}":9092"

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
  --add-host ecs01:${ecsIn01} \
  --add-host ecs02:${ecsIn02} \
  --add-host ecs03:${ecsIn03} \
  --name "$app" \
  --network mynet --network-alias "$app" "$image

  p "sleep 10s for kafka starting"
  sleep 10
  check_d $host $app
}


#### 使用
#```bash
## 创建topic
#bin/kafka-topics.sh --create --topic demo-test --bootstrap-server localhost:9092
#
## 查看topic
#bin/kafka-topics.sh --describe --topic demo-test --bootstrap-server localhost:9092
#
## 发消息
#bin/kafka-console-producer.sh --topic demo-test --bootstrap-server localhost:9092
#
## 收消息
#bin/kafka-console-consumer.sh --topic demo-test --from-beginning --bootstrap-server localhost:9092
#
#```

install_kafka $ecs01 $ecs02 $ecs03
