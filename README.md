### Run:
```
sudo docker build --no-cache -t hysteria2-webui . && \
sudo docker run --name hysteria2-webui -d \
    --restart unless-stopped \
    -p 51841:51841 \
    -p 51840:51840/tcp \
    -p 51840:51840/udp \
    -p 80:80/udp \
    --env PORT=51841 \
    --env HYSTERIA_PORT=51840 \
    --env PASSWORD='<YOUR_PASSWORD>' \
    --env HOST_URL='<YOUR_IP_OR_DOMAIN>' \
    --volume ~/hysteria2-webui/:/etc/hysteria/ \
    hysteria2-webui
```
