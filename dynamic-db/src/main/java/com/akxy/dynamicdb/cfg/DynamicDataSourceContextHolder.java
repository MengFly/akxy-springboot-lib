package com.akxy.dynamicdb.cfg;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangp
 */
@Slf4j
public class DynamicDataSourceContextHolder {
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
    private static final List<String> DATA_SOURCE_NAMES = new ArrayList<>(6);

    private DynamicDataSourceContextHolder() {
    }

    public static void setDataSource(String dataSource) {
        CONTEXT_HOLDER.set(dataSource);
    }

    public static String getDataSource() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 恢复数据源
     */
    public static void restoreDataSource() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 查看数据源是否存在
     */
    public static boolean containsDataSource(String dataSourceName) {
        return DATA_SOURCE_NAMES.contains(dataSourceName);
    }

    public static void addDataSource(String dataSourceName) {
        DATA_SOURCE_NAMES.add(dataSourceName);
    }
}
