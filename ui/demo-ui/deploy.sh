#!/bin/bash

. bin/common.sh

function deploy_web() {
  host=$1
  remote_path=/data/app/front

  # 清理远程目录
  pf "clean up app front folder"
  run $host "rm -rf "$remote_path"/*"

  # 拷贝到远程目录
  pf "deploy files to app"
  copy $host "dist/*" $remote_path

  # 拷贝图标文件到远程目录
  copy $host "public/title.svg" "${remote_path}/assets"

  # 拷贝到本机目录
  echo "copy to local folder"
  cp -r dist/* /var/front/static
  cp public/title.svg /var/front/static/assets

  echo "done!"
}

function build() {
  # 清理项目构建目录
  pf "clean up project files"
  cd ui/demo-ui
  rm -rf dist/*

  # 构建前端项目
  pf "build project"
  pnpm run build-f
}

# 构建
build

deploy_web 106.14.208.194