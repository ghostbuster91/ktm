#!/usr/bin/env bash
cd ~
mkdir -p .ktm/.modules/com.github.ghostbuster91:ktm/0.0.1
mkdir .ktm/bin
echo "ktm:com.ghostbuster91" >> .ktm/aliases
cd .ktm/modules/com.github.ghostbuster91:ktm
wget https://github.com/ghostbuster91/ktm/releases/download/0.0.1/ktm.tar
tar xf ktm.tar
rm ktm.tar
mv ktm 0.0.1
ln -s ~/.ktm/modules/com.github.ghostbuster91:ktm/0.0.1/ktm/bin/ktm ~/.ktm/bin/ktm
echo 'Past following line into .bashrc / .zshrc etc'
echo 'export PATH=$PATH:~/.ktm/bin'