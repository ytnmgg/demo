#!/bin/bash

. bin/common.sh

function install_redis() {
  host=$1

  image_name=redis:latest
  container_name=redis

  run $host "docker pull ${image_name}"
  rmc $host $container_name

  run $host "docker run -d \
  -e TZ=Asia/Shanghai \
  -p 6379:6379 \
  -e MYSQL_ROOT_PASSWORD=${mysql_pwd} \
  --add-host ecs01:${ecsIn01} \
  --add-host ecs02:${ecsIn02} \
  --add-host ecs03:${ecsIn03} \
  --name ${container_name} \
  --network mynet --network-alias ${container_name} ${image_name}"

  p "sleep 10s for redis starting"
  sleep 10
  check_d $host ${container_name}
}

### 登录redis管理
#```bash
# docker exec -it redis redis-cli
#```

install_redis $ecs01