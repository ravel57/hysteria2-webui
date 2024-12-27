#!/bin/bash

path=$(pwd)
cd ../../WebstormProjects/hysteria-web-ui || exit

npm install
npx vite build

if [ -d "./dist/" ]; then
    cp -r "./dist/"* "$path/src/main/resources/static"
else
    echo "Copping error" >&2
    exit 1
fi