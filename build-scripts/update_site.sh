#!/bin/sh
DIRECTORY=$(cd `dirname $0` && pwd)

GIT_SITE_DIR=$DIRECTORY/../config/site
LOCAL_SITE_DIR=/var/www-download

files='*.php tile_sources.xml favicon.ico'
for f in $files ; do
	cp $GIT_SITE_DIR/$f $LOCAL_SITE_DIR/ -u;
done

cp $GIT_SITE_DIR/resource/* $LOCAL_SITE_DIR/resource/* -u;

