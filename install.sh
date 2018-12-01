#!/usr/bin/env bash
set -e

function install() {
    VERSION="0.0.6"
    test ! -e ~/.ktm || (echo "~/.ktm exists!"; exit 1)
    mkdir -p ~/.ktm/modules/com.github.ghostbuster91/ktm/${VERSION}/ktm
    mkdir ~/.ktm/bin
    echo "ktm com.github.ghostbuster91:ktm" >> ~/.ktm/aliases
    cd ~/.ktm/modules/com.github.ghostbuster91/ktm
    wget https://github.com/ghostbuster91/ktm/releases/download/${VERSION}/ktm.tar
    tar xf ktm.tar
    rm ktm.tar
    mv ktm-${VERSION}/* ${VERSION}/ktm
    rm -rf ktm-${VERSION}
    ln -s ~/.ktm/modules/com.github.ghostbuster91/ktm/${VERSION}/ktm/bin/ktm ~/.ktm/bin/ktm
}

function printPostInstallationNote() {
    RED='\033[0;31m'
    NC='\033[0m' # No Color
    ORANGE='\033[1;33m'
    echo
    printf ${RED}
    printf "Remember to paste following line into your .bashrc/.zshrc,\n"
    printf "or other corresponding file for your shell:\n"
    printf ${ORANGE}
    printf "    export PATH=\$PATH:~/.ktm/bin $NC"
}

install
printPostInstallationNote