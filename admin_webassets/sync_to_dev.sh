#!/bin/bash

/usr/bin/rsync -vr -i -t --delete --exclude 'sync_to_dev.sh' --exclude 'Crud-webassets.code-workspace' --exclude '.vscode' /Users/zrid/Documents/Acceix/NetBeansProjects/acceix-framework/acceix-mainapp/admin_webassets zrid@dev1.asadov.me:/home/zrid/acceix-framework/acceix-mainapp/

