#!/usr/bin/env bash
set -e

# Build the frontend application with vue-cli
cd client
npm run build

# Remove all previously build output from static resources
cd ..
rm -rf ./src/main/resources/static/*
rm -f ./src/main/resources/templates/index.html

# Copy the newly build application to the static resources
cp -r ./client/dist/* ./src/main/resources/static
mv ./src/main/resources/static/index.html ./src/main/resources/templates/index.html