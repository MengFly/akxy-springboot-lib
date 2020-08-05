package com.akxy.dynamicdb.cfg;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Sql执行时间记录拦截器
 *
 * @author wangp
 */
@Intercepts(@Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
@Slf4j
public class SqlCheckInterceptor implements Interceptor {

    private final int dynamicCountLimit;
    private final int dynamicSecondLimit;

    public SqlCheckInterceptor(int dynamicCountLimit, int dynamicSecondLimit) {
        this.dynamicCountLimit = dynamicCountLimit;
        this.dynamicSecondLimit = dynamicSecondLimit;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        long startTime = System.currentTimeMillis();
        String sql = null;
        try {
            Object[] args = invocation.getArgs();
            if (args != null && args.length > 0 && args[0] instanceof MappedStatement) {
                sql = ((MappedStatement) args[0]).getId();
            }
            Object proceed = invocation.proceed();
            if (proceed instanceof List) {
                if (((List<?>) proceed).size() > dynamicCountLimit) {
                    log.warn(String.format(">>>>>>>>> [SQL WARN:数据量过多 (%s)条] -> [%s] ", sql, ((List<?>) proceed).size()));
                }
            }
            return proceed;
        } finally {
            long sqlCost = System.currentTimeMillis() - startTime;
            if (sqlCost >  TimeUnit.SECONDS.toMillis(dynamicSecondLimit)) {
                log.warn(String.format(">>>>>>>>> [SQL WARN:执行超过时限(%s)ms] -> [%s]", sql, sqlCost));
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(java.util.Properties properties) {

    }

}
