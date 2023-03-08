package mydataharbor.plugin.jdbc.sink;

import lombok.extern.slf4j.Slf4j;
import mydataharbor.IDataSink;
import mydataharbor.common.jdbc.exception.JdbcDataSourceCreateException;
import mydataharbor.common.jdbc.sink.JdbcSinkReq;
import mydataharbor.common.jdbc.sink.config.JdbcSinkConfig;
import mydataharbor.exception.ResetException;
import mydataharbor.setting.BaseSettingContext;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * jdbc 写入源
 * Created by xulang on 2021/8/18.
 */
@Slf4j
public abstract class JdbcSink implements IDataSink<JdbcSinkReq, BaseSettingContext> {

    private BasicDataSource dataSource;

    protected JdbcTemplate jdbcTemplate;

    private JdbcSinkConfig jdbcSinkConfig;

    private DataSourceTransactionManager dataSourceTransactionManager = null;

    static {
        //让 Oracle遵循jdbc规范
        System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
    }

    public JdbcSink(JdbcSinkConfig jdbcSinkConfig) {
        this.jdbcSinkConfig = jdbcSinkConfig;
        Properties connectionProps = new Properties();
        connectionProps.put("username", jdbcSinkConfig.getUsername());
        connectionProps.put("password", jdbcSinkConfig.getPassword());
        connectionProps.put("driverClassName", driverClassName());
        connectionProps.put("url", jdbcSinkConfig.getUrl());
        connectionProps.put("initialSize", jdbcSinkConfig.getInitialSize());
        try {
            dataSource = BasicDataSourceFactory
                    .createDataSource(connectionProps);
            if (enableTransaction())
                dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
            dataSource.start();
            jdbcTemplate = new JdbcTemplate(dataSource);
        }
        catch (Exception e) {
            throw new JdbcDataSourceCreateException("创建jdbc输出源失败！:" + e.getMessage(), e);
        }
    }

    /**
     * 数据库驱动名称
     *
     * @return
     */
    public abstract String driverClassName();

    /**
     * 是否支持事务
     *
     * @return
     */
    public boolean enableTransaction() {
        return true;
    }

    @Override
    public void close() throws IOException {
        if (dataSource != null) {
            try {
                dataSource.close();
            }
            catch (SQLException throwables) {
                log.error("关闭输出源失败！", throwables);
            }
        }
    }

