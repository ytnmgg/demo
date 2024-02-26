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
  --name ${container_name} \
  --network mynet --network-alias ${container_name} ${image_name}"

  p "sleep 10s for hbase starting"
  sleep 10s
  check_d $host ${container_name}
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

if [ ${#host_array[@]} -ne 1 ]
then
  echo "\033[31mFATAL: need and only need 1 host! \033[0m" && exit 1
fi

install_hbase ${host_array[@]}
