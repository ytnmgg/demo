# 环境
```bash
# /etc/hosts
172.27.183.154 master
172.27.183.155 node1
172.27.183.156 node2
```

# nfs
在master节点上：

1. 安装nfs工具

```bash
> yum install nfs-utils -y
```

2. 准备共享目录

```bash
> mkdir -pv /root/data/nfs
```

3. 导出目录
```bash
> less /etc/exports
/root/data/nfs  node1(rw,no_root_squash)
/root/data/nfs  node2(rw,no_root_squash)
```

4. 启动服务
```bash
> sudo service nfs-server start
```

# nginx
在master节点上：

1. 准备目录
```bash
> mkdir -pv /root/data/nfs/nginx/conf.d
> mkdir -pv /root/data/nfs/nginx/static

> ssh root@node1 -i ./one_console_2.pem mkdir -pv /root/data/nginx/logs
> ssh root@node2 -i ./one_console_2.pem mkdir -pv /root/data/nginx/logs
```

2. 准备配置文件

把项目文件./nginx/default.conf拷贝到master机器的/root/data/nfs/nginx/conf.d中

2. k8s配置详见 ./nginx/nginx.yaml

3. 安装nginx
```bash
> kubectl create -f nginx.yaml
```

# influxdb

> https://docs.influxdata.com/influxdb/v2.2/install/?t=CLI+Setup
> https://hub.docker.com/_/influxdb
> 
1. 准备目录
```bash
> mkdir -pv /root/data/nfs/influxdb/data
```

2. 安装influxdb
```bash
> kubectl create -f influxdb.yaml
```

3. 安装influx cli
> https://docs.influxdata.com/influxdb/v2.2/tools/influx-cli/?t=Linux
> 
```bash

> wget https://dl.influxdata.com/influxdb/releases/influxdb2-client-2.3.0-linux-amd64.tar.gz
> tar xvzf influxdb2-client-2.3.0-linux-amd64.tar.gz
> sudo cp influxdb2-client-2.3.0-linux-amd64/influx /usr/local/bin/
```

4. 为了方便使用cli，还得配置端口转发
```bash
> kubectl port-forward svc/service-influxdb -n dev 8086:8086

# 注意上面的命令是同步执行的无返回的，需要新建一个窗口挂在那里，执行这个命令
```

5. 配置influxdb
```bash
> influx setup
```
按流程分别输入
- Enter a primary username.
  - rick
- Enter a password for your user.
  - rick1234
- Confirm your password by entering it again.
  - rick1234
- Enter a name for your primary organization.
  - rick
- Enter a name for your primary bucket.
  - rick-bucket
- Enter a retention period for your primary bucket—valid units are nanoseconds (ns), microseconds (us or µs), milliseconds (ms), seconds (s), minutes (m), hours (h), days (d), and weeks (w). Enter nothing for an infinite retention period.
  - {press enter}
- Confirm the details for your primary user, organization, and bucket.
  - y
    
更新token
```bash
# 查看
> influx config list
Active	Name	URL			Org
*	default	http://localhost:8086	rick

# 创建token
> influx auth create -o rick --all-access

# 查看token
> influx config list --json
```
