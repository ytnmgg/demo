# 基础镜像使用java
FROM openjdk:8
# 作者
LABEL author=rick<rick.wl@antfin.com>
# 解压kafka
ADD ./kafka_2.13-3.3.1 kafka
WORKDIR kafka

# 执行安装、启动
RUN bash -c "bin/kafka-storage.sh format -t $(bin/kafka-storage.sh random-uuid) -c config/kraft/server.properties"
ENTRYPOINT ["bin/kafka-server-start.sh","config/kraft/server.properties"]
