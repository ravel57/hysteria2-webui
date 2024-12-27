## Run:
```
sudo docker build -t hysteria2-webui . && \
sudo docker run --name hysteria2-webui -d \
    --restart unless-stopped \
    -p 51841:51841 \
    --env PORT=51841 \
    --env PASSWORD='<YOUR_PASSWORD>' \
    --volume /etc/hysteria/:/etc/hysteria/ \
    hysteria2-webui
```
