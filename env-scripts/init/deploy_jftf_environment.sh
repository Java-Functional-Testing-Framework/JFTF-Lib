#!/bin/bash
# ©2023 JFTF
# JFTF environment deployment script
# Usage
# sudo ./deploy_jftf_environment.sh
# Version 1.0

APT_PACKAGES="python3-venv python3-dev python3-pip mariadb-server libmariadb-dev-compat libmariadb-dev libssl-dev memcached libmemcached-tools rsyslog ksystemlog"
DATABASE_NAME="jftf_cmdb"
DATABASE_USER="jftf"
DATABASE_PASSWORD="jftf_development"

function install_apt_package(){
  echo "Checking installation of $1"
  if [ "$(dpkg-query -W -f='${Status}' "$1" 2>/dev/null | grep -c "ok installed")" -eq 0 ]; then
    apt-get install "$1";
  else
    echo "Package $1 already installed on the system!";
  fi
}

function install_apt_dependencies(){
  echo "Installing required apt packages"
  for PACKAGE in $APT_PACKAGES; do
    install_apt_package "$PACKAGE"
  done
}

function my_sql_secure(){
  echo "Running My SQL secure installation script";
  sudo mysql_secure_installation;
}

function configure_database(){
  echo "Configuring MariaDB development database and development user";
  sudo mariadb -e "CREATE OR REPLACE USER $DATABASE_USER@localhost IDENTIFIED BY '$DATABASE_PASSWORD';";
  if [ $? ]; then
    echo "JFTF development user created successfully!";
  else
    echo "Failed to create JFTF development user!";
    exit 1;
  fi
  sudo mariadb -e "DROP DATABASE IF EXISTS $DATABASE_NAME;";
  sudo mariadb -e "CREATE OR REPLACE DATABASE $DATABASE_NAME;";
  if [ $? ]; then
    echo "JFTF development database created successfully!";
  else
    echo "Failed to create JFTF development database!";
    exit 1;
  fi
  sudo mariadb -e "GRANT ALL ON $DATABASE_NAME.* TO $DATABASE_USER@localhost IDENTIFIED BY '$DATABASE_PASSWORD' WITH GRANT OPTION;";
  if [ $? ]; then
    echo "JFTF development user granted development database permissions!";
  else
    echo "Failed to grant JFTF development user development database permissions!";
    exit 1;
  fi
  sudo mariadb -e "FLUSH PRIVILEGES;";
}

function configure_rsyslog_remote_logging(){
  echo "Reconfiguring rsyslog daemon configuration file to allow remote logging";
  if [ ! -f /etc/rsyslog.conf ]; then
    echo "rsyslog.conf not found";
    exit 1;
  fi
  sed -i 's/#module(load="imudp")/module(load="imudp")/' /etc/rsyslog.conf;
  sed -i 's/#input(type="imudp" port="514")/input(type="imudp" port="514")/' /etc/rsyslog.conf;

  # shellcheck disable=SC2016
  if ! grep -q '$AllowedSender UDP, 127.0.0.1' /etc/rsyslog.conf; then
    # shellcheck disable=SC2016
    echo '$AllowedSender UDP, 127.0.0.1' >> /etc/rsyslog.conf
  fi

  sudo systemctl restart rsyslog.service;
  echo "Rsyslog daemon configration successful!";
}

if [ "$EUID" -ne 0 ]
  then echo "Please run the script as root!"
  exit
fi

echo;
echo "JFTF development environment deployment script";
echo;
echo "WARNING!!!";
echo "This script will drop and reset the JFTF database! Continue at your own RISK!!!";
echo "Script only to be used for development/testing purposes, not for production deployment!";
echo;

while true
do
    read -r -p 'Do you want to start the setup process? Y(y)/N(n) ' choice
    case "$choice" in
      n|N) break;;
      y|Y) echo; install_apt_dependencies; echo; my_sql_secure; echo; configure_database; echo; configure_rsyslog_remote_logging; echo; break;;
      *) echo 'Response not valid';;
    esac
done

echo;
echo "JFTF development deployment completed successfully!";
