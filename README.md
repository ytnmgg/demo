# 依赖安装

## 启动并配置nginx
yum install nginx

systemctl start nginx
systemctl status nginx

配置nginx：
/etc/nginx/nginx.conf

```bash
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log;
pid /run/nginx.pid;

# Load dynamic modules. See /usr/share/doc/nginx/README.dynamic.
include /usr/share/nginx/modules/*.conf;

events {
    worker_connections 1024;
}

http {
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile            on;
    tcp_nopush          on;
    tcp_nodelay         on;
    keepalive_timeout   65;
    types_hash_max_size 4096;

    include             /etc/nginx/mime.types;
    default_type        application/octet-stream;

    # Load modular configuration files from the /etc/nginx/conf.d directory.
    # See http://nginx.org/en/docs/ngx_core_module.html#include
    # for more information.
    include /etc/nginx/conf.d/*.conf;
    
    upstream demoapp {
      server localhost:8080;
    }
    
    server {
        listen       80;
        listen       [::]:80;
        server_name  _;
        gzip_buffers 4 16k;      #设置gzip申请内存的大小,其作用是按块大小的倍数申请内存空间,param2:int(k) 后面单位是k。这里设置以16k为单位,按照原始数据大小
        以16k为单位的4倍申请内存
        gzip_http_version 1.1;   #识别http协议的版本,早起浏览器可能不支持gzip自解压,用户会看到乱码
        gzip_comp_level 9;       #设置gzip压缩等级，等级越底压缩速度越快文件压缩比越小，反之速度越慢文件压缩比越大；等级1-9，最小的压缩最快 但是消耗cpu
        gzip_types text/plain application/x-javascript text/css application/xml text/javascript application/x-httpd-php application/javascript;
        gzip_vary on;            #启用应答头"Vary: Accept-Encoding"

        # Load configuration files for the default server block.
        include /etc/nginx/default.d/*.conf;

        error_page 404 /404.html;
        location = /404.html {
        }

        error_page 500 502 503 504 /50x.html;
        location = /50x.html {
        }

        location / {
            proxy_pass http://demoapp/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr; #保留代理之前的真实客户端ip
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; #记录代理过程

        }

        # 静态文件配置
        location /static/ {
           root /data/app/front;
        }
        
#        location / {
#            proxy_pass http://127.0.0.1:8080$request_uri;
#        }

        location /assets {
            alias /data/app/front/assets;
        }
    }
}
```

### 重新加载配置
sudo nginx -s reload
或
sudo systemctl reload nginx



## 创建docker网络
docker network create mynet

## grafana
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

## mysql
docker pull mysql:5.7
docker run -d -p 3306:3306 -v /data/mysql/conf:/etc/mysql -v /data/mysql/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=1234Qweh --name mysql01 --network mynet --network-alias mysql01 mysql:5.7

### 登录mysql管理
docker ps
docker exec -it 9b1451b721fe mysql -u root -p

## redis
docker search redis
docker pull redis:latest
docker run -d -p 6379:6379 --network mynet --network-alias redis01 --name redis01 redis:latest

### 登录redis管理
docker ps
docker exec -it 32320064d6f0 redis-cli

## 其它准备
### 文件夹路径准备好
mkdir -p /data/app/config

# 前端部署
cd ui/demo-ui 
sh deploy.sh

# 部署后端
sh src/main/resources/docker/deploy.sh

