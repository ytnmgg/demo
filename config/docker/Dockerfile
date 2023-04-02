# 基础镜像使用java
FROM openjdk:8
# 作者
LABEL author=rick<rick.wl@demo.com>
# 自定义标签，用于查询image的时候过滤：docker images -f label=type=demoapp -q
LABEL type=@DOCKER_CONF_APP_NAME@
# VOLUME 指定了临时文件目录为/tmp。
# 其效果是在主机 /var/lib/docker 目录下创建了一个临时文件，并链接到容器的/tmp
# VOLUME /tmp
# 将jar包添加到容器中并更名为app.jar
ADD @DOCKER_CONF_JAR_NAME@ @DOCKER_CONF_APP_NAME@.jar
# 环境变量
ENV profile.active prod
ENV mysql.host @DOCKER_CONF_MYSQL_HOST@
ENV mysql.user @DOCKER_CONF_MYSQL_USER@
ENV mysql.pwd @DOCKER_CONF_MYSQL_PWD@
ENV host.inner @DOCKER_CONF_HOST_INNER@
ENV nacos.cluster @DOCKER_CONF_NACOS_CLUSTER@
# dubbo服务注册IP
ENV DUBBO_IP_TO_REGISTRY @DOCKER_CONF_HOST_INNER@
# 宿主机docker网卡ip，以及docker remote api的映射端口
ENV docker.remote.host http://172.17.0.1:8989
# 宿主机docker remote api 版本
ENV docker.remote.host.version v1.26
# 容器中mount的宿主机nginx日志文件夹地址（run命令中会映射到host日志地址：/var/log/nginx）
ENV host.nginx.log.path /var/log/nginx
# 前端文件版本
ENV front.version v0.0.1
ENV front.sso.version v0.0.1
# redis host
ENV redis.host @DOCKER_CONF_REDIS_HOST@
# 运行jar包
RUN bash -c 'touch /@DOCKER_CONF_APP_NAME@.jar'
# 创建app日志文件夹
RUN bash -c 'mkdir -p /var/log/app'
# 创建nginx日志文件夹
RUN bash -c 'mkdir -p /var/log/nginx'
ENTRYPOINT ["java", \
    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=@DOCKER_CONF_DEBUG_PORT@", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Duser.timezone=GMT+08", \
    "-Dfile.encoding=UTF8", \
    "-Dproject.name=@DOCKER_CONF_APP_NAME@", \
    "-jar", "/@DOCKER_CONF_APP_NAME@.jar"]

