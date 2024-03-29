#!/bin/bash

. bin/common.sh

#设置分隔符
#OLD_IFS="$IFS"
#IFS=","
#cluster_hosts_pub_array=($given_hosts)
#cluster_hosts_inner_array=($cluster_hosts_inner)
##恢复原来的分隔符
#IFS="$OLD_IFS"

## 组装ng配置里面的demoapp upstream配置
#demoapp_upstream=''
#for ((i=0;i<${#cluster_hosts_inner_array[@]};i++))
#do
#  demoapp_upstream="server "${cluster_hosts_inner_array[$i]}":18080 weight=5 max_fails=3 fail_timeout=30;\n"$demoapp_upstream
#done

function install_docker() {
  host=$1

  # 检查安装docker
  pf "check docker installation"
  if run $host "docker -v >/dev/null 2>&1";
  then
    echo "docker already installed"
  else
    echo "docker not existing, install it first..."
    run $host "yum -y update"
    run $host "yum install -y yum-utils"
    run $host "yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo"
   #run $host "yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo"
    run $host "yum install -y docker-ce docker-ce-cli containerd.io"
    run $host "systemctl enable docker"
    run $host "systemctl restart docker"
    run $host "systemctl status docker"
    run $host "docker info"

    p "copy docker config file to host"
    copy $host "src/main/resources/config/daemon.json" "/etc/docker/daemon.json"
    run $host "systemctl restart docker"
  fi

  # 创建docker网络
  if run $host "docker network inspect mynet >/dev/null 2>&1";
  then
    echo "docker network mynet already exists"
  else
    echo "docker network mynet not existing, create it"
    run $host "docker network create mynet"
  fi
}

function install_ng() {
  host=$1
  upstream=$2
  container_name=ng
  image_name=nginx:1.23.2
  image_label_type=ng

#  # 查询服务器配置
#  get_host_info $host "ip_inner" upstream_host

  # 准备nginx配置文件
  pf "prepare nginx config file"
  run $host "mkdir -p /data/nginx"
  p "copy nginx config file to host"
  copy $host "config/ng/*" "/data/nginx/"

  # 替换ng配置文件中的ip占位符为真实ip地址
  replace $host "/data/nginx/nginx.conf" "@NG_CONF_APP_UPSTREAM@" "$upstream"
  replace $host "/data/nginx/nginx.conf" "@DOCKER_HOST_ADDRESS@" $host

  # 安装ng
  pf "install nginx"

  if run $host "docker container inspect "$container_name" >/dev/null 2>&1";
  then
    echo "container "$container_name" exists, remove it"
    run $host "docker rm -f "$container_name
  else
    echo "container "$container_name" does not exit"
  fi

  run $host "docker pull nginx:1.23.2"
  run $host "docker run -d -e TZ=Asia/Shanghai -p 80:80 -p 443:443 -p 81:81 \
    -v /data/nginx/nginx.conf:/etc/nginx/nginx.conf \
    -v /var/log/nginx:/var/log/nginx \
    -v /data/app/front:/usr/share/nginx/html \
    --add-host ecs01:${ecsIn01} \
    --add-host ecs02:${ecsIn02} \
    --add-host ecs03:${ecsIn03} \
    --name ${container_name} --network mynet --network-alias ${container_name} \
    nginx:1.23.2"
    # nginx:1.23.2 'nginx-debug' '-g' 'daemon off;'" # 开启debug的写法

  # 检查 nginx docker container 是否运行正常
  check_d $host $container_name

  # 配置logrotate
  copy $host "config/ng/nginx" "/etc/logrotate.d/nginx"

  # 强制执行一次切分
  run $host "logrotate -vf /etc/logrotate.d/nginx"
}

# 依赖：web应用安装位置
upstream="server ecs02:18080;"

install_docker $ecs02 &&
install_ng $ecs02 "$upstream"
