#!/bin/bash

base_dir=$(dirname $0)
echo $base_dir

instance_name=`dd bs=4 count=1 if=/dev/urandom 2>/dev/null | xxd -p `
echo $instance_name

filename=$base_dir/../../../../pom.xml

pwd $filename
start=`grep -n "<properties>" $filename|head -n 1|awk -F ":" '{print $1}'`
end=`grep -n "</properties>" $filename|head -n 1|awk -F ":" '{print $1}'`
parent=`sed -n $start,$end"p" $filename`
# echo $parent
version=`echo $parent|awk -F "<points.version>" 'BEGIN{IGNORECASE=1} {print $2}'|awk -F "</points.version>" 'BEGIN{IGNORECASE=1} {print $1}'`
echo $version

docker run --name backend-restful_$instance_name -d -p 9070:9080 com.wt2024.points/backend-restful:$version