#!/bin/bash

# 任何一条指令有错则退出，不再继续执行
set -e

date_format="+%Y-%m-%d %H:%M:%S"
pem=/Users/rick.wl/Desktop/rick01.pem
container_name=demoapp
image_name=demoapp:0.0.1
image_label_type=demoapp

echo "[$(date "$date_format")][#1 start to build jar...]"
mvn clean && mvn package -DskipTests=true

echo "[$(date "$date_format")][#2 copy jar to host]"
scp -i $pem ./target/demo-0.0.1-SNAPSHOT.jar root@47.99.61.11:/data/app/

echo "[$(date "$date_format")][#3 copy Dockerfile to host]"
scp -i $pem ./src/main/resources/docker/Dockerfile root@47.99.61.11:/data/app/

echo "[$(date "$date_format")][#4 inspect container <"$container_name">...]"
if ssh -i $pem root@47.99.61.11 docker container inspect $container_name >/dev/null 2>&1;
then
  echo "container exists, remove it"
  ssh -i $pem root@47.99.61.11 docker rm -f $container_name
else
  echo "container does not exit"
fi

echo "[$(date "$date_format")][#5 inspect image <"$image_name">...]"
if ssh -i $pem root@47.99.61.11 docker image inspect $image_name >/dev/null 2>&1;
then
  echo "image exists, remove it"
  ssh -i $pem root@47.99.61.11 "docker images -f label=type="$image_label_type" -q | xargs docker rmi"
else
  echo "image does not exit"
fi

echo "[$(date "$date_format")][#6 clean up images]"
ssh -i $pem root@47.99.61.11 docker image prune -f

echo "[$(date "$date_format")][#7 start to build docker image <"$image_name">...]"
build_cmd="cd /data/app/ ; docker build -t "$image_name" ."
echo "command is: "$build_cmd
ssh -i $pem root@47.99.61.11 $build_cmd

echo "[$(date "$date_format")][#8 start to run container <"$container_name">...]"
run_cmd="docker run -d -p 8080:8080 -v /var/log/app:/var/log/app -v /var/log/nginx:/var/log/nginx -v /data/app/front:/var/front  --name "$container_name" --network mynet --network-alias "$container_name" "$image_name
echo "command is: "$run_cmd
ssh -i $pem root@47.99.61.11 $run_cmd

echo "done!"