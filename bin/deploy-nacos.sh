#!/bin/bash

. bin/common.sh

function init_db() {
  host=$1

  get_host_info $host "mysql_pwd" mysql_pwd

  p "copy db init file to host"
  run $host "mkdir -p /data/nacos"
  copy $host "config/nacos/mysql-schema.sql" "/data/nacos/mysql-schema.sql"

  p "execute db init file"
  run $host "docker exec -i mysql mysql -uroot -p${mysql_pwd} </data/nacos/mysql-schema.sql"
}

function install_nacos() {
  host1=$1
  host2=$2
  host3=$3

  nacos_cluster="ecs01:8848,ecs02:8848,ecs03:8848"

  run_docker $host1 $nacos_cluster
  run_docker $host2 $nacos_cluster
  run_docker $host3 $nacos_cluster
}

function run_docker() {
  host=$1
  nacos_cluster=$2

  container_name=nacos
  image_name=nacos/nacos-server:v2.2.1

  # 获取服务器配置
  get_host_info $host "ip_inner" ip_inner

  run $host "mkdir -p /var/log/nacos"
  run $host "chmod -R 777 /var/log/nacos"

  run $host "docker pull ${image_name}"
  rmc $host ${container_name}

  get_host_info $host "nacos_auth_key" nacos_auth_key
  get_host_info $host "nacos_auth_value" nacos_auth_value
  get_host_info $host "nacos_auth_token" nacos_auth_token
  get_host_info $host "mysql_user" mysql_user
  get_host_info $host "mysql_pwd" mysql_pwd

  run $host "docker run -d -p 8848:8848 -p 7848:7848 -p 9848:9848 -p 9849:9849 \
  -v /var/log/nacos:/home/nacos/logs \
  -e PREFER_HOST_MODE=hostname \
  -e MODE=cluster \
  -e NACOS_SERVERS=\"${nacos_cluster}\" \
  -e NACOS_SERVER_IP=${ip_inner}\
  -e NACOS_AUTH_IDENTITY_KEY=${nacos_auth_key} \
  -e NACOS_AUTH_IDENTITY_VALUE=${nacos_auth_value} \
  -e NACOS_AUTH_TOKEN=${nacos_auth_token} \
  -e SPRING_DATASOURCE_PLATFORM=mysql \
  -e MYSQL_SERVICE_HOST=ecs01 \
  -e MYSQL_SERVICE_PORT=3306 \
  -e MYSQL_SERVICE_DB_NAME=nacos_config \
  -e MYSQL_SERVICE_USER=${mysql_user} \
  -e MYSQL_SERVICE_PASSWORD=${mysql_pwd} \
  -e MYSQL_SERVICE_DB_PARAM='characterEncoding=utf8&connectTimeout=10000&socketTimeout=30000&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true' \
  --add-host ecs01:${ecsIn01} \
  --add-host ecs02:${ecsIn02} \
  --add-host ecs03:${ecsIn03} \
  --name ${container_name} --network mynet --network-alias ${container_name} \
  ${image_name}"

  p "sleep 10s for NACOS starting"
  sleep 10
  check_d $host ${container_name}
}

function init_nacos() {
  host=$1

  # 通过nacos api导入默认配置（--data-urlencode content@${FILE_PATH}用于post文件内容）
  # https://nacos.io/zh-cn/docs/open-api.html
  curl -X POST --header 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode content@config/nacos/auth-core-prod.yml \
    'http://'$host':8848/nacos/v1/cs/configs?group=DEFAULT_GROUP&type=yaml&dataId=auth-core-prod.yml'


  curl -X POST --header 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode content@config/nacos/monitor-core-prod.yml \
    'http://'$host':8848/nacos/v1/cs/configs?group=DEFAULT_GROUP&type=yaml&dataId=monitor-core-prod.yml'

  curl -X POST --header 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode content@config/nacos/web-prod.yml \
    'http://'$host':8848/nacos/v1/cs/configs?group=DEFAULT_GROUP&type=yaml&dataId=web-prod.yml'

}

init_db $ecs01 &&
install_nacos $ecs01 $ecs02 $ecs03 &&
init_nacos $ecs01