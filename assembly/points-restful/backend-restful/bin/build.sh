#!/bin/bash

base_dir=$(dirname $0)
echo $base_dir

mvn clean package dockerfile:build -Pdocker -f $base_dir/../pom.xml