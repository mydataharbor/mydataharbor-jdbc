package mydataharbor.source.jdbc;

import static mydataharbor.common.jdbc.source.config.JdbcDataSourceConfig.MILLI_SECOND;
import static mydataharbor.common.jdbc.source.config.JdbcDataSourceConfig.SECOND;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mydataharbor.common.jdbc.exception.JdbcDataSourceCreateException;
import mydataharbor.common.jdbc.source.JdbcResult;
import mydataharbor.common.jdbc.source.config.JdbcDataSourceConfig;
import mydataharbor.common.jdbc.source.config.JdbcSyncModel;
import mydataharbor.datasource.AbstractRateLimitDataSource;
import mydataharbor.exception.TheEndException;
import mydataharbor.setting.BaseSettingContext;
import mydataharbor.threadlocal.TaskStorageThreadLocal;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * jdbc 数据源
 * 全量中断必须从头开始，因为全量考虑性能没有order by
 * 增量模式下，宕机重启，可以从断开处继续拉数据
 * Created by xulang on 2021/8/18.
 */
@Slf4j
public abstract class JdbcDataSource extends AbstractRateLimitDataSource<JdbcResult, BaseSettingContext> {

    private BasicDataSource dataSource;

    private Connection connection;

    private JdbcTemplate jdbcTemplate;

    private JdbcDataSourceConfig jdbcDataSourceConfig;

    private ResultSet preResultSet;

    /**
     * 全量拉取是否已经ok
     */
    private boolean completePollOk = false;

    /**
     * 当前结果集是否到头
     */
    private boolean nowRowSetEmpty;

    private List<JdbcResult> tmp = new CopyOnWriteArrayList<>();

    /**
     * 上一次扫描时间
     */
    private Object lastTime;

    private Object rollbackUnit = new Object();

    private SimpleDateFormat dateFormat;

    private CountDownLatch countDownLatch;

    static {
        //让 Oracle遵循jdbc规范
        System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
    }

    public JdbcDataSource(JdbcDataSourceConfig jdbcDataSourceConfig) {
        super(jdbcDataSourceConfig);
        this.jdbcDataSourceConfig = jdbcDataSourceConfig;
        Properties connectionProps = new Properties();
        connectionProps.put("username", jdbcDataSourceConfig.getUsername());
        connectionProps.put("password", jdbcDataSourceConfig.getPassword());
        connectionProps.put("driverClassName", driverClassName());
        connectionProps.put("url", jdbcDataSourceConfig.getUrl());
        connectionProps.put("initialSize", jdbcDataSourceConfig.getInitialSize());
        try {
            dataSource = BasicDataSourceFactory.createDataSource(connectionProps);
            dataSource.start();
            connection = dataSource.getConnection();
        }
        catch (Exception e) {
            throw new JdbcDataSourceCreateException("创建jdbc数据源失败！:" + e.getMessage(), e);
        }
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        if (jdbcDataSourceConfig.getModel() == JdbcSyncModel.INCREMENT || jdbcDataSourceConfig.getModel() == JdbcSyncModel.INCREMENT_AFTER_COMPLETE) {
            if (!MILLI_SECOND.equals(jdbcDataSourceConfig.getTimeFormat()) && !SECOND.equals(jdbcDataSourceConfig.getTimeFormat()))
                this.dateFormat = new SimpleDateFormat(jdbcDataSourceConfig.getTimeFormat());
        }
    }

    @SneakyThrows
    @Override
    public void init(BaseSettingContext settingContext) {
        this.lastTime = getInitLastTime();
        this.completePollOk = getCompletePollOk();
    }

    /**
     * 获得增量sql
     */
    private String getIncreaseSql() {
        /**
         * 增量sql
         */
        String increaseSql = "";
        switch (jdbcDataSourceConfig.getModel()) {
            case INCREMENT:
            case INCREMENT_AFTER_COMPLETE:
                increaseSql = jdbcDataSourceConfig.getQuerySql() + " ";
                StringBuilder sb = new StringBuilder(increaseSql);
                int index = increaseSql.toLowerCase().lastIndexOf(whereFlag);
                if (index >= 0) {
                    increaseSql = sb.insert(index + whereFlag.length(), jdbcDataSourceConfig.getRollingFieldName() + " > ? and " + jdbcDataSourceConfig.getRollingFieldName() + " < ?" + " and ").toString();
                }
                else {
                    increaseSql = sb.append(" where " + jdbcDataSourceConfig.getRollingFieldName() + " > ? and " + jdbcDataSourceConfig.getRollingFieldName() + " < ? ").toString();
                }
                break;
        }
        //增加 order by
        increaseSql += " order by " + jdbcDataSourceConfig.getRollingFieldName() + " asc";
        return increaseSql;
    }

