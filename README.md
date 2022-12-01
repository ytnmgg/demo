# 1 依赖安装

## 安装docker、安装nginx
```bash
sh src/main/resources/preconfig.sh
```

## 安装 mysql
```bash
mkdir -p /data/mysql/conf/conf.d
mkdir -p /data/mysql/conf/mysql.conf.d
docker pull mysql:latest
docker run -d -p 3306:3306 -v /data/mysql/conf:/etc/mysql -v /data/mysql/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=1234Qweh --name mysql01 --network mynet --network-alias mysql01 mysql:latest
```

### 登录mysql管理
```bash
docker ps
docker exec -it 9b1451b721fe mysql -u root -p
```

### 设置mysql
#### 设置时区
```bash
select now();
show variables like '%time_zone%';
set global time_zone='+8:00';
```

#### 设置编码
```bash
SHOW VARIABLES LIKE 'character_set%';
```

修改 /etc/mysql/my.cnf
```bash
[client]
default-character-set=utf8mb4

[mysqld]
character_set_server=utf8mb4
```

### 初始化mysql数据
拷贝项目的data-init.sql到vm的/data/mysql/conf下面
登录mysql，执行命令
```bash
source /etc/mysql/data-init.sql
```

### 初始化quartz数据
拷贝项目的quartz-init.sql到vm的/data/mysql/conf下面
登录mysql，执行命令
```bash
source /etc/mysql/quartz-init.sql
```


## 安装 grafana
docker run -d -p 3000:3000 -v /data/grafana:/var/lib/grafana -v /data/grafana/defaults.ini:/usr/share/grafana/conf/defaults.ini --network mynet --network-alias grafana --name grafana grafana/grafana-oss
```bash
# 1. 注意上面的 /data/grafana:/var/lib/grafana 是grafana的数据文件，包含了所有的数据和图表配置，用于新起docker时继承之前的图表数据
# 2. /data/grafana/defaults.ini:/usr/share/grafana/conf/defaults.ini 是grafana配置，需要修改的有两点：
# 2.1 开启匿名访问
# [auth.anonymous]
# enabled = true
# 2.2 允许iframe嵌入
# [security]
# allow_embedding = true
```


## 安装 redis
```bash
docker search redis
docker pull redis:latest
docker images

docker run -d -p 6379:6379 --network mynet --network-alias redis01 --name redis01 redis:latest

docker exec -it 32320064d6f0 redis-cli
```

### 登录redis管理
docker ps
docker exec -it 32320064d6f0 redis-cli



# 2 前端部署
```bash
sh ui/demo-ui/deploy.sh
```


# 3 部署后端
```bash
sh src/main/resources/docker/deploy.sh
```

# 其它

## 本地调试
### 设置idea的Debug&Run Configurations的环境变量
profile.active=dev;front.version=v0.0.1