#!/bin/bash

. src/main/resources/common.sh

remote_path=/data/app/front

# 清理项目构建目录
pf "clean up project files"
cd ui/demo-ui
rm -rf dist/*

# 构建前端项目
pf "build project"
pnpm run build-f

# host循环，安装ng
for host in ${cluster_hosts_pub_array[@]}
do
  # 清理远程目录
  pf "clean up app front folder"
  run $host "rm -rf "$remote_path"/*"

  # 拷贝到远程目录
  pf "deploy files to app"
  copy $host "dist/*" $remote_path

  # 拷贝到本机目录
  echo "copy to local folder"
  cp -r dist/* /var/front/static

  echo "done!"
done