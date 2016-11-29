#!/bin/sh
node ./aws_main.js
ansible-playbook ./ansible/bs.yml -i ./ansible/inventory
