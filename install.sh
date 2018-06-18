#!/usr/bin/env bash
cd ~
mkdir .ktm
cd .ktm
mkdir bin
mkdir modules
cd modules
mkdir ktm
cd ktm
mkdir 0.0.1
wget https://github.com/ghostbuster91/ktm/releases/download/0.0.1/ktm.tar
tar xf ktm.tar
rm ktm.tar
mv ktm 0.0.1
cd ../..
ln -s ~/.ktm/modules/ktm/0.0.1/ktm/ bin/ktm
echo 'Past following line into .bashrc / .zshrc etc'
echo 'PATH=$PATH:~/.ktm/bin'
echo 'export $PATH'
