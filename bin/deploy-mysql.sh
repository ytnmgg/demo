#!/bin/bash

. bin/common.sh

function install_mysql() {
  host=$1

  data_local_dir=/data/mysql
  image_name=mysql:latest
  container_name=mysql

  # 创建本地数据文件夹
  mysql_conf_dir_01="/data/mysql/conf/conf.d"
  mysql_conf_dir_02="/data/mysql/conf/mysql.conf.d"
  mysql_data_dir="/data/mysql/data"
  run $host "rm -rf $mysql_conf_dir_01 >/dev/null 2>&1 && mkdir -p ${mysql_conf_dir_01}"
  run $host "rm -rf $mysql_conf_dir_02 >/dev/null 2>&1 && mkdir -p ${mysql_conf_dir_02}"
  run $host "rm -rf $mysql_data_dir >/dev/null 2>&1 && mkdir -p ${mysql_data_dir}"

  # 复制配置文件
  p "copy template config file to host"
  copy $host "config/mysql/my.cnf" "${data_local_dir}/conf/my.cnf"

  # 获取服务器配置
  get_host_info $host "mysql_pwd" mysql_pwd

  run $host "docker pull ${image_name}"
  rmc $host $container_name

  run $host "docker run -d \
  -e TZ=Asia/Shanghai \
  -p 3306:3306 \
  -v ${data_local_dir}/conf:/etc/mysql \
  -v ${data_local_dir}/data:/var/lib/mysql \
  -e MYSQL_ROOT_PASSWORD=${mysql_pwd} \
  --add-host ecs01:${ecsIn01} \
  --add-host ecs02:${ecsIn02} \
  --add-host ecs03:${ecsIn03} \
  --name ${container_name} \
  --network mynet --network-alias ${container_name} ${image_name}"

  p "sleep 10s for mysql starting"
  sleep 10
  check_d $host ${container_name}
}

function init_mysql() {
    host=$1
    data_local_dir=/data/mysql
    container_name=mysql

    # 复制配置文件
    p "copy template config file to host"
    copy $host "config/mysql/data-init.sql" "${data_local_dir}/conf/"
    copy $host "config/mysql/quartz-init.sql" "${data_local_dir}/conf/"

    # 获取服务器配置
    get_host_info $host "mysql_pwd" mysql_pwd

    # 执行初始化脚本
    run $host "docker exec mysql mysql -u root --password=$mysql_pwd -e 'source /etc/mysql/data-init.sql'"
    run $host "docker exec mysql mysql -u root --password=$mysql_pwd -e 'source /etc/mysql/quartz-init.sql'"
}

##### 查看修改是否生效
#```bash
## 登录mysql
#docker exec -it mysql mysql -u root -p
#
##查看时区是否正确
#select now();
#show variables like '%time_zone%';
#
##查看字符集是否正确
#SHOW VARIABLES LIKE 'character_set%';
#```


install_mysql $ecs01 &&
init_mysql $ecs01
