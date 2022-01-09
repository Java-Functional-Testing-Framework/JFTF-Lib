#!/bin/bash
# ©2022 JFTF
# JFTF CMDB environment generation script
# Prerequisites
# (MariaDB DBMS)
# Existing root user with full privileges
# Version 1.0

jftf_user_password=$1
jftf_user_ip=$2

function init_jftf_cmdb(){
  echo
  sudo mariadb -e "CREATE OR REPLACE USER jftf@$jftf_user_ip IDENTIFIED BY '$jftf_user_password';"
  if [ $? == 0 ]
  then
    echo "JFTF user created successfully!"
  else
    echo "Failed to create JFTF user!"
    exit 1;
  fi
  echo

  sudo mariadb -e "DROP DATABASE IF EXISTS jftf_cmdb;";
  sudo mariadb -e "CREATE OR REPLACE DATABASE jftf_cmdb;";
    if [ $? == 0 ]
    then
      echo "JFTF CMDB created successfully!"
    else
      echo "Failed to create JFTF CMDB!"
      exit 1;
    fi
  echo

  sudo mariadb -e "GRANT ALL ON jftf_cmdb.* TO jftf@$jftf_user_ip IDENTIFIED BY '$jftf_user_password' WITH GRANT OPTION;"
  if [ $? == 0 ]
  then
    echo "JFTF user granted database permissions!"
  else
    echo "Failed to grant JFTF user database permissions!"
    exit 1;
  fi
  sudo mariadb -e "FLUSH PRIVILEGES;"
  echo

  sudo mariadb < ../sql/create_db.sql
  if [ $? == 0 ]
  then
    echo "Created database table schema successfully!"
  else
    echo "Failed to create database table schema!"
    exit 1;
  fi
  echo

    sudo mariadb < ../sql/create_views.sql
    if [ $? == 0 ]
    then
      echo "Created database views schema!"
    else
      echo "Failed to create database views schema!"
      exit 1;
    fi
  exit 0;
}

if [ "$EUID" -ne 0 ]
  then echo "Please run the script as root!"
  exit
fi

if [ -z "$jftf_user_password" ] || [ -z "$jftf_user_ip" ]
then
  echo "Script usage is: database_init.sh <jftf_user_password> <jftf_user_ip>"
  exit 1;
fi

echo "jftf_user_password: $jftf_user_password"
echo "jftf_user_ip: $jftf_user_ip"
echo
echo "WARNING!!!"
echo "This script will drop and reset the JFTF CMDB database! Continue at your own RISK!!!"
while true
do
    read -r -p 'Do you want to continue? ' choice
    case "$choice" in
      n|N) break;;
      y|Y) init_jftf_cmdb;;
      *) echo 'Response not valid';;
    esac
done
