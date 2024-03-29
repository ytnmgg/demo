# 1 依赖安装

## 准备服务器集群
### 修改域名映射文件
在每台host的hosts文件中新增host映射
```bash
vim /etc/hosts

172.27.183.154 ecs01
172.27.183.155 ecs02
172.27.183.156 ecs03
```

### 添加host配置
复制代码目录中的config/host-info.config配置文件到每个host的/data目录下面
并按实际情况修改内容

## 安装docker、安装nginx
```bash
sh bin/deploy-docker-ng.sh
```

## 安装zookeeper
```bash
sh bin/deploy-zk.sh
```

## 安装 mysql
```bash
sh bin/deploy-mysql.sh
```

## 安装 redis
```bash
sh bin/deploy-redis.sh
```

## 安装kafka
```bash
sh bin/deploy-kafka.sh
```

## 安装hbase
```bash
sh bin/deploy-hbase.sh
```




## 安装 prometheus

### 准备配置文件
```bash
mkdir -p /data/prometheus/conf
mkdir -p /data/prometheus/data
chmod 777 /data/prometheus/data

# 将resources/prometheus.yml 拷贝至 /data/prometheus/conf 下面
```

### 通过docker安装并启动
```bash
docker pull prom/prometheus
docker run -d -e TZ=Asia/Shanghai -p 9090:9090 \
-v /data/prometheus/conf/prometheus.yml:/etc/prometheus/prometheus.yml \
-v /data/prometheus/data:/prometheus \
--name prometheus01 --network mynet --network-alias prometheus01 \
prom/prometheus \
--config.file=/etc/prometheus/prometheus.yml \
--storage.tsdb.path=/prometheus \
--storage.tsdb.retention.time=90d \
--storage.tsdb.retention.size=10GB \
--web.console.libraries=/usr/share/prometheus/console_libraries \
--web.console.templates=/usr/share/prometheus/consoles
```

### 安装 Node exporter（监控linux系统）
```bash
docker pull quay.io/prometheus/node-exporter
docker run -d -e TZ=Asia/Shanghai --net="host" --pid="host" -v "/:/host:ro,rslave" quay.io/prometheus/node-exporter:latest --path.rootfs=/host
```

### 安装 nginx-prometheus-exporter（监控nginx）
```bash
# https://github.com/nginxinc/nginx-prometheus-exporter
docker run -d -e TZ=Asia/Shanghai -p 9113:9113 \
--name ngExporter01 --network mynet --network-alias ngExporter01 \
nginx/nginx-prometheus-exporter:0.10.0 -nginx.scrape-uri=http://ng01:81/nginx_status
```

### 安装 cadvisor (监控Docker)
```bash
# https://github.com/google/cadvisor

docker pull google/cadvisor

docker run \
  -e TZ=Asia/Shanghai \
  --volume=/:/rootfs:ro \
  --volume=/var/run:/var/run:ro \
  --volume=/sys:/sys:ro \
  --volume=/var/lib/docker/:/var/lib/docker:ro \
  --volume=/dev/disk/:/dev/disk:ro \
  --publish=8082:8080 \
  --detach=true \
  --name=cadvisor \
  --privileged \
  --device=/dev/kmsg \
  --network mynet --network-alias cadvisor \
  google/cadvisor:latest
```


## 安装elasticsearch
### 准备
```bash
# https://www.elastic.co/guide/en/elasticsearch/reference/8.5/docker.html#docker-prod-prerequisites

vim /etc/sysctl.conf

# 添加一行
vm.max_map_count = 655360

# 执行命令使上面的参数生效
sysctl -p

# 创建data文件夹
mkdir -p /data/es/data
mkdir -p /data/es/config
mkdir -p /data/es/plugins
mkdir -p /data/es/certs
mkdir -p /data/es/logs

chmod -R 777 /data/es

# 下面的步骤只需要在es01执行
## 生成certs
docker run -it --rm \
-v /data/es/certs:/usr/share/elasticsearch/config/certs \
docker.elastic.co/elasticsearch/elasticsearch:8.5.3 \
sh -c "bin/elasticsearch-certutil ca --silent --pem -out config/certs/ca.zip && unzip config/certs/ca.zip -d config/certs"

## 在certs下面新增instances.yml文件
vim data/es/certs/instances.yml

## 文件内容：
instances:
  - name: es01
    ip:
      - 172.27.183.154
    dns:
      - es01
  - name: es02
    ip:
      - 172.27.183.155
    dns:
      - es02

## 创建certs文件
docker run -it --rm \
-v /data/es/certs:/usr/share/elasticsearch/config/certs \
docker.elastic.co/elasticsearch/elasticsearch:8.5.3 \
sh -c "bin/elasticsearch-certutil cert --silent --pem -out config/certs/certs.zip --in config/certs/instances.yml --ca-cert config/certs/ca/ca.crt --ca-key config/certs/ca/ca.key && unzip config/certs/certs.zip -d config/certs"

# 下面的步骤只需要在es02执行 (ES安装完成之后，生成密码文件了才执行）
## 从es01 copy文件过来
scp -i /data/one_console_2.pem -r root@172.27.183.154:/data/es/certs/* /data/es/certs
scp -i /data/one_console_2.pem -r root@172.27.183.154:/data/es/pwd_elastic /data/es/pwd_elastic

```

