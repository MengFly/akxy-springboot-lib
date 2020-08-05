### 1. 在 pom 文件中引入代码包
```xml
<dependency>
    <groupId>com.akxy</groupId>
    <artifactId>autocheck</artifactId>
    <version>1.0</version>
</dependency>
```

### 2. Springboot项目中在 Application类中添加注解,并打开定时器功能
```java
@Import(AutoCheckApplication.class)
@EnableScheduling
public class XXXApplication {    
    public static void main(String[] args) {
        XXXApplication.run(XXXApplication.class, args);
    }    
}
```

第二步完成后就已经自动配置完接口 /autoCheck/alive, 和接口/autoCheck/health, 通过get请求就可以访问到这两个接口了 

### 3. 监控方法
如果要监控某个方法，需要在方法上添加 @AutoCheck 注解，其中value表示超时时间，timeUint 表示超时时间的单位  
例如：@AutoCheck(value = 10, timeUnit = TimeUnit.MINUTES) 表示 10 分钟，默认为15分钟

一旦方法超过设定的执行时间以后就会终止项目


### 4. 日志显示
如果需要查看监控的运行情况，可以在 properties 配置文件在配置参数
autoCheck.showlog=true， 默认是不输出日志的