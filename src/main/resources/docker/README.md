# Docker版运行指引

## 安装docker
```bash
yum update
yum install -y yum-utils
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
yum install -y docker-ce docker-ce-cli containerd.io

systemctl enable docker
systemctl restart docker
systemctl status docker
docker info

cat <<EOF > /etc/docker/daemon.json
{
"registry-mirrors": [
"https://docker.mirrors.ustc.edu.cn",
"http://hub-mirror.c.163.com"
],
"max-concurrent-downloads": 10,
"log-driver": "json-file",
"log-level": "warn",
"log-opts": {
"max-size": "10m",
"max-file": "3"
},
"data-root": "/var/lib/docker"
}
EOF

systemctl restart docker
```

## 创建docker网络
```bash
docker network create mynet
```

## docker安装mysql
```bash
mkdir -p /data/mysql/conf/conf.d
mkdir -p /data/mysql/conf/mysql.conf.d
docker pull mysql:latest
docker run -d -p 3306:3306 -v /data/mysql/conf:/etc/mysql -v /data/mysql/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=1234Qweh --name mysql01 --network mynet --network-alias mysql01 mysql:latest

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
source /etc/mysql/conf/data-init.sql
```

## docker安装redis
```bash
docker search redis
docker pull redis:latest
docker images

docker run -d -p 6379:6379 --network mynet --network-alias redis01 --name redis01 redis:latest

docker exec -it 32320064d6f0 redis-cli
```