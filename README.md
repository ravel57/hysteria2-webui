### Run:
```
sudo docker build --no-cache -t hysteria2-webui . && \
sudo docker run --name hysteria2-webui -d \
    --restart unless-stopped \
    -p 51841:51841 \
    -p 443:443/tcp \
    -p 443:443/udp \
    --env PORT=51841 \
    --env PASSWORD='<YOUR_PASSWORD>' \
    --env HOST_URL='<YOUR_IP_OR_DOMAIN>' \
    --volume ~/hysteria2-webui/:/etc/hysteria/ \
    hysteria2-webui
```
