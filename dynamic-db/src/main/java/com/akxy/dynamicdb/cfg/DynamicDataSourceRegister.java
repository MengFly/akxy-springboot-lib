package com.akxy.dynamicdb.cfg;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源注册
 * 启动动态数据源请在启动类中（如SpringBootSampleApplication）
 * 添加 @Import(DynamicDataSourceRegister.class)
 *
 * @author wangp
 */
@SuppressWarnings({"rawtypes", "unchecked", "unused"})
@Configuration
@Component
public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    /**
     * 默认的数据源类型
     */
    private static final String DEFAULT_DATASOURCE_TYPE = "org.apache.tomcat.jdbc.pool.DataSource";
    /**
     * 默认数据源
     */
    private DataSource defaultDataSource;

    @Value("${spring.datasource.names}")
    public String mainDb;

    private Binder binder;

    /**
     * 数据源公共属性
     */
    private PropertyValues dataSourcePropertyValues;
    /**
     * 其他数据源
     */
    private final Map<String, DataSource> customDataSources = new HashMap<>(16);


    /**
     * 加载多数据源配置
     */
    @Override
    public void setEnvironment(Environment env) {
        binder = Binder.get(env);
        initDefaultDataSource();
        initCustomDataSources();
    }

    /**
     * 初始化默认数据源
     */
    private void initDefaultDataSource() {
        Map<String, Object> dataSourceMap = binder.bind("spring.datasource", Map.class).get();
        defaultDataSource = buildDataSource(dataSourceMap);
        bindData(defaultDataSource);
    }

    /**
     * 创建DataSource
     */
    private DataSource buildDataSource(Map<String, Object> dataSourceMap) {
        Object type = dataSourceMap.get("type");
        type = type == null ? DEFAULT_DATASOURCE_TYPE : type;
        try {
            //noinspection unchecked
            Class<? extends DataSource> dataSourceType = (Class<? extends DataSource>) Class.forName(type.toString());
            String driverClassName = dataSourceMap.get("driver-class-name").toString();
            String url = dataSourceMap.get("url").toString();
            String username = dataSourceMap.get("username").toString();
            String password = dataSourceMap.get("password").toString();
            return DataSourceBuilder.create().type(dataSourceType).driverClassName(driverClassName).url(url)
                    .username(username).password(password).build();
        } catch (ClassNotFoundException e) {
//            log.error("创建数据源失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 为DataSource绑定更多属性
     */
    private void bindData(DataSource dataSource) {
        DataBinder dataBinder = new DataBinder(dataSource);
        if (dataSourcePropertyValues == null) {
            Map map = binder.bind("datasource.global", Map.class).get();
            // 排除已经设置的属性
            map.remove("type");
            map.remove("driver-class-name");
            map.remove("url");
            map.remove("username");
            map.remove("password");
            dataSourcePropertyValues = new MutablePropertyValues(map);
        }
        dataBinder.bind(dataSourcePropertyValues);
    }

    /**
     * 初始化其他数据源
     */
    private void initCustomDataSources() {
        String dataSources = binder.bind("custom.datasource.names", String.class).get();
        if (StringUtils.isEmpty(dataSources)) {
            return;
        }
        String dataSourceSplitRegex = ",";
        for (String dsName : dataSources.split(dataSourceSplitRegex)) {
            String customName = "custom.datasource." + dsName.toLowerCase();
            Map<String, Object> properties =
                    binder.bind(customName, Map.class).get();
            DataSource dataSource = buildDataSource(properties);
            customDataSources.put(dsName, dataSource);
            bindData(dataSource);
        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Map<String, DataSource> targetDataSources = new HashMap<>(16);
        // 添加默认数据源
        targetDataSources.put(mainDb, defaultDataSource);
        DynamicDataSourceContextHolder.addDataSource(mainDb);
        // 添加其他数据源
        targetDataSources.putAll(customDataSources);
        customDataSources.keySet().forEach(DynamicDataSourceContextHolder::addDataSource);

        // 创建DynamicDataSource
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);
        beanDefinition.setSynthetic(true);
        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        propertyValues.addPropertyValue("defaultTargetDataSource", defaultDataSource);
        propertyValues.addPropertyValue("targetDataSources", targetDataSources);

        registry.registerBeanDefinition("dataSource", beanDefinition);
    }

    /**
     * 配置事务管理器
     */
    @Bean
    public DataSourceTransactionManager transactionManager(DynamicDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
