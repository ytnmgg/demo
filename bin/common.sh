#!/bin/bash

# 任何一条指令有错则退出，不再继续执行
set -e

# 在输出窗口显示命令本身
#set -x

# ================ 环境信息配置 =============
# 集群外网ip
cluster_hosts_pub=139.224.72.37,106.14.208.194,139.196.230.113
# 集群内网ip
cluster_hosts_inner=172.27.183.154,172.27.183.155,172.27.183.156
# mysql服务内网地址
mysql_host_inner=172.27.183.154
# redis服务内网地址
redis_host_inner=172.27.183.154
# 本地ssh授权文件地址
pem=/Users/rick.wl/work/one_console_2.pem
# =========================================

#设置分隔符
OLD_IFS="$IFS"
IFS=","
cluster_hosts_pub_array=($cluster_hosts_pub)
cluster_hosts_inner_array=($cluster_hosts_inner)
#恢复原来的分隔符
IFS="$OLD_IFS"

date_format="+%Y-%m-%d %H:%M:%S"
# 打印主要内容
# $1: 待打印字符串
function pf() {
    #echo "\033[33m$(printf %.s= {1..80})\033[0m"
    echo "\033[33m$(date "$date_format") ========== $1 ========== \033[0m"
}

# 打印次要内容
# $1: 待打印字符串
function p() {
  echo "\033[33m$(date "$date_format") \033[0m" $1
}

# 远程执行命令
# $1: 远程机器ip
# $2: 命令字符串
# $3: 命令返回值，传参需要传引用，而不是变量值（带$符号）
function run() {
  echo "\033[33m$(date "$date_format") \033[0m run cmd ["$2"] at host ["$1"]"

  result=$(ssh -i $pem root@$1 $2)
  ret=$?

  # result是命令执行的输出内容，用于保存到返回结果中给调用方使用
  if [ $3 ]
  then
    # $3是外部传入的引用
    # 正常最好用 local -n ref=$3 或者 declare -n ref=$3，然后给ref赋值：ref=$result，但是macOS的bash不支持这种写法
    eval $3='$result'
  fi

  # ret是命令的执行成功与否，用于if判断
  return $ret
}

# 远程拷贝文件
# $1: 远程机器ip
# $2: 本地文件（夹）目录
# $3: 远程文件（夹）目录
function copy() {
  echo "\033[33m$(date "$date_format") \033[0m copy local ["$2"] to ["$3"] of host ["$1"]"
  scp -i $pem -r $2 root@$1:$3
}

# 检查docker容器是否在运行中
# $1: 远程机器ip
# $2: 容器名称
function check_d() {
  if run $1 "docker ps | grep '"$2"' >/dev/null 2>&1 && echo 'ok'" ;
  then
    echo "check container "$2" success"
    return 0;
  else
    echo "check container "$2" failed"
    return -1;
  fi
}

# 替换文件中的占位符为真实内容
# $1: 远程机器ip
# $2: 文件名
# $3: 占位符
# $4: 真实内容
function replace() {
  p "replace "$3" by real value: ""$4"
  run $1 "sed -i 's/$3/$4/g' "$2

#  run $1 "awk '/"$3"/{print NR}' "$2"|xargs -I '{}' sed -i '{}i "$4"' "$2
#  run_cmd="sed -i '"$3"/d' "$2
#  run $1 $run_cmd
}

# 检查container是否存在，存在就删了
# $1: 远程机器ip
# $2: 容器名
function rmc() {
  if run $1 "docker ps -a | grep '"$2"' >/dev/null 2>&1" ;
  then
    echo "container "$2" exists, remove it"
    run $1 "docker rm -f "$2
  else
    echo "container "$2" does not exit"
  fi
}

# 删除不可见字符
# https://wiki.bash-hackers.org/syntax/pe#substring_removal
# https://superuser.com/questions/489180/remove-r-from-echoing-out-in-bash-script
# $1: 待处理字符串
# $2: 处理结果，传参需要传引用，而不是变量值（带$符号）
function removeInvisible() {
  STR=$1
  STR="${STR%%[[:cntrl:]]}"
  eval $2='$STR'
}