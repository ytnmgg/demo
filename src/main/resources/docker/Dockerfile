# 基础镜像使用java
FROM java:8
# 作者
MAINTAINER rick <rick.wl@antfin.com>
# VOLUME 指定了临时文件目录为/tmp。
# 其效果是在主机 /var/lib/docker 目录下创建了一个临时文件，并链接到容器的/tmp
# VOLUME /tmp
# 将jar包添加到容器中并更名为app.jar
ADD demo-0.0.1-SNAPSHOT.jar app.jar
# 环境变量
ENV profile.active prod
ENV mysql.host mysql01
ENV mysql.user root
ENV mysql.pwd
# 宿主机docker网卡ip，以及docker remote api的映射端口
ENV docker.remote.host http://172.17.0.1:8989
# 宿主机docker remote api 版本
ENV docker.remote.host.version v1.26
# 容器中mount的宿主机日志文件夹地址（run命令中会映射到host日志地址：/var/log）
ENV host.log.path /var/host-log
# 运行jar包
RUN bash -c 'touch /app.jar'
# 创建日志文件夹
RUN bash -c 'mkdir -p /var/log/app'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Duser.timezone=GMT+08","-Dfile.encoding=UTF8","-jar","/app.jar"]
