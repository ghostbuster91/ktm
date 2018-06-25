#!/usr/bin/env bash
set -e
VERSION="0.0.4"
test ! -e ~/.ktm || (echo "~/.ktm exists!"; exit 1)
mkdir -p ~/.ktm/modules/com.github.ghostbuster91/ktm/${VERSION}/ktm
mkdir ~/.ktm/bin
echo "ktm com.github.ghostbuster91:ktm" >> ~/.ktm/aliases
cd ~/.ktm/modules/com.github.ghostbuster91/ktm
wget https://github.com/ghostbuster91/ktm/releases/download/${VERSION}/ktm.tar
tar xf ktm.tar
rm ktm.tar
mv ktm-${VERSION}/* ${VERSION}/ktm
ln -s ~/.ktm/modules/com.github.ghostbuster91/ktm/${VERSION}/ktm/bin/ktm ~/.ktm/bin/ktm
echo 'Past following line into .bashrc / .zshrc etc'
echo 'export PATH=$PATH:~/.ktm/bin'
