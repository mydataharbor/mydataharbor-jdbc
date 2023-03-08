package mydataharbor.plugin.jdbc.clickhouse.sink;

import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.exception.ResetException;
import mydataharbor.setting.BaseSettingContext;
import mydataharbor.plugin.jdbc.sink.JdbcSink;
import mydataharbor.common.jdbc.sink.JdbcSinkReq;
import mydataharbor.common.jdbc.sink.config.JdbcSinkConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xulang on 2021/8/19.
 */
@MyDataHarborMarker(title = "clickhouse-0.3.x输出源")
public class JdbcClickhouse03xSink extends JdbcSink {

    public JdbcClickhouse03xSink(JdbcSinkConfig jdbcSinkConfig) {
        super(jdbcSinkConfig);
    }

    @Override
    public String driverClassName() {
        return "ru.yandex.clickhouse.ClickHouseDriver";
    }

    @Override
    public String name() {
        return super.name() + "clickhouse-0.3.x";
    }

    /**
     * clickhouse支持insert已经存在的数据
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
    @Override
    public Object[] generateSave(JdbcSinkReq.WriteDataInfo writeDataInfo, String insertColumnNames, String updateColumnNames, String valuePlaceholder, Object[] values, StringBuilder whereSql, Object[] whereValues, StringBuilder sql) {
        sql.append("INSERT INTO ");
        sql.append(writeDataInfo.getTableName());
        sql.append(" ");
        sql.append(insertColumnNames);
        sql.append(" VALUES ");
        sql.append(valuePlaceholder);
        return values;
    }

    @Override
    public boolean enableTransaction() {
        return false;
    }

    @Override
    public WriterResult write(List<JdbcSinkReq> records, BaseSettingContext settingContext) throws ResetException {
        WriterResult writerResult = super.write(records, settingContext);
        Set<String> tableNames = new HashSet<>();
        for (JdbcSinkReq record : records) {
            for (JdbcSinkReq.WriteDataInfo writeDataInfo : record.getWriteDataInfos()) {
                tableNames.add(writeDataInfo.getTableName());
            }
        }
        for (String tableName : tableNames) {
            jdbcTemplate.execute("optimize table " + tableName + " final;");
        }
        return writerResult;
    }

}
