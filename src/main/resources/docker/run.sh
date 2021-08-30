docker build -t demoapp:0.1 .
docker run -d -p 8080:8080 -v /data/app/log:/var/log/app -v /var/log:/var/host-log --name demoapp --network mynet --network-alias demoapp demoapp:0.1
