#!/bin/bash

. bin/common.sh

function install_jar(){
  host=$1
  app=$2
  port=$3
  debug_port=$4
  jar="${app}-0.0.1-SNAPSHOT.jar"
  image="${app}:0.0.1"
  dockerfile="Dockerfile-${app}"

  p "copy jar to host"
  copy $host "${app}/target/${jar}" "/data/app/"

  p "copy docker files to host"
  run $host "mkdir -p /data/app/config"
  copy $host "config/docker/Dockerfile" "/data/app/${dockerfile}"

  # 查询服务器配置
  get_host_info $host "mysql_user" mysql_user
  get_host_info $host "mysql_pwd" mysql_pwd
  get_host_info $host "nacos_cluster" nacos_cluster
  get_host_info $host "ip_inner" host_inner
  get_host_info $host "mysql_host_inner" mysql_host_inner
  get_host_info $host "redis_host_inner" redis_host_inner
  get_host_info $host "kafka_bootstraps" kafka_bootstraps

  p "replace variables to real value in docker file"
  replace $host "/data/app/${dockerfile}" "@DOCKER_CONF_APP_NAME@" $app
  replace $host "/data/app/${dockerfile}" "@DOCKER_CONF_JAR_NAME@" $jar
  replace $host "/data/app/${dockerfile}" "@DOCKER_CONF_MYSQL_HOST@" $mysql_host_inner
  replace $host "/data/app/${dockerfile}" "@DOCKER_CONF_REDIS_HOST@" $redis_host_inner
  replace $host "/data/app/${dockerfile}" "@DOCKER_CONF_MYSQL_USER@" $mysql_user
  replace $host "/data/app/${dockerfile}" "@DOCKER_CONF_MYSQL_PWD@" $mysql_pwd
  replace $host "/data/app/${dockerfile}" "@DOCKER_CONF_DEBUG_PORT@" $debug_port
  replace $host "/data/app/${dockerfile}" "@DOCKER_CONF_HOST_INNER@" $host_inner
  replace $host "/data/app/${dockerfile}" "@DOCKER_CONF_NACOS_CLUSTER@" $nacos_cluster
  replace $host "/data/app/${dockerfile}" "@DOCKER_CONF_KAFKA_BOOTSTRAP_SERVERS@" $kafka_bootstraps


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
  run $host "docker build -f /data/app/${dockerfile} -t "$image" /data/app"

  pf "run container <"$app">..."
  run $host "docker run -d -e TZ=Asia/Shanghai \
  -p 20880:20880 \
  -p ${port}:${port} \
  -p ${debug_port}:${debug_port} \
  -v /var/log/app:/var/log/app \
  -v /var/log/nginx:/var/log/nginx \
  -v /data/app/front:/var/front \
  -v /data/app/config:/var/config \
  -v /data/es/certs/ca/ca.crt:/var/ca.crt:ro \
  -v /data/es/pwd_elastic:/var/pwd_elastic:ro \
  -v /data/host-info.config:/var/host-info.config:ro \
  --name "$app" \
  --network mynet --network-alias "$app" "$image

  p "sleep 10s for java starting"
  sleep 10s
  check_d $host $app
}

function build_jar(){
  # mvn编译
  pf "build jar..."
  mvn clean && mvn package -DskipTests=true
}


usage()
{
  echo "\nUSAGE:"
  echo "-h\t<host>"
  echo "-t\t<web|auth-core|monitor-core>"
}

while getopts "h:t:" opt; do
    case $opt in
        h) host=${OPTARG} ;;
        t) type=${OPTARG} ;;
        *) ;;
    esac
done

not_empty "-h" ${host} || (usage && exit 1)
not_empty "-t" ${type} || (usage && exit 1)

case ${type} in
  web)
    port=18080
    debug_port=18081
  ;;
  auth-core)
    port=18082
    debug_port=18083
  ;;
  monitor-core)
    port=18084
    debug_port=18085
  ;;
  *)
  ;;
esac

not_empty "port" ${port}
not_empty "debug_port" ${debug_port}

build_jar &&
install_jar ${host} ${type} ${port} ${debug_port}
