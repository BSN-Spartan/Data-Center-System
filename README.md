# Data Center System Deployment Manual

## Introduction

This document is a guide to install, configure and run the Data Center System of the BSN Spartan Network. This local system will help users manage their data centers and manage nodes and NTT. The system only interacts with installed full nodes of Non-Cryptocurrency Public Chains on Spartan Network, and never connects to any external third-party systems.

> As a clear demonstration, all commands in this document are run with root permission. These commands can also be run under normal user permissions, please set the file storage and configure the parameters properly.

## 1. Installation

### 1.1 Hardware Requirements
It is recommended to build the Data Center Management System on Linux Server with the following requirements:

#### Minimum Requirements:

- 2 CPU
- Memory: 4GB
- Disk: 25GB SSD
- Bandwidth: 20Mbps

#### Recommended Requirements:

- 4 CPU
- Memory: 16GB
- Disk: 50GB SSD
- Bandwidth: 20Mbps

### 1.2 Prerequisites

- Java 1.8 or later
- mysql 5.7 or later
- jar (Optional)
- Docker (Optional)

### 1.3 Creating the Database

1. Make sure you have installed MySQL 5.7 or later version in your system. You can go to [MySQL official website](https://dev.mysql.com/doc/mysql-linuxunix-excerpt/5.7/en/linux-installation.html) to learn how to install MySQL on Linux.
    ```
    mysql -V
    ```
    ![](https://github.com/BSN-Spartan/Data-Center-System/blob/main/images/1.mysqlversion.jpg?raw=true)

2. Get the mysql script from [here](https://github.com/BSN-Spartan/Data-Center-System/blob/main/src/main/resources/sql/v1.0/bsn_spartan_dc.sql)

3. Login to MySQL service, name and create the database:

   ```sql
   CREATE DATABASE db_name DEFAULT CHARACTER SET utf8 COLLATE = utf8_general_ci;
   ```
   ![](https://github.com/BSN-Spartan/Data-Center-System/blob/main/images/2.dbname.jpg?raw=true)
4. Execute sql script to initialize the table:
     ```sql
   use db_name;
   ```
   ![](https://github.com/BSN-Spartan/Data-Center-System/blob/main/images/3.usedb.jpg?raw=true)

5. Execute the rest commands in sequence. Then, check the table structure:
    ```
    show tables;
    ```
    The table of the database is like below:

    ![](https://github.com/BSN-Spartan/Data-Center-System/blob/main/images/4.showtables.jpg?raw=true)

## 2. Download and Configuration

### 2.1 Downloading the Package

Download the package of the Data Center System from [here](https://github.com/BSN-Spartan/Data-Center-System/releases/tag/v1.0.0). In the same link, you can also download the source code and compile it by yourself.

### 2.2 Downloading Configuration Files

Download the configuration files, including [application.yml](https://github.com/BSN-Spartan/Data-Center-System/blob/main/src/main/resources/application.yml), [application-prod.yml](https://github.com/BSN-Spartan/Data-Center-System/blob/main/src/main/resources/application-prod.yml) and [logback-spring.xml](https://github.com/BSN-Spartan/Data-Center-System/blob/main/src/main/resources/logback-spring.xml).

### 2.3 Configuration

#### 2.3.1 Editing `application.yml`

- Configure the email

  ```yml
  spring:
    mail:
    # The server address for the email
      host: smtp.example.com
    # The server port
      port: 587
    # The sender address of the email
      username: xxx@example.com
    # The password to login the server 
      password: password
  ```

  

- Change the default login account information

  If the database has never been initialized with any account before, the service will be initialized with this information when started:

  ```yml
  system:
    adminName: xxx
    adminEmail: xxx@xxx.com
    defaultPassword: xxx
  ```
  Example:

  ![](https://github.com/BSN-Spartan/Data-Center-System/blob/main/images/6.%20application.jpg?raw=true)

- Configure Kong gateway

  Configure the username and password of Kong gateway
  
  ```yml
  kongGateWayConfig:
    # Username configured in the operations and maintenance system
    username: username
    # Password configured in the operations and maintenance system
    password: password
    # User access key URL
    userAccessKeyUrl: /gateway/api/v0/accessKey/save
    # User default request configuration
    userDefaultReqConfig: /gateway/api/v0/accessKey/defaultConfig
  ```

#### 2.3.2 Editing `application-prod.yml`

- Configure the data source

  ```yml
  server:
    # Specify the port that runs the Data Center System
    port: 8085
  mysql:
    # Specify the IP address, port number and the name of your database
    write_url: jdbc:mysql://database-IP:port/db_name?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC&allowMultiQueries=true&useSSL=false
    # Enter the login name of the database
    write_username: db_username
    # Enter the password
    write_password: db_password
    # Specify the IP address, port number and the name of your database
    read_url: jdbc:mysql://database-IP:port/db_name?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC&allowMultiQueries=true&useSSL=false
    # Enter the login name of the database
    read_username: db_username
    # Enter the password
    read_password: db_password
  ```
  Example:
  
  ![](https://github.com/BSN-Spartan/Data-Center-System/blob/main/images/7.%20appprod.jpg?raw=true)
  
- Configure the node information

  ```yml
   chain:
    # Default Node's JSON-RPC interface of Spartan-I Chain
    nodeRpcAddress: "http://node-IP:node-rpc-port"
    # Default Chain's network ID, do not change this configuration
    chainId: 9090
    # Query the transaction pool's waiting time: millisecond, recommended 1.5 seconds
    txPoolSleep: 1500
    # Change the path of the Keystore file to a designated directory
    walletFilePath: "your-directory/wallet"
    # This number is recommended to be set as the latest block height of the Default Chain before the Data Center System started. The system can then parse events from all blocks after this block height. For example: blockHeight: 270441
    blockHeight: block_height
  ```
  Example:

  ![](https://github.com/BSN-Spartan/Data-Center-System/blob/main/images/8.%20prod2.jpg?raw=true)

- Configure the email sender name (system name)

  ```yml
  # Send email sender name (system name)
  sys-name: BSN-Spartan
  captchaTimeOut: 5
  ```

- Configure  the payment information

  ```yml
  # stripe configuration information
  stripe:
    currency: USD
    cueDate: 3
  
  # coinbase configuration information
  coinbase:
    server-addr: https://api.commerce.coinbase.com/
    api:
      createCharge: charges
      queryCharge: charges/
  ```

  

## 3. Starting the Service

### 3.1 Starting by Package

Make sure Java 1.8 or later version has been installed in your system. You can go to [Oracle official website](https://docs.oracle.com/en/java/javase/13/install/installation-jdk-linux-platforms.html#GUID-737A84E4-2EFF-4D38-8E60-3E29D1B884B8) to learn how to install Java on Linux.
```
java -version
```

![](https://github.com/BSN-Spartan/Data-Center-System/blob/main/images/5.jpg?raw=true)

Put `Data-Center-System-0.0.1-SNAPSHOT.jar`, `application.yml`,  `application-prod.yml` and `logback-spring.xml` files into the same directory and run the command below:

```yml
java -jar Data-Center-System-0.0.1-SNAPSHOT.jar --spring.config.location=./application.yml --spring.config.location=./application-prod.yml --logging.config=./logback-spring.xml - LANG=zh_CN.UTF-8
```
Result:
![](https://github.com/BSN-Spartan/Data-Center-System/blob/main/images/9.rundatacenter.jpg?raw=true)

After starting up the Data Center System, you may find the "the basic information of data center is not configured" error message in the process. You need to configure the information after logging into the system.

You can also execute in background by `nohup`command:

```yml
nohup java -jar Data-Center-System-0.0.1-SNAPSHOT.jar --spring.config.location=./application.yml --spring.config.location=./application-prod.yml --logging.config=./logback-spring.xml - LANG=zh_CN.UTF-8 >/dev/null 2>&1 &
```

### 3.2 Starting by Docker

The container is `/bsn/spartan-dc` and the service working directory can be configured by the Data Center Operator:

```yml
version: "3"
services:
  spartan-dc:
    image: oracle-jdk11
    container_name: spartan-dc
    working_dir: /bsn/spartan-dc
    restart: always
    #privileged: true
    ports:
      - "8085:8085"
    volumes:
      - ./Data-Center-System-0.0.1-SNAPSHOT.jar:/bsn/spartan-dc/Data-Center-System-0.0.1-SNAPSHOT.jar
      - ./conf:/bsn/spartan-dc/conf
      - ./logs/:/bsn/spartan-dc/logs
      - ./wallet:/bsn/spartan-dc/src/main/resources/wallet
      - /etc/localtime:/etc/localtime
      #- /root/skywalking-agent/:/bsn/spartan-dc/skywalking-agent
    environment:
      # Specify the time zone
      - TZ=Asia/Shanghai
    entrypoint: java -jar Data-Center-System-0.0.1-SNAPSHOT.jar --spring.config.location=./conf/application.yml --spring.config.location=./conf/application-prod.yml --logging.config=./conf/logback-spring.xml - LANG=zh_CN.UTF-8
```
## 4. Data Center Registration

After successfully starting the service, the Data Center Operator can access to the system from `http://localhost:server_port`.

![](https://github.com/BSN-Spartan/Data-Center-System/blob/main/images/10.DChome.jpg?raw=true)

Before using the System, the Data Center Operator should go to the [Spartan Official Website](http://spartan.bsn.foundation/) to register a new data center.

Please refer to the [BSN Spartan Network User Manual](http://spartan.bsn.foundation/static/quick-start/2gettingStarted/2-1-4.html) for detailed instructions.



## 5. Data Center Upgrade Manual

If you have installed the Data Center System with v1.0.0, you can follow below steps to upgrade to V1.1.0.

### 5.1 Configuration 

#### 5.1.1 Editing `application.yml` 

- spring

  Add below configuration in `spring`.

  ```yml
    mvc:
      pathmatch:
        matching-strategy: ant_path_matcher
    mail:
      host: [smtp.exmail.qq.com]
      username: [username]
      password: [password]      
  ```

- system

  Add below configuration in `system`.

  ```yml
  iconBase64: 'data:image/jpg;base64,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'
  ```

#### 5.1.2 Adding `kongGateWayConfig` 

  * This file configures the default password of the gateway, please remember to update this configuration if there's any change.

  ```yml
kongGateWayConfig:
  username: admin
  password: abc123
  userAccessKeyUrl: /gateway/api/v0/accessKey/save
  userDefaultReqConfig: /gateway/api/v0/accessKey/defaultConfig
  ```

#### 5.1.3 Editing `application-prod.yml` 
  Add below configuration in `application-prod.yml` .

- ```
  # Send email sender name (system name)
  sys-name: BSN-Spartan
  captchaTimeOut: 5
  
  # stripe configuration information
  stripe:
    currency: USD
    cueDate: 3
    draftModifiedCount: 3
  
  # coinbase configuration information
  coinbase:
    server-addr: https://api.commerce.coinbase.com/
    api:
      createCharge: charges
      queryCharge: charges/
  ```

### 5.2 Execute SQL Script

 Execute the [SQL script](https://github.com/BSN-Spartan/Data-Center-System/blob/main/src/main/resources/sql/v1.1/Upgrade(v1.0%20to%20v1.1).sql) in the database.


### 5.3 Restart the System with New Jar

  Restart the system with the  [new jar](https://github.com/BSN-Spartan/Data-Center-System/releases/tag/v1.1.0).

