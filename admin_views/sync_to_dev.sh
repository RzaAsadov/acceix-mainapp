#!/bin/bash

/usr/bin/rsync -vr -i -t --delete --exclude 'sync_to_dev.sh' --exclude 'Crud-templates.code-workspace' --exclude '.vscode' /Users/zrid/Documents/Acceix/NetBeansProjects/acceix-framework/acceix-mainapp/admin_views zrid@dev:/home/zrid/acceix-framework/acceix-mainapp/

