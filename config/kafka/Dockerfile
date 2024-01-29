# 基础镜像使用java
FROM openjdk:8
# 作者
LABEL author=rick<rick.wl@demo.com>
# 自定义标签，用于查询image的时候过滤：docker images -f label=type=kafka-server -q
LABEL type=@APP_NAME@
# 解压kafka
ADD ./@KAFKA_PATH_NAME@ kafka
WORKDIR kafka

# 执行安装、启动
#RUN bash -c "/kafka/bin/kafka-storage.sh format -t $(/kafka/bin/kafka-storage.sh random-uuid) -c /kafka/config/kraft/server.properties"
RUN bash -c "/kafka/bin/kafka-storage.sh format -t @CLUSTER_ID@ -c /kafka/config/kraft/server.properties"
ENTRYPOINT ["/kafka/bin/kafka-server-start.sh","/kafka/config/kraft/server.properties"]
