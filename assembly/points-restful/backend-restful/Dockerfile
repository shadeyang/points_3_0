FROM openjdk:11-jdk-slim

MAINTAINER shade.yang@aliyun.com

USER root

ARG filename
ARG app_path="/opt"
ARG main_class="com.wt2024.points.restful.backend.ProviderLauncher"
ARG run_args="--spring.profiles.active=docker"
ARG java_opts="-Dsun.misc.URLClassPath.disableJarChecking=true -Djava.security.edg=file:/dev/./urandom -XX:+UnlockExperimentalVMOptions -Dfile.encoding=utf-8"

RUN mkdir -p ${app_path}/apps && \
    chmod 755 -R ${app_path} && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo 'Asia/Shanghai' > /etc/timezone

COPY target/${filename} ${app_path}/apps/app.jar

ENV APP_JAR="${app_path}/apps/app.jar" \
    MAIN_CLASS="${main_class}" RUN_ARG="${run_args}" JAVA_OPTS="${java_opts}"

ENTRYPOINT java -server ${JAVA_OPTS} -jar ${APP_JAR} ${RUN_ARG}