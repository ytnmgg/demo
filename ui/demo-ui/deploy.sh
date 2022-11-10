#!/bin/bash

set -e

pem=/Users/rick.wl/work/one_console_2.pem
remote_path=/data/app/front


echo "delete files"
rm -rf ./dist/*

echo "start to build project..."
pnpm run build-f

echo "clean up app front folder"
ssh root@139.224.72.37 -i $pem & rm -rf $remote_path/*

echo "start to deploy files to app..."
scp -i $pem -r ./dist/* root@139.224.72.37:$remote_path

echo "copy to local folder"
cp -r ./dist/* /var/front/static

echo "done!"