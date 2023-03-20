#!/bin/bash

. src/main/resources/bin/common.sh

host1=139.224.72.37
host2=106.14.208.194
host1_inner=172.27.183.154
host2_inner=172.27.183.155
index1='01'
index2='02'

seed_hosts="${host1_inner}:9300,${host2_inner}:9300"
initial_master_nodes="es01,es02"

function install_es(){
  host=$1
  host_inner=$2
  i=$3

  esName="es${i}"
  esExporterName="esExporter${i}"
  kibanaName="kib${i}"
  filebeatName="fb${i}"

  rmc $host "${esName}"
  rmc $host "${esExporterName}"
  rmc $host "${kibanaName}"
  rmc $host "${filebeatName}"

  run $host "rm -rf /data/es/data/*"
  run $host "rm -rf /data/es/logs/*"

  run $host "docker run -d -p 9200:9200 -p 9300:9300 \
    -v /data/es/certs:/usr/share/elasticsearch/config/certs \
    -v /data/es/data:/usr/share/elasticsearch/data \
    -v /data/es/logs:/usr/share/elasticsearch/logs \
    -v /data/es/plugins:/usr/share/elasticsearch/plugins \
    -e cluster.name=demo-es-cluster \
    -e node.name=${esName} \
    -e http.port=9200 \
    -e transport.port=9300 \
    -e network.publish_host=${host_inner} \
    -e discovery.seed_hosts=${seed_hosts} \
    -e cluster.initial_master_nodes=${initial_master_nodes} \
    -e node.roles=master,data,ingest,transform \
    -e ES_JAVA_OPTS='-Xms10g -Xmx10g' \
    -e bootstrap.memory_lock=true \
    -e xpack.security.enabled=true \
    -e xpack.security.http.ssl.enabled=true \
    -e xpack.security.http.ssl.key=certs/${esName}/${esName}.key \
    -e xpack.security.http.ssl.certificate=certs/${esName}/${esName}.crt \
    -e xpack.security.http.ssl.certificate_authorities=certs/ca/ca.crt \
    -e xpack.security.http.ssl.verification_mode=certificate \
    -e xpack.security.transport.ssl.enabled=true \
    -e xpack.security.transport.ssl.key=certs/${esName}/${esName}.key \
    -e xpack.security.transport.ssl.certificate=certs/${esName}/${esName}.crt \
    -e xpack.security.transport.ssl.certificate_authorities=certs/ca/ca.crt \
    -e xpack.security.transport.ssl.verification_mode=certificate \
    -e xpack.license.self_generated.type=basic \
    --ulimit memlock=-1:-1 \
    --name ${esName} --network mynet --network-alias ${esName}  \
    docker.elastic.co/elasticsearch/elasticsearch:8.5.3"

  p "sleep 10s for es starting"
  sleep 10s
  check_d $host $esName
}

function install_filebeat() {
  host=$1
  host_inner=$2
  i=$3
  pwd=$4

  # https://www.elastic.co/guide/en/beats/filebeat/8.5/filebeat-installation-configuration.html
  p "copy template filebeat config file to host"
  copy $host "src/main/resources/config/filebeat.docker.yml" "/data/es/config/filebeat.docker.yml"
  copy $host "src/main/resources/config/filebeat-fields.yml" "/data/es/config/filebeat-fields.yml"

  # 模板文件内容变量处理
  filebeatName="fb${i}"
  replace $host "/data/es/config/filebeat.docker.yml" "@FILEBEAT_NAME@" $filebeatName
  replace $host "/data/es/config/filebeat.docker.yml" "@ES_HOST_MAIN@" $host_inner
  replace $host "/data/es/config/filebeat.docker.yml" "@ELASTIC_PASSWORD@" $pwd

  rmc $host $filebeatName

  run $host "chmod go-w /data/es/config/filebeat.docker.yml"

  run $host "docker run -d \
  --name ${filebeatName} --network mynet --network-alias ${filebeatName} \
  --user=root \
  --volume=/var/log/nginx:/var/log/nginx:ro \
  --volume=/var/log/app:/var/log/app:ro \
  --volume=/data/es/config/filebeat.docker.yml:/usr/share/filebeat/filebeat.yml:ro \
  --volume=/data/es/config/filebeat-fields.yml:/usr/share/filebeat/filebeat-fields.yml:ro \
  --volume=/data/es/certs/ca/ca.crt:/usr/share/filebeat/ca.crt:ro \
  --volume=/etc/localtime:/etc/localtime:ro \
  docker.elastic.co/beats/filebeat:8.5.3 filebeat -e --strict.perms=false"

  p "sleep 10s for filebeat starting"
  sleep 10s
  check_d $host $filebeatName
}

