## dynamic-db

### pom.xml
```xml
<groupId>com.akxy</groupId>
   <artifactId>dynamic-db</artifactId>
<version>1.1</version>
```

为将dynamic-db中的配置生效，需要将包名配置到Applicaiton类中。以及import多数据源的注册类

```java
@ComponentScan(basePackages = {"本项目的包名","com.akxy.dynamicdb"})
@Import({DynamicDataSourceRegister.class})
```

+ 配置sql警告是否开启

  ```properties
  dynamic.db.sqlCostWarn.enable=[false/true]
  ```

+ 配置sql警告的数量

  ```properties
  dynamic.db.sqlCostWarn.countLimit=5000
  ```

+ 配置sql警告的耗时（秒）

  ```properties
  dynamic.db.sqlCostWarn.secondLimit=5
  ```

+ 配置entity所在包位置

  ```properties
  dynamic.db.entity.package=com.akxy.plat.entity
  ```

### 配置数据源

  ```properties
# 配置主数据库源
spring.datasource.username=username
spring.datasource.password=password
spring.datasource.names=ds0 
spring.datasource.driver-class-name=driver
spring.datasource.url=url
spring.datasource.type=数据库连接池

# 配置其他数据库源
custom.datasource.names=ds1,ds2,...
# 其他数据库源以custom.datasource.[name]开头，其中name为custom.datasource.names中配置的数据源名称，以逗号分开
# 示例
custom.datasource.ds1.driver-class-name=
custom.datasource.ds1.url=
custom.datasource.ds1.username=
custom.datasource.ds1.password=
  ```

### 配置数据库连接池属性

> 数据库连接池属性以 datasource.global.开头，每个属性请自行查找对应的连接池Datasource的对应属性，属性名称需要和Datasource属性对应

  示例:

  ```properties
datasource.global.idleConnectionValidationSeconds=100
datasource.global.maxConnection=100
datasource.global.testWhileIdle=true
datasource.global.testOnBorrow=true
datasource.global.timeBetweenEvictionRunsMillis=600000
datasource.global.minEvictableIdleTimeMillis=300000
datasource.global.idleTimeout=1000
datasource.global.loginTimeout=60000
datasource.global.removeAbandoned=true
datasource.global.maxIdleTime=86000
datasource.global.waitTimeout=3153600000
datasource.global.interactiveTimeout=3153600000
datasource.global.maxConnections=1000
datasource.global.autoReconnect=true
datasource.global.autoReconnectForPools=true
datasource.global.validationQuery=select 1 from dual
#初始化连接
datasource.global.initialSize=10
#最大空闲连接
datasource.global.maxIdle=20
#最小空闲连接
datasource.global.minIdle=5
#最大连接数量
datasource.global.maxActive=50
#是否在自动回收超时连接的时候打印连接的超时错误
datasource.global.logAbandoned=true
#超时时间(以秒数为单位)
datasource.global.removeAbandonedTimeout=180
##<!-- 超时等待时间以毫秒为单位 6000毫秒/1000等于60秒 -->
datasource.global.maxWait=1000
  ```

### Change logs
V1.1
+ 添加多数据库数据库标识对大写字符的支持