#!/bin/bash

. bin/common.sh

function init_db() {
  host=$1

  get_host_info $host "mysql_pwd" mysql_pwd

  run $host "cat /data/es/pwd_elastic" pwd_elastic

  p "copy db init file to host"
  run $host "mkdir -p /data/nacos"
  copy $host "config/nacos/mysql-schema.sql" "/data/nacos/mysql-schema.sql"

  p "execute db init file"
  run $host "docker exec -i mysql01 mysql -uroot -p${mysql_pwd} </data/nacos/mysql-schema.sql"
}

function install_nacos() {
  host=$1
  host_inner=$2

  container_name=nacos
  image_name=nacos/nacos-server:v2.2.1

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
  -e PREFER_HOST_MODE=ip \
  -e MODE=cluster \
  -e NACOS_SERVERS=\"172.27.183.154:8848 172.27.183.155:8848 172.27.183.156:8848\" \
  -e NACOS_SERVER_IP=${host_inner}\
  -e NACOS_AUTH_IDENTITY_KEY=${nacos_auth_key} \
  -e NACOS_AUTH_IDENTITY_VALUE=${nacos_auth_value} \
  -e NACOS_AUTH_TOKEN=${nacos_auth_token} \
  -e SPRING_DATASOURCE_PLATFORM=mysql \
  -e MYSQL_SERVICE_HOST=172.27.183.154 \
  -e MYSQL_SERVICE_PORT=3306 \
  -e MYSQL_SERVICE_DB_NAME=nacos_config \
  -e MYSQL_SERVICE_USER=${mysql_user} \
  -e MYSQL_SERVICE_PASSWORD=${mysql_pwd} \
  --name ${container_name} --network mynet --network-alias ${container_name} \
  ${image_name}"

  p "sleep 10s for es starting"
  sleep 10s
  check_d $host ${container_name}
}

# 初始化nacos所需库和表
init_db 139.224.72.37

# 集群安装nacos，函数入参分别是节点的外网ip和内网ip
install_nacos 139.224.72.37 172.27.183.154
install_nacos 106.14.208.194 172.27.183.155
install_nacos 139.196.230.113 172.27.183.156

# 通过nacos api导入默认配置（--data-urlencode content@${FILE_PATH}用于post文件内容）
# https://nacos.io/zh-cn/docs/open-api.html
curl -X POST --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode content@config/nacos/auth-core-prod.yml \
  'http://139.224.72.37:8848/nacos/v1/cs/configs?group=DEFAULT_GROUP&type=yaml&dataId=auth-core-prod.yml'
