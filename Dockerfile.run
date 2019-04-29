#
# NCANode Dockerfile for run NCANode
#
# The NCANode Dockerfile for run ncanode app in container
# ----------------------------------------------------
# Build image with command: docker build -t malikzh/ncanode .
# Run container with command: docker run -dti -p 14579:14579 malikzh/ncanode
# ----------------------------------------------------
# Author: @malikzh ( https://github.com/malikzh )
# License: MIT
#

FROM ubuntu:16.04

# Installing dependencies
RUN apt-get update && apt-get install -y default-jre crudini curl tzdata jq

# Set timezone Asia/Almaty
RUN ln -fs /usr/share/zoneinfo/Asia/Almaty /etc/localtime && dpkg-reconfigure -f noninteractive tzdata

# Set working dir for NCANode
WORKDIR /NCANode

# Downloading the latest NCANode
RUN curl $(curl -s https://api.github.com/repos/malikzh/NCANode/releases/latest | jq '.assets | .[] | .browser_download_url' -r | grep -s '\.tar.gz$') -L -o NCANode.tar.gz

# Extract NCANode.tar.gz
RUN tar -xzvf NCANode.tar.gz . && rm -f NCANode.tar.gz

# Redirect all I/O
RUN ln -sf /dev/stdout logs/request.log && ln -sf /dev/stderr logs/error.log

# Set configuration for listen on all interfaces
RUN crudini --set NCANode.ini http ip 0.0.0.0

# Run the NCANode
CMD ["./NCANode.sh"]