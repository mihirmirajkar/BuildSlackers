#!/bin/sh
export ANSIBLE_HOST_KEY_CHEKCING=False
rm ./ansible/inventory
node ./aws_main.js`
ansible-playbook./ansible/playbook.yml -i ./ansible/inventory
