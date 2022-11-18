# Data Center System Deployment Manual

If you have installed the Data Center System with v1.0.0, you can follow below steps to upgrade to V1.1.0.

### 1.1 Configuration 

#### 1.1.1 Editing `application.yml` 

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

#### 1.1.2 Adding `kongGateWayConfig` 

  * This file configures the default password of the gateway, please remember to update this configuration if there's any change.

  ```yml
kongGateWayConfig:
  username: admin
  password: abc123
  userAccessKeyUrl: /gateway/api/v0/accessKey/save
  userDefaultReqConfig: /gateway/api/v0/accessKey/defaultConfig
  ```

#### 1.1.3 Editing `application-prod.yml` 
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

### 1.2 Execute SQL Script

 Execute the [SQL script](https://github.com/BSN-Spartan/Data-Center-System/blob/main/src/main/resources/sql/v1.1/Upgrade(v1.0%20to%20v1.1).sql) in the database.


### 1.3 Restart the System with New Jar

  Restart the system with the [new jar](https://github.com/BSN-Spartan/Data-Center-System/releases/tag/v1.1.0).