function  gen_es_pwd() {
  host=$1
  name=$2
  file_name="/data/es/pwd_${name}"
  touch file_name

  # 可能有一些warning信息，取最后一行
  run $host "docker exec -t es01 /usr/share/elasticsearch/bin/elasticsearch-reset-password -b -f -s -u ${name} | sed -n '\$p'" pwd

  # 删除不可见字符
  #echo $pwd| awk '{print length($0)}'
  removeInvisible $pwd pwd_clean
  #echo $pwd_clean | awk '{print length($0)}'

  # 存入文件中
  run $host "echo ${pwd_clean} > ${file_name}"
}

function install_es_exporter() {
  # https://github.com/prometheus-community/elasticsearch_exporter
  host=$1
  i=$2
  pwd=$3

  name="esExporter${i}"
  esName="es${i}"

  rmc $host $name

  run $host "docker run -d -e TZ=Asia/Shanghai -p 9114:9114 \
  --name ${name} --network mynet --network-alias ${name} \
  --volume=/data/es/certs/ca/ca.crt:/tmp/ca.crt:ro \
  quay.io/prometheuscommunity/elasticsearch-exporter:latest \
  --es.uri=https://elastic:${pwd}@${esName}:9200 \
  --es.ca=/tmp/ca.crt \
  --es.all \
  --es.ssl-skip-verify"

  check_d $host $name
}

function install_kibana() {
  host=$1
  esName="es01"
  name="kib01"

  run $host "rm -rf /data/es/kibanadata/*"

  run $host "mkdir -p /data/es/kibanadata"
  run $host "chmod -R 777 /data/es/kibanadata"

  #从文件中取出密码
  run $host "cat /data/es/pwd_kibana_system" pwd_kibana_system

  rmc $host $name

  run $host "docker run -d -p 5601:5601 \
  -v /data/es/certs:/usr/share/kibana/certs \
  -v /data/es/kibanadata:/usr/share/kibana/data \
  -e ELASTICSEARCH_HOSTS=https://${esName}:9200 \
  -e ELASTICSEARCH_USERNAME=kibana_system \
  -e ELASTICSEARCH_PASSWORD=${pwd_kibana_system} \
  -e ELASTICSEARCH_SSL_CERTIFICATEAUTHORITIES=/usr/share/kibana/certs/ca/ca.crt \
  --name ${name} --network mynet --network-alias ${name}  \
  docker.elastic.co/kibana/kibana:8.5.3"

  check_d $host $name
}

# 安装ES
p "install es on hosts"
install_es $host1 $host1_inner $index1
install_es $host2 $host2_inner $index2

# 调用ES工具生成密码
p "generate password for user elastic and store in file"
gen_es_pwd $host1 'elastic'
p "generate password for user kibana_system and store in file"
gen_es_pwd $host1 'kibana_system'

# 调用ES API创建index
# TODO

# 安装filebeat
p "install filebeat on hosts"
run $host1 "cat /data/es/pwd_elastic" pwd_elastic
install_filebeat $host1 $host1_inner $index1 $pwd_elastic
install_filebeat $host2 $host1_inner $index2 $pwd_elastic

# 安装 Elasticsearch Exporter （监控es）
p "install filebeat on main host"
install_es_exporter $host1 $index1 $pwd_elastic

# 安装 kibana
p "install kibana on main host"
install_kibana $host1