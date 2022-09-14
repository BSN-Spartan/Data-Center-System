# Data Center System Deployment Manual

> This document is a guide to install, configure and run the Data Center System of the BSN Spartan Network. This local system will help users manage their data centers and manage nodes and NTT. The system only interacts with installed full nodes of Non-Cryptocurrency Public Chains on Spartan Network, and never connects to any external third-party systems.


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

### 1.3 Create the Database

1. Get the mysql script from [here](https://github.com/BSN-Spartan/Data-Center-System/blob/main/src/main/resources/sql/v1.0/bsn_spartan_dc.sql)

2. Name and create the database

   ```sql
   CREATE DATABASE db_name DEFAULT CHARACTER SET utf8 COLLATE = utf8_general_ci;
   ```

3. Execute sql script to initialize the table
     ```sql
   use db_name;
   ```
   Then execute the rest commands in sequence.

## 2. Download and Configuration

### 2.1 Download Package and Source Code

Download the package and source code of the Data Center System from [here](https://github.com/BSN-Spartan/Data-Center-System/releases/tag/v1.0.0)

### 2.2 Download Configuration Files

Download the configuration files from [here](https://github.com/BSN-Spartan/Data-Center-System/tree/main/src/main/resources)

### 2.3 Configuration

#### 2.3.1 Edit `application.yml`

- Change the default login account information

  If the database has never been initialized with any account before, the service will be initialized with this information when started

  ```yml
  system:
    adminName: xxx
    adminEmail: xxx@xxx.com
    defaultPassword: xxx
  ```

#### 2.3.2 Edit `application-prod.yml`

- Configure the data source

  ```yml
  server:
    # Specify the port that runs the Data Center Management System
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

- Configure the node information

  ```yml
   chain:
    # Default Chain Node's JSON-RPC interface
    nodeRpcAddress: "http://node-IP:node-rpc-port"
    # Default Chain's netWork ID, do not change this configuration
    chainId: 9090
    # Query the transaction pool's waiting time: millisecond, recommended 1.5 seconds
    txPoolSleep: 1500
    # Change the path of the Keystore file to a designated directory
    walletFilePath: "your-directory/wallet"
    # This number is recommended to be set as the latest block height of the Default Chain before the Data Center System started. The system can then parse events from all blocks after this block height.
    blockHeight: 10000
  ```

- Configure the task time, in the Cron time string format

  ```yml
  task:
    # Whether to start a timed task to submit a transaction/time parse
    enabled: true
    # Parse event
    eventAnalytics: "0/2 * * * * ?"
    # Submit Gas Credit top-up transaction
    gasRechargeSubmit: "0/10 * * * * ?"
    # Query Gas Credit top-up result
    gasRechargeResult: "0/10 * * * * ?"
    # Submit node registration application
    nodeUpChain: "0/10 * * * * ?"
    # Query gas price
    chainGasPrice: "0/60 * * * * ?"
  ```

- Configure the effective time period of emergency Gas Credit top-up

  ```yml
  metaTx:
    # Default Chain ID, do not change this configuration
    chainId: 1
    # Effective time period, in hour
    deadline: 12
    # Do not change this configuration
    domainSeparator: "0x9d5e39915081369daa50abc8dc23f93c9c174abc4fc01efd13e3660e5d080276"
    # Do not change this configuration
    metaTransferTypeHash: "0xbf13ac0a2964a57037372fadd1f7b4fa6785ffdc315c85dfe8d1f9b1b01a7a51"
  ```

## 3. Start the Service

### 3.1 Start by Package

Put `Data-Center-System-0.0.1-SNAPSHOT.jar`, `application.yml`,  `application-prod.yml` and `logback-spring.xml` into the same directory and run the command below:

```yml
java -jar Data-Center-System-0.0.1-SNAPSHOT.jar --spring.config.location=./application.yml --spring.config.location=./application-prod.yml --logging.config=./logback-spring.xml - LANG=zh_CN.UTF-8
```

You can also execute in the background via `nohup`:

```yml
nohup java -jar Data-Center-System-0.0.1-SNAPSHOT.jar --spring.config.location=./application.yml --spring.config.location=./application-prod.yml --logging.config=./logback-spring.xml - LANG=zh_CN.UTF-8 >/dev/null 2>&1 &
```

### 3.2 Start by Docker

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

Before using the System, the Data Center Operator go to the [Spartan Official Website](http://spartan.bsn.foundation/) to register a new data center.

Please refer to the [BSN Spartan Network User Manual](http://spartan.bsn.foundation/static/quick-start/2gettingStarted/2-1-4.html) for detailed instructions.