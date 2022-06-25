#!/bin/bash
set -e

baseDir=$(dirname $0)
echo "当前目录"$baseDir

function change_sql(){
# 仅查找目录下文件夹下sql，若不需要转换则直接防止到根目录下
  if [ $1 == $sourceDir ]
    then
      return
    else
      echo $1 $2 $3
      targetTempDir=${1/$sourceDir/$targetDir}
      echo $targetTempDir
      if [ ! -d $targetTempDir ]; then
        mkdir -p $targetTempDir
      fi

      if [[ $3 = *'.sql' ]]; then
        sourceFile=$1"/"$3
        sourceFileBak=$sourceFile".bak"
        targetFile=$targetTempDir"/"$3
        echo "修正原始sql文件"$sourceFile
        sed 's/UNIQUE INDEX/UNIQUE KEY/g' $sourceFile|sed '/ENGINE/d'|sed '/DEFAULT CHARACTER SET/d'|sed '/COLLATE/d' > $sourceFileBak
        java -version
        echo "输出h2文件"$targetFile
        java -jar $baseDir/mysql2h2-converter-tool-0.1-SNAPSHOT.jar $sourceFileBak > $targetFile
      else
        echo $3"不是sql文件"
      fi
  fi
}

function read_dir(){
  echo "查找待转化的sql脚本"

  for file in `ls $1` #注意此处这是两个反引号，表示运行系统命令
  do
    if [ -d $1"/"$file ] #注意此处之间一定要加上空格，否则会报错
      then
        read_dir $1"/"$file $2
      else
        change_sql $1 $2 $file
    fi
  done
}

dir=$(ls -l $baseDir/.. |awk '/^d/ {print $NF}')
for i in $dir
do
  baseSqlDir=$baseDir/../$i/sql
  sourceDir=$baseSqlDir/mysql
  targetDir=$baseSqlDir/../src/main/resources/h2
  if [ -d $baseSqlDir ]
    then
      if [ -d $baseSqlDir ]
        then
          echo "删除"$targetDir"之前的h2sql"
          rm -rf $targetDir
      fi
      read_dir $sourceDir $targetDir
    else
      echo $i"没有sql文件夹"
      continue
  fi
  echo $i
done

#/Applications/IntelliJ\ IDEA\ CE.app/Contents/plugins/maven/lib/maven3/bin/mvn clean compile -f ../pom.xml