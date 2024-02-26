#!/bin/bash

. bin/common.sh

function install_filebeat() {
  host=$1

  data_local_dir=/data/filebeat
  image_name=docker.elastic.co/beats/filebeat:8.12.1
  container_name=filebeat

  # 生成8位UUID，大小写字母组成
  run $host "head -n 5 /dev/urandom | tr -dc 'a-zA-Z' | head -c 8" filebeat_name_suffix
  filebeat_cluster_name="fb_${filebeat_name_suffix}"
  p "will use filebeat cluster name as: ${filebeat_cluster_name}"

  # 创建本地数据文件夹
  run $host "rm -rf $data_local_dir >/dev/null 2>&1 && mkdir -p ${data_local_dir}"

  # 复制配置文件
  # https://www.elastic.co/guide/en/beats/filebeat/8.12/filebeat-installation-configuration.html
  p "copy template filebeat config file to host"
  copy $host "config/filebeat/filebeat.docker.yml" "${data_local_dir}/filebeat.docker.yml"

  # 获取服务器配置
  get_host_info $host "kafka_bootstraps" kafka_bootstraps
  # 逗号分隔，转换成引号包围加逗号分隔，例如: a,b,c -> "a","b","c"
  kafka_bootstraps=$(echo ${kafka_bootstraps} | awk -F ',' '{r="\""$1"\"";for(i=2;i<=NF;i++){r=r",\""$i"\""}print r}')

  # 模板文件内容变量处理
  replace $host "${data_local_dir}/filebeat.docker.yml" "@FILEBEAT_NAME@" $filebeat_cluster_name
  replace $host "${data_local_dir}/filebeat.docker.yml" "@FB_CONF_KAFKA_BOOTSTRAP_SERVERS@" $kafka_bootstraps

  run $host "docker pull ${image_name}"
  rmc $host $container_name

  run $host "chmod go-w ${data_local_dir}/filebeat.docker.yml"

  run $host "docker run -d \
  -e --strict.perms=false \
  --user=root \
  --volume=/var/log/nginx:/var/log/nginx:ro \
  --volume=/var/log/app:/var/log/app:ro \
  --volume=${data_local_dir}/filebeat.docker.yml:/usr/share/filebeat/filebeat.yml:ro \
  --volume=/etc/localtime:/etc/localtime:ro \
  --volume=/etc/timezone:/etc/timezone:ro \
  --name ${container_name} \
  --network mynet --network-alias ${container_name} ${image_name}"

  p "sleep 10s for filebeat starting"
  sleep 10s
  check_d $host $filebeatName
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

install_filebeat ${host_array[@]}