    /**
     * 数据库驱动名称
     *
     * @return
     */
    public abstract String driverClassName();

    private String whereFlag = "where ";

    public Object getNowTime() {
        if (dateFormat != null) {
            return new Date();
        }
        else if (SECOND.equals(jdbcDataSourceConfig.getTimeFormat())) {
            return System.currentTimeMillis() / 1000;
        }
        else {
            return System.currentTimeMillis();
        }
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException e) {
            log.warn("线程睡眠被打断！", e);
        }
    }

    @Override
    public Collection<JdbcResult> doPoll(BaseSettingContext settingContext) throws TheEndException {
        if (!tmp.isEmpty()) {
            return tmp;
        }
        ResultSet resultSet = null;
        switch (jdbcDataSourceConfig.getModel()) {
            case COMPLETE:
                if (completePollOk) {
                    throw new TheEndException("迁移结束");
                }
                if (preResultSet == null) {
                    preResultSet = queryForResultSet(jdbcDataSourceConfig.getQuerySql());
                }
                resultSet = preResultSet;
                break;
            case INCREMENT:
                if (preResultSet == null || nowRowSetEmpty) {
                    sleep(jdbcDataSourceConfig.getSleepTimeOnIncrement());//防止一直空转，查询数据库
                    preResultSet = queryForResultSet(getIncreaseSql(), lastTime, getNowTime());
                }
                resultSet = preResultSet;
                break;
            case INCREMENT_AFTER_COMPLETE:
                if (!completePollOk) {
                    if (preResultSet == null) {
                        preResultSet = queryForResultSet(jdbcDataSourceConfig.getQuerySql());
                    }
                    resultSet = preResultSet;
                }
                else {
                    //增量
                    if (preResultSet == null || nowRowSetEmpty) {
                        sleep(jdbcDataSourceConfig.getSleepTimeOnIncrement());//防止一直空转，查询数据库
                        preResultSet = queryForResultSet(getIncreaseSql(), lastTime, getNowTime());
                    }
                    resultSet = preResultSet;
                }
                break;
        }
        try {
            tmp = getResults(resultSet);
        }
        catch (SQLException throwables) {
            preResultSet = null;//下一次重新生成
            throw new RuntimeException("抽取数据发生异常：" + throwables.getMessage(), throwables);
        }
        return tmp;
    }

    private ResultSet queryForResultSet(String querySql, Object... args) {
        this.countDownLatch = new CountDownLatch(1);
        QueryForResultSet queryForResultSet = new QueryForResultSet(querySql, args);
        queryForResultSet.start();
        return queryForResultSet.getResultSet();
    }

    public Object getInitLastTime() throws ParseException {
        Serializable lastTime = TaskStorageThreadLocal.get().getFromCache("lastTime");
        if (lastTime != null)
            return lastTime;
        if (dateFormat != null) {
            return dateFormat.parse(jdbcDataSourceConfig.getStartTime().toString());
        }
        return jdbcDataSourceConfig.getStartTime();
    }

    public void setLastTime(Object lastTime) {
        this.lastTime = lastTime;
        TaskStorageThreadLocal.get().setToCache("lastTime", System.currentTimeMillis(), (Serializable) lastTime);
    }

    public void setCompletePollOk(boolean completePollOk) {
        TaskStorageThreadLocal.get().setToCache("completePollOk", System.currentTimeMillis(), completePollOk);
        this.completePollOk = completePollOk;
    }

    public boolean getCompletePollOk() {
        Serializable completePollOk = TaskStorageThreadLocal.get().getFromCache("completePollOk");
        if (completePollOk != null)
            return (boolean) completePollOk;
        return false;
    }

    private List<JdbcResult> getResults(ResultSet resultSet) throws SQLException {
        List<JdbcResult> result = new CopyOnWriteArrayList<>();
        int count = 0;
        ResultSetMetaData metaData = resultSet.getMetaData();
        String[] columnNames = new String[metaData.getColumnCount()];
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columnNames[i - 1] = metaData.getColumnLabel(i);
        }
        while (count < jdbcDataSourceConfig.getMaxPollRecords()) {
            if (resultSet.next()) {
                JdbcResult row = new JdbcResult();
                if (jdbcDataSourceConfig.getModel().equals(JdbcSyncModel.INCREMENT_AFTER_COMPLETE)) {
                    if (completePollOk)
                        row.setJdbcSyncModel(JdbcSyncModel.INCREMENT);
                    else
                        row.setJdbcSyncModel(JdbcSyncModel.COMPLETE);
                }
                else {
                    row.setJdbcSyncModel(jdbcDataSourceConfig.getModel());
                }
                try {
                    row.setPosition(resultSet.getRow());
                }
                catch (Exception e) {
                    //有的jdbc厂商没有实现该方法，hive相关的基本都没有
                }
                Map<String, Object> data = new HashMap<>();
                for (String columnName : columnNames) {
                    Object columnValue = resultSet.getObject(columnName);
                    if (jdbcDataSourceConfig.getPrimaryKeys() != null && jdbcDataSourceConfig.getPrimaryKeys().contains(columnName)) {
                        row.getPrimaryKeysValues().put(columnName, columnValue);
                    }
                    if (columnName.equals(jdbcDataSourceConfig.getRollingFieldName())) {
                        row.setTimeFlag(columnValue);
                    }
                    data.put(columnName, columnValue);
                }
                row.setData(data);
                result.add(row);
                count++;
            }
            else {
                if (!completePollOk) {
                    setCompletePollOk(true);
                }
                nowRowSetEmpty = true;
                try {
                    countDownLatch.countDown();//关闭结果集
                    connection.close();//归还连接给连接池
                }
                catch (Exception e) {
                    log.error("回收数据库连接失败", e);
                }
                break;
            }
        }
        return result;
    }

    @Override
    public Long total() {
        if (jdbcDataSourceConfig.getModel().equals(JdbcSyncModel.COMPLETE) && jdbcDataSourceConfig.getCountSql() != null && jdbcDataSourceConfig.getCountSql().length() > 0) {
            Long total = jdbcTemplate.queryForObject(jdbcDataSourceConfig.getCountSql(), Long.class);
            return total;
        }
        return super.total();
    }

    @Override
    public Object rollbackTransactionUnit(JdbcResult record) {
        //rollback 只有一个回滚单元
        return rollbackUnit;
    }

    @Override
    public void commit(JdbcResult record, BaseSettingContext settingContext) {
        synchronized (tmp) {
            tmp.remove(record);
            if (jdbcDataSourceConfig.getRollingFieldName() != null && jdbcDataSourceConfig.getRollingFieldName().length() > 0) {
                Object commitRecordTime = record.getData().get(jdbcDataSourceConfig.getRollingFieldName());
                if (commitRecordTime == null) {
                    commitRecordTime = record.getData().get(jdbcDataSourceConfig.getRollingFieldName().split("\\.")[1]);
                }
                if (lastTime != null && lastTime instanceof Comparable && lastTime.getClass().equals(commitRecordTime.getClass())) {
                    if (((Comparable) commitRecordTime).compareTo(lastTime) > 0) {
                        setLastTime(commitRecordTime);
                    }
                }
                else {
                    setLastTime(commitRecordTime);
                }
            }
        }
    }

    @Override
    public void commit(Iterable<JdbcResult> records, BaseSettingContext settingContext) {
        records.forEach(jdbcResult -> commit(jdbcResult, settingContext));
    }

    @Override
    public void rollback(JdbcResult record, BaseSettingContext settingContext) {
        //什么事都不用做
    }

    @Override
    public void rollback(Iterable<JdbcResult> records, BaseSettingContext settingContext) {
        //什么事都不用做
    }

    @Override
    public String dataSourceType() {
        return "jdbc-";
    }

    @Override
    public void close() throws IOException {
        if (dataSource != null) {
            try {
                dataSource.close();
            }
            catch (SQLException throwables) {
                log.error("关闭数据源失败！", throwables);
            }
        }
    }

    class QueryForResultSet extends Thread {

        private String sql;

        private Object[] args;

        private CountDownLatch innerCountDownLatch = new CountDownLatch(1);

        private ResultSet resultSet;

        public QueryForResultSet(String sql, Object[] args) {
            this.sql = sql;
            this.args = args;
        }

        @Override
        public void run() {
            super.run();
            try {
                jdbcTemplate.query(sql, (ResultSetExtractor<Object>) rs -> {
                    resultSet = rs;
                    innerCountDownLatch.countDown();
                    try {
                        countDownLatch.await();//等游标处理完毕，jdbc内部会自动关闭
                    }
                    catch (InterruptedException e) {
                        log.error("", e);
                    }
                    return rs;
                }, args);
            }
            catch (Exception e) {
                innerCountDownLatch.countDown();
                log.error("", e);
                throw e;
            }
        }

        public ResultSet getResultSet() {
            try {
                innerCountDownLatch.await();
            }
            catch (InterruptedException e) {
                log.error("", e);
            }
            return resultSet;
        }
    }

}


