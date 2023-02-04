#!/bin/bash

. src/main/resources/bin/common.sh

# 组装ng配置里面的demoapp upstream配置
demoapp_upstream=''
for ((i=0;i<${#cluster_hosts_inner_array[@]};i++))
do
  demoapp_upstream="server "${cluster_hosts_inner_array[$i]}":8080 weight=5 max_fails=3 fail_timeout=30;\n"$demoapp_upstream
done

# host循环，安装ng
for host in ${cluster_hosts_pub_array[@]}
do
  pf "pre config for host: "$host

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
    run $host "yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo"
    run $host "yum install -y docker-ce docker-ce-cli containerd.io"
    run $host "systemctl enable docker"
    run $host "systemctl restart docker"
    run $host "systemctl status docker"
    run $host "docker info"

    p "copy docker config file to host"
    copy $host "src/main/resources/daemon.json" "/etc/docker/daemon.json"
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

  # 准备nginx配置文件
  pf "prepare nginx config file"
  run $host "mkdir -p /data/nginx"
  p "copy nginx config file to host"
  copy $host "src/main/resources/nginx.conf" "/data/nginx/nginx.conf"

  # 替换ng配置文件中的ip占位符为真实ip地址
  p "replace @PRE_CONF_DEMO_APP_UPSTREAM@ by real value"
  replace $host "/data/nginx/nginx.conf" "@PRE_CONF_DEMO_APP_UPSTREAM@" $demoapp_upstream

  # 安装ng
  pf "install nginx"
  container_name=ng01
  image_name=nginx:1.23.2
  image_label_type=ng01
  if run $host "docker container inspect "$container_name" >/dev/null 2>&1";
  then
    echo "container "$container_name" exists, remove it"
    run $host "docker rm -f "$container_name
  else
    echo "container "$container_name" does not exit"
  fi

  run $host "docker pull nginx:1.23.2"
  run $host "docker run -d -e TZ=Asia/Shanghai -p 80:80 -p 443:443 -p 81:81 -v /data/nginx/nginx.conf:/etc/nginx/nginx.conf -v /var/log/nginx:/var/log/nginx -v /data/app/front:/usr/share/nginx/html --name ng01 --network mynet --network-alias ng01 nginx:1.23.2"

  # 检查 nginx docker container 是否运行正常
  check_d $host $container_name

  # 配置logrotate
  run $host "touch /etc/logrotate.d/nginx"
  run $host "cat <<EOF > /etc/logrotate.d/nginx
/var/log/nginx/*.log {
  daily
  missingok
  rotate 52
  compress
  delaycompress
  notifempty
  #create 640 nginx adm
  sharedscripts
  postrotate
    docker restart ng01
  endscript
}
EOF"
  # 强制执行一次切分
  run $host "logrotate -vf /etc/logrotate.d/nginx"

done