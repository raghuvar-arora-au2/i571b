#!/bin/bash

dir=`dirname $0`
MAIN_FILE="App.java"
MAIN_CLASS="App"

PROJECT_HOME="./"
PATH_TO_MAIN_FILE="$PROJECT_HOME/parser/src"
PATH_TO_CLASS_FILE="$PROJECT_HOME/parser/bin"
#On the VM - i571b -> /home/apal5/projects/i571b

read INPUT;
echo "Input Read $INPUT";
 

#Compilation
echo "-------------Compilation-------------"
cd ${PATH_TO_MAIN_FILE}
echo "javac ${PATH_TO_MAIN_FILE}/${MAIN_FILE} -d ${PATH_TO_CLASS_FILE}"
javac ${MAIN_FILE} -d ${PATH_TO_CLASS_FILE}

cd ${dir}

#Run
cd ${PATH_TO_CLASS_FILE}
echo "-------------Execution-------------"
java ${MAIN_CLASS} $INPUT;
cd ${dir}