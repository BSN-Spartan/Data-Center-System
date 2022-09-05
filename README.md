# Data Center System Deployment Manual


## 1. Installation

### 1.1 Prerequisites:

- Java 1.8 or later
- mysql 5.7 or later
- jar (Optional)
- Docker (Optional)

### 1.2 Database

#### 1.2.1 Create the Database

1. Get the mysql script from [here](https://github.com/BSN-Spartan/Data-Center-System/blob/main/src/main/resources/sql/v1.0/bsn_spartan_dc.sql)

2. Create the database

   ```sql
   CREATE DATABASE [db_name] DEFAULT CHARACTER SET utf8 COLLATE = utf8_general_ci;
   ```

3. Execute sql script to initialize the table
     ```sql
   use [db_name];
   ```
   Then execute the rest commands in sequence.

## 2 Download and Configuration

### 2.1 Download Package and Source Code

Download the package and source code of the Data Center System from [here](https://github.com/BSN-Spartan/Data-Center-System/releases/tag/v1.0.0)

### 2.2 Download YAML Files

Download the YAML files from [here](https://github.com/BSN-Spartan/Data-Center-System/tree/main/src/main/resources)

### 2.3 Edit Files

#### 2.3.1 Edit `application.yml`

- Change the default login account information

  If the database has never been initialized with any account, the service will be initialized with this information when started

  ```yml
  system:
    adminName: [xxx]
    adminEmail: [xxx@xxx.com]
    defaultPassword: [xxx]
  ```

#### 2.3.2 Edit `application-prod.yml`

- Configure the data source

  ```yml
  mysql:
    # Specity the IP address, port number and the name of your database
    write_url: jdbc:mysql://[database-IP]:[port]/[db_name]?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC&allowMultiQueries=true&useSSL=false
    # Enter the username
    write_username: [db_username]
    # Enter the password
    write_password: [db_password]
    # Specity the IP address, port number and the name of your database
    read_url: jdbc:mysql://[database-IP]:[port]/[db_name]?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC&allowMultiQueries=true&useSSL=false
    # Enter the username
    read_username: [db_username]
    # Enter the password
    read_password: [db_password]
  ```

- Configure the node information

  ```yml
   chain:
    # Node's JSON-RPC interface of the default chain
    nodeRpcAddress: "http://[node-IP]:[node-rpc-port]"
    # Default chain's netWork ID, do not change this parameter
    chainId: 9090
    # Query the transaction pool's waiting time: millisecond, recommended 1.5 seconds
    txPoolSleep: 1500
    # Path of the Keystore file
    walletFilePath: "[your-directory]/wallet"
    # After the system started, the event parsing can be started from this block height. This number can be determined based on the latest block height before the system started.
    blockHeight: [10000]
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
    # Query transaction status
    checkTxStates: "0/10 * * * * ?"
    # Query gas price
    chainGasPrice: "0/60 * * * * ?"
  ```

- Configure the effective time period of emergency Gas Credit top-up

  ```yml
  metaTx:
    # Only used by the default chain, do not change this configuration
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

Put `Data-Center-System-0.0.1-SNAPSHOT.jar` package, `application.yml` and `application-prod.yml` files into the same directory and run the command below:

```yml
nohup java -jar Data-Center-System-0.0.1-SNAPSHOT.jar --spring.config.location=./application.yml --spring.config.location=./application-prod.yml --logging.config=./logback-spring.xml - LANG=zh_CN.UTF-8 >/dev/null 2>&1 &
```

### 3.2 Start by Docker

The container is `/bsn/spartan-dc` and the service working directory can be configured by the data center owner

```yml
version: "3"
services:
  nodeManager:
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



​    

