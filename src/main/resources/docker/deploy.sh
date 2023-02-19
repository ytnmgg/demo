#!/bin/bash

. src/main/resources/bin/common.sh

container_name=demoapp
image_name=demoapp:0.0.1
image_label_type=demoapp

# mvn编译
pf "build jar..."
mvn clean && mvn package -DskipTests=true

# host循环，安装
for host in ${cluster_hosts_pub_array[@]}
do
  # 把jar包和配置文件拷贝至host
  pf "prepare host"
  p "copy jar to host"
  copy $host "./target/demo-0.0.1-SNAPSHOT.jar" "/data/app/"

  p "copy config files to host"
  run $host "mkdir -p /data/app/config"
  copy $host "./src/main/resources/docker/Dockerfile" "/data/app/"
  copy $host "./src/main/resources/docker/config.properties" "/data/app/config/"

  # 查询host-info
  run $host "cat /data/host-info.config" host_info
  mysql_user=`echo ${host_info} | sed '/^mysql_user=/!d;s/.*=[[:space:]]*//;s/[[:space:]]*$//'`
  if [ -z "$mysql_user" ];then
    echo "ERROR: invalid mysql_user in host-info.config"
    exit 1
  fi

  mysql_pwd=`echo ${host_info} | sed '/^mysql_pwd=/!d;s/.*=[[:space:]]*//;s/[[:space:]]*$//'`
  if [ -z "$mysql_pwd" ];then
    echo "ERROR: invalid mysql_pwd in host-info.config"
    exit 1
  fi

  # 替换配置文件中的占位符为真实内容
  replace $host "/data/app/Dockerfile" "@DOCKER_CONF_MYSQL_HOST@" $mysql_host_inner
  replace $host "/data/app/Dockerfile" "@DOCKER_CONF_REDIS_HOST@" $redis_host_inner
  replace $host "/data/app/Dockerfile" "@DOCKER_CONF_MYSQL_USER@" $mysql_user
  replace $host "/data/app/Dockerfile" "@DOCKER_CONF_MYSQL_PWD@" $mysql_pwd

  # 查看已有的demoapp进程是否存在，存在就先清理掉
  p "check if container already exists"
  if run $host "docker container inspect "$container_name" >/dev/null 2>&1";
  then
    echo "container already exists, remove it"
    run $host "docker rm -f "$container_name
  else
    echo "container does not exit"
  fi

  p "check if image already exists"
  if run $host "docker image inspect "$image_name" >/dev/null 2>&1";
  then
    echo "image already exists, remove it"
    run $host "docker images -f label=type="$image_label_type" -q | xargs docker rmi"
  else
    echo "image does not exit"
  fi

  run $host "docker image prune -f"

  # build docker image and run container
  pf "build docker image <"$image_name">..."
  build_cmd="cd /data/app/ ; docker build -t "$image_name" ."
  run $host $build_cmd

  pf "run container <"$container_name">..."
  run_cmd="docker run -d -e TZ=Asia/Shanghai -p 8080:8080 -v /var/log/app:/var/log/app -v /var/log/nginx:/var/log/nginx -v /data/app/front:/var/front -v /data/app/config:/var/config  --name "$container_name" --network mynet --network-alias "$container_name" "$image_name
  run $host $run_cmd

  pf "done!"
done