### 安装
```bash
sh src/main/resources/bin/deploy-es.sh

# 测试连接（密码输入上面保存的）
curl --cacert /data/es/certs/ca/ca.crt -u elastic https://localhost:9200
# kibana 页面：http://139.224.72.37:5601/
# 登录用户：elastic 登录密码：cat /data/es/pwd_elastic
```


## 安装 grafana
### 准备配置文件
```bash
mkdir -p /data/grafana
chmod 777 /data/grafana

# 将resources/grafana.ini 拷贝至 /data/grafana 下面

# 1. 注意docker命令里面映射的 /data/grafana:/var/lib/grafana 是grafana的数据文件，包含了所有的数据和图表配置，用于新起docker时继承之前的图表数据
# 2. /data/grafana/grafana.ini:/etc/grafana/grafana.ini 是grafana配置，需要修改的有：

## 开启匿名访问
#[auth.anonymous]
#enabled = true

[auth.generic_oauth]
enabled = true
name = OAuth
allow_sign_up = true
client_id = grafana
client_secret = grafana_xxx
auth_url = /grafana-auth/auth.json
token_url = http://ng01/grafana-auth/token.json
api_url = http://ng01/grafana-auth/api.json
role_attribute_path = role

## 开启自动登录
[auth]
oauth_auto_login = true

[server]
domain = 139.224.72.37
# 因为用nginx做了路由转发，需要配置这个，作为登录的callback地址头
root_url = %(protocol)s://%(domain)s:%(http_port)s/grafana/
serve_from_sub_path = true

# 允许iframe嵌入
[security]
allow_embedding = true

```

### 通过docker安装并启动
```bash
docker pull grafana/grafana-oss
docker run -d -e TZ=Asia/Shanghai -p 3000:3000 -v /data/grafana:/var/lib/grafana -v /data/grafana/grafana.ini:/etc/grafana/grafana.ini --name grafana01 --network mynet --network-alias grafana01 grafana/grafana-oss
```

### 接入prometheus
Data Source 类型选择Prometheus，HTTP选项的URL填：http://prometheus01:9090
其它属性默认即可

dashboard导入的配置有：
- 1860: Node Exporter Full
- 3662: Prometheus 2.0 Overview
- 12708: NGINX exporter
- 15798: Docker monitoring with service selection




# 2 前端部署
```bash
sh ui/demo-ui/deploy.sh
```


# 3 部署后端
```bash
sh bin/deploy-jar.sh 
```


# 4 本地调试
绑定host，用域名访问
```bash
127.0.0.1 demo.rick.com
106.14.208.194 api.rick.com
```
这样可以防止本地页面访问的ip和远端api的ip不一致，cookie带不上去。
因为前端代码里面做了如下设置，绑定host以后，页面和api地址都属于域名rick.com，可以带cookie
```bash
1. .env.dev 设置了api地址:
VITE_BASE_URL='http://api.rick.com:80'

2. src/utils/auth.ts 设置了cookie的绑定域名:
  cookies.set(AccessTokenKey, token.accessToken, {domain: "rick.com"})

```


# 其它
## git 多账号
git 的local config添加core.sshCommand属性，指定当前账号的ssh key就行了：
> git config core.sshCommand "ssh -i ~/.ssh/id_rsa_example -F /dev/null"

## 本地调试
### 设置idea的Debug&Run Configurations的环境变量
profile.active=dev;front.version=v0.0.1