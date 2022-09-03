# Data Center System Deployment Manual


## 1. Installation

### 1.1 Prerequisites:

- Java 1.8 or later
- mysql 5.7 (recommended)
- jar (Optional)
- docker (Optional)

### 1.2 Database

#### 1.2.1 Create the Database

1. Get the mysql script from [github](https://github.com/BSN-Spartan/Data-Center-System/blob/main/src/main/resources/sql/v1.0/bsn_spartan_dc.sql)

2. Create the database

   ```sql
   CREATE DATABASE [db_name] DEFAULT CHARACTER SET utf8 COLLATE = utf8_general_ci;
   ```

3. Execute sql script to initialize the table

   ```sql
   use [db_name];
   
   #Execute sql
   ```

## 2 Configuration

### 2.1 Edit `application.yml`

- Change the default login account

  If the database has never been initialized with any account, the service will be initialized with this information when it is first started

  ```yml
  system:
    adminName: admin
    adminEmail: admin@reddatetech.com
    defaultPassword: 888888
  ```

### 2.2 Edit `application-prod.yml`

- Configure the data source

  ```yml
  mysql:
    write_url: jdbc:mysql://[database-IP]:[port]/[db_name]?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC&allowMultiQueries=true&useSSL=false
    write_username: db_username
    write_password: db_password
    read_url: jdbc:mysql://[database-IP]:[port]/[db_name]?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC&allowMultiQueries=true&useSSL=false
    read_username: db_username
    read_password: db_password
  ```

- Configure the node information

  ```yml
   chain:
    # Node's JSON-RPC interface of the default chain
    nodeRpcAddress: "http://[node-IP]:[node-rpc-port]"
    # Default chain's netWork ID
    chainId: 9090
    # Query the transaction pool's waiting time: millisecond, recommended 1.5 seconds
    txPoolSleep: 1500
    # Path of the Keystore file
    walletFilePath: "your-directory/wallet"
  ```

- Configure the task time

  ```yml
  task:
    # Whether to start a timed task to submit a transaction/time parse
    enabled: false
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
    # Query gasprice
    chainGasPrice: "0/60 * * * * ?"
  ```

- Configure the effective time period of emergency NTT top-up

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

### 3.1 Download the Service Package

#### 3.1.1 Download the Package

```shell
  git clone https://github.com/BSN-Spartan/Data-Center-System/releases/tag/
```

#### 3.1.2 Download the Source Code

```shell
  git clone https://github.com/BSN-Spartan/Data-Center-System/
```

### 3.2 Start the Service

#### 3.2.1 Start by jar

```yml
nohup java -jar spartan_dc-0.0.1-SNAPSHOT.jar --spring.config.location=./application.yml --spring.config.location=./application-prod.yml --logging.config=./logback-spring.xml - LANG=zh_CN.UTF-8 &
```

#### 3.2.2 Start by Docker

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
      - ./spartan_dc-0.0.1-SNAPSHOT.jar:/bsn/spartan-dc/spartan_dc-0.0.1-SNAPSHOT.jar
      - ./conf:/bsn/spartan-dc/conf
      - ./logs/:/bsn/spartan-dc/logs
      - ./wallet:/bsn/spartan-dc/src/main/resources/wallet
      - /etc/localtime:/etc/localtime
      #- /root/skywalking-agent/:/bsn/spartan-dc/skywalking-agent
    environment:
      # Specify the time zone
      - TZ=Asia/Shanghai
    entrypoint: java -jar spartan_dc-0.0.1-SNAPSHOT.jar --spring.config.location=./conf/application.yml --spring.config.location=./conf/application-prod.yml --logging.config=./conf/logback-spring.xml - LANG=zh_CN.UTF-8
```



​    

