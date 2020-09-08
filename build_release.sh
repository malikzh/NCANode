#!/usr/bin/env bash

#
# Build script for CI
#

if [ "$1" == "" ]; then
    echo "Version not specified"
    exit
fi

RELEASE_DIR="release_v${1}"
NCANODE_ORIGINAL_JAR="./target/ncanode-jar-with-dependencies.jar"

if [ ! -f "$NCANODE_ORIGINAL_JAR" ]; then
  echo "Release $NCANODE_ORIGINAL_JAR not exists"
  exit
fi

mkdir $RELEASE_DIR && \
cp $NCANODE_ORIGINAL_JAR "$RELEASE_DIR/NCANode.jar" && \
cp "./NCANode.bat" "$RELEASE_DIR"  && \
cp "./NCANode.sh" "$RELEASE_DIR"  && \
chmod +x "$RELEASE_DIR/NCANode.sh" && \
cp "./NCANode.ini" "$RELEASE_DIR"  && \
cp "./NCANode.postman_collection.json" "$RELEASE_DIR"  && \
cp "./NCANode_2.0.postman_collection.json" "$RELEASE_DIR"  && \
cp "./README.md" "$RELEASE_DIR"  && \
cp "./LICENSE" "$RELEASE_DIR"  && \
cp -r "./docs" "$RELEASE_DIR"  && \
mkdir -p "$RELEASE_DIR/ca/root" && \
mkdir -p "$RELEASE_DIR/ca/trusted" && \
mkdir -p "$RELEASE_DIR/cache/crl" && \
mkdir "$RELEASE_DIR/logs" && \
touch "$RELEASE_DIR/logs/error.log" && \
touch "$RELEASE_DIR/logs/request.log" && \
curl -k -L -o "$RELEASE_DIR/ca/root/root_rsa.crt" https://pki.gov.kz/cert/root_rsa.crt && \
curl -k -L -o "$RELEASE_DIR/ca/root/root_gost.crt" https://pki.gov.kz/cert/root_gost.crt && \
curl -k -L -o "$RELEASE_DIR/ca/trusted/pki_rsa.crt" https://pki.gov.kz/cert/pki_rsa.crt && \
curl -k -L -o "$RELEASE_DIR/ca/trusted/pki_gost.crt" https://pki.gov.kz/cert/pki_gost.crt && \
curl -k -L -o "$RELEASE_DIR/ca/trusted/nca_rsa.crt" https://pki.gov.kz/cert/nca_rsa.crt && \
curl -k -L -o "$RELEASE_DIR/ca/trusted/nca_gost.crt" https://pki.gov.kz/cert/nca_gost.crt && \
cd "$RELEASE_DIR" && \
zip "../NCANode.zip" -r . && \
tar cvzf "../NCANode.tar.gz" . && \
cd .. && \
rm -r "$RELEASE_DIR"

md5sum "NCANode.zip" "NCANode.tar.gz"
sha1sum "NCANode.zip" "NCANode.tar.gz"

echo "Build OK"
