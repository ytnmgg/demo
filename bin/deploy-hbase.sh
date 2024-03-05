#!/bin/bash

. bin/common.sh

function install_hbase() {
  host=$1

  container_name=hbase
  image_name=harisekhon/hbase
  data_local_dir=/data/hbase

  # 创建本地目录，用于映射hbase文件目录（简单用本地文件实现，不使用hdfs）
#  run $host "ls $data_local_dir >/dev/null 2>&1 || (mkdir -p ${data_local_dir} && chmod -R 777 ${data_local_dir})"
  run $host "rm -rf $data_local_dir >/dev/null 2>&1 && mkdir -p ${data_local_dir} && chmod -R 777 ${data_local_dir}"

  run $host "docker pull ${image_name}"
  rmc $host ${container_name}

  pf "run container <"$app">..."
  run $host "docker run -d -e TZ=Asia/Shanghai \
  -p 2181:2181 \
  -p 16000:16000 \
  -p 16010:16010 \
  -p 16020:16020 \
  -p 16030:16030 \
  -v ${data_local_dir}:/hbase-data \
  --add-host ecs01:${ecsIn01} \
  --add-host ecs02:${ecsIn02} \
  --add-host ecs03:${ecsIn03} \
  --name ${container_name} \
  --network mynet --network-alias ${container_name} ${image_name}"

  p "sleep 10s for hbase starting"
  sleep 10
  check_d $host ${container_name}
}

#### 监控页面
#http://${ip}:16010/master-status
#
#### 使用
#```bash
## 登录docker
#docker exec -it hbase bash
#
## 打开hbase shell
#./hbase/bin/hbase shell
#
## 命名空间相关操作
#list_namespace
#list_namespace_tables '命名空间名'
#create_namespace '命名空间名'
#drop_namespace '命名空间名'
#
## 表相关操作
#create '命名空间:表名','列簇名'
#create '表名','列簇名' # 省略命名空间，使用默认命名空间default
#create '命名空间:表名', {NAME => '列簇名1'}, {NAME => '列簇名2'} # 多列簇
#disable '表名' # 失效表
#drop '表名' # 删表（删之前需要先disable）
#
## 数据相关操作
#scan '表名'
#get '表名','rowkey'
#get '表名','rowkey','列簇:列'
#put '表名','rowkey','列簇:列','属性'
#delete '表名','rowKey','列簇:列'
#deleteall '表名','rowkey'
#truncate '表名' # 清空表数据
#
#```

install_hbase $ecs03
