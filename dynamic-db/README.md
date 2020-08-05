## dynamic-db

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