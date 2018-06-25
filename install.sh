#!/usr/bin/env bash
set -e
test ! -e ~/.ktm || (echo "~/.ktm exists!"; exit 1)
mkdir -p ~/.ktm/modules/com.github.ghostbuster91/ktm/0.0.3
mkdir ~/.ktm/bin
echo "ktm:com.ghostbuster91" >> ~/.ktm/aliases
cd ~/.ktm/modules/com.github.ghostbuster91/ktm
wget https://github.com/ghostbuster91/ktm/releases/download/0.0.3/ktm.tar
tar xf ktm.tar
rm ktm.tar
mv ktm 0.0.3
ln -s ~/.ktm/modules/com.github.ghostbuster91/ktm/0.0.3/ktm/bin/ktm ~/.ktm/bin/ktm
echo 'Past following line into .bashrc / .zshrc etc'
echo 'export PATH=$PATH:~/.ktm/bin'