    @Override
    public String name() {
        return "jdbc";
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    @Override
    public WriterResult write(JdbcSinkReq record, BaseSettingContext settingContext) throws ResetException {
        TransactionStatus transactionStatus = null;
        if (dataSourceTransactionManager != null) {
            TransactionDefinition definition = new DefaultTransactionDefinition();
            transactionStatus = dataSourceTransactionManager.getTransaction(definition);
        }
        try {
            Map<String, List<Object[]>> stringListMap = process(record);
            for (String sql : stringListMap.keySet()) {
                jdbcTemplate.batchUpdate(sql, stringListMap.get(sql));
            }
            if (transactionStatus != null) {
                dataSourceTransactionManager.commit(transactionStatus);
            }
        }
        catch (Exception e) {
            log.error("写入数据时发生异常", e);
            if (jdbcSinkConfig.getOnlyOnIOExceptionRollback() == false || e instanceof IOException) {
                if (transactionStatus != null)
                    dataSourceTransactionManager.rollback(transactionStatus);
                throw new ResetException("写入数据时发生异常：" + e.getMessage());
            }
            else {
                return WriterResult.builder().success(false).commit(true).msg(e.getMessage()).build();
            }
        }
        return WriterResult.builder().success(true).commit(true).msg("ok").build();
    }

    private Map<String, List<Object[]>> process(JdbcSinkReq record) {
        List<Integer> result = new ArrayList<>();
        Map<String, List<Object[]>> res = new HashMap<>();
        try {
            for (JdbcSinkReq.WriteDataInfo writeDataInfo : record.getWriteDataInfos()) {
                Map<String, Object> data = writeDataInfo.getData();
                Set<Map.Entry<String, Object>> dataEntrySet = data.entrySet();
                String insertColumnNames = dataEntrySet.stream().map(o -> o.getKey()).collect(Collectors.joining(", ", "(", ")"));
                String updateColumnNames = dataEntrySet.stream().map(o -> o.getKey() + " = ?").collect(Collectors.joining(", "));
                String valuePlaceholder = dataEntrySet.stream().map(o -> "?").collect(Collectors.joining(", ", "(", ")"));
                Object[] values = dataEntrySet.stream().map(o -> o.getValue()).toArray();
                StringBuilder whereSql = new StringBuilder();
                Object[] whereValues = new Object[0];
                if (writeDataInfo.getWhere() != null) {
                    whereSql = new StringBuilder();
                    whereSql.append(" WHERE ");
                    whereSql.append(writeDataInfo.getWhere().keySet().stream().map(o -> o + " = ?").collect(Collectors.joining("and ")));
                    whereValues = writeDataInfo.getWhere().values().stream().toArray();
                }
                StringBuilder sql = new StringBuilder();
                Object[] executeValues = null;
                switch (writeDataInfo.getWriteModel()) {
                    case INSERT:
                        sql.append("INSERT INTO ");
                        sql.append(writeDataInfo.getTableName());
                        sql.append(" ");
                        sql.append(insertColumnNames);
                        sql.append(" VALUES ");
                        sql.append(valuePlaceholder);
                        executeValues = values;
                        break;
                    case UPDATE:
                        sql.append("UPDATE ");
                        sql.append(writeDataInfo.getTableName());
                        sql.append(" SET ");
                        sql.append(updateColumnNames);
                        if (whereValues.length > 0) {
                            sql.append(whereSql.toString());
                        }
                        executeValues = concat(values, whereValues);
                        break;
                    case DELETE:
                        sql.append("DELETE FROM ");
                        sql.append(writeDataInfo.getTableName());
                        if (whereValues.length > 0) {
                            sql.append(whereSql.toString());
                        }
                        executeValues = whereValues;
                        break;
                    case SAVE:
                        executeValues = generateSave(writeDataInfo, insertColumnNames, updateColumnNames, valuePlaceholder, values, whereSql, whereValues, sql);
                        break;
                }
                if (executeValues != null) {
                    List<Object[]> list = res.get(sql.toString());
                    if (list == null) {
                        list = new ArrayList<>();
                        res.put(sql.toString(), list);
                    }
                    list.add(executeValues);
                }
            }
        }
        catch (Exception e) {
            log.error("写入数据时发生异常", e);
            throw e;
        }
        return res;
    }

    /**
     * save情况下如何执行sql
     *
     * @param writeDataInfo
     * @param insertColumnNames
     * @param updateColumnNames
     * @param valuePlaceholder
     * @param values
     * @param whereSql
     * @param whereValues
     * @param sql
     * @return
     */
    public Object[] generateSave(JdbcSinkReq.WriteDataInfo writeDataInfo, String insertColumnNames, String updateColumnNames, String valuePlaceholder, Object[] values, StringBuilder whereSql, Object[] whereValues, StringBuilder sql) {
        sql.append("UPDATE ");
        sql.append(writeDataInfo.getTableName());
        sql.append(" SET ");
        sql.append(updateColumnNames);
        if (whereValues.length > 0) {
            sql.append(whereSql.toString());
        }
        int upset = jdbcTemplate.update(sql.toString(), concat(values, whereValues));
        if (upset == 0) {
            sql.setLength(0);
            sql.append("INSERT INTO ");
            sql.append(writeDataInfo.getTableName());
            sql.append(" ");
            sql.append(insertColumnNames);
            sql.append(" VALUES ");
            sql.append(valuePlaceholder);
            return values;
        }
        return null;
    }

    @Override
    public WriterResult write(List<JdbcSinkReq> records, BaseSettingContext settingContext) throws ResetException {
        List<List<Integer>> result = new ArrayList<>();
        Map<String, List<Object[]>> toWrite = new HashMap<>();
        for (JdbcSinkReq record : records) {
            Map<String, List<Object[]>> stringListMap = process(record);
            for (Map.Entry<String, List<Object[]>> stringListEntry : stringListMap.entrySet()) {
                List<Object[]> list = toWrite.get(stringListEntry.getKey());
                if (list == null) {
                    toWrite.put(stringListEntry.getKey(), stringListEntry.getValue());
                }
                else {
                    list.addAll(stringListEntry.getValue());
                }
            }
        }
        TransactionStatus transactionStatus = null;
        if (dataSourceTransactionManager != null) {
            TransactionDefinition definition = new DefaultTransactionDefinition();
            transactionStatus = dataSourceTransactionManager.getTransaction(definition);
        }
        try {
            for (String sql : toWrite.keySet()) {
                jdbcTemplate.batchUpdate(sql, toWrite.get(sql));
            }
            if (transactionStatus != null)
                dataSourceTransactionManager.commit(transactionStatus);
        }
        catch (Exception e) {
            log.error("写入数据时发生异常", e);
            if (jdbcSinkConfig.getOnlyOnIOExceptionRollback() == false || e instanceof IOException) {
                if (transactionStatus != null)
                    dataSourceTransactionManager.rollback(transactionStatus);
                throw new ResetException("写入数据时发生异常：" + e.getMessage());
            }
            else {
                return WriterResult.builder().success(false).commit(true).msg(e.getMessage()).build();
            }
        }
        return WriterResult.builder().success(true).commit(true).msg("ok").writeReturn(result).build();
    }
}


