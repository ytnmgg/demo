#!/bin/bash

. bin/common.sh

usage()
{
  echo "\nUSAGE:\n"
  echo "-h\t<ip列表>\t\t\t\t(必须)"
  echo "-a\t<web|auth-core|monitor-core>\t\t(必须)"

  echo "\nNOTE:"
  echo "\t参数hosts可以单个ip，也可以多个，逗号分隔"
}

checkOpts () {
    local key=$1
    local value=$2
    [[ -z "${value}" ]] \
    && echo  "\033[31mFATAL: ${key} should not be empty! \033[0m" \
    && usage \
    && return 1
    return 0
}

#while getopts "h:a:" opt;do
#    case $opt in
#        h) given_hosts=${OPTARG} && echo "given_hosts=${given_hosts}" ;;
#        a) given_app=${OPTARG} && echo "given_app=${given_app}" ;;
#        *) usage && exit 0 ;;
#    esac
#done


#read -p "确认参数无误，继续执行脚本？[Y/N]" input
#if [[ $input = "y" || $input = "Y" ]];then
#    echo -e "\n------ continue... ------\n"
#else
#    echo -e "\n------ exit ------\n"
#    exit 1
#fi

#! checkOpts "-h" ${given_hosts} && exit 1
#! checkOpts "-a" ${given_app} && exit 1


##设置分隔符
#OLD_IFS="$IFS"
#IFS=","
#cluster_hosts_pub_array=($given_hosts)
#cluster_hosts_inner_array=($cluster_hosts_inner)
##恢复原来的分隔符
#IFS="$OLD_IFS"

function install_jar(){
  host=$1
  host_inner=$2
  app=$3
  port=$4
  debug_port=$5
  jar="${app}-0.0.1-SNAPSHOT.jar"
  image="${app}:0.0.1"
  dockerfile="Dockerfile-${app}"
  nacos_cluster=172.27.183.154:8848,172.27.183.155:8848,172.27.183.156:8848

  p "copy jar to host"
  copy $host "${app}/target/${jar}" "/data/app/"

  p "copy docker files to host"
  run $host "mkdir -p /data/app/config"
  copy $host "config/docker/Dockerfile" "/data/app/${dockerfile}"

  # 查询host-info
  #run $host "cat /data/host-info.config | sed '/^mysql_user=/!d;s/.*=[[:space:]]*//;s/[[:space:]]*$//'" mysql_user
  get_host_info $host "mysql_user" mysql_user
  if [ -z "$mysql_user" ];then
    echo "ERROR: invalid mysql_user in host-info.config"
    exit 1
  fi

  #run $host "cat /data/host-info.config | sed '/^mysql_pwd=/!d;s/.*=[[:space:]]*//;s/[[:space:]]*$//'" mysql_pwd
  get_host_info $host "mysql_pwd" mysql_pwd
  if [ -z "$mysql_pwd" ];then
    echo "ERROR: invalid mysql_pwd in host-info.config"
    exit 1
  fi

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

# mvn编译
pf "build jar..."
mvn clean && mvn package -DskipTests=true

#install_jar 139.224.72.37 172.27.183.154 "auth-core" 18082 18083

install_jar 106.14.208.194 172.27.183.155 "web" 18080 18081



