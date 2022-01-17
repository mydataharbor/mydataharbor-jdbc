package mydataharbor.plugin.jdbc.mysql.sink;

import mydataharbor.sink.jdbc.JdbcSink;
import mydataharbor.sink.jdbc.JdbcSinkReq;
import mydataharbor.sink.jdbc.config.JdbcSinkConfig;
import mydataharbor.source.jdbc.JdbcDataSource;
import mydataharbor.source.jdbc.config.JdbcDataSourceConfig;

/**
 * Created by xulang on 2021/8/19.
 */
public class JdbcMysql60xSink extends JdbcSink {

  public JdbcMysql60xSink(JdbcSinkConfig jdbcSinkConfig) {
    super(jdbcSinkConfig);
  }

  @Override
  public String driverClassName() {
    return "com.mysql.cj.jdbc.Driver";
  }

  @Override
  public String name() {
    return super.name() + "mysql-6.0.x";
  }

  @Override
  public Object[] generateSave(JdbcSinkReq.WriteDataInfo writeDataInfo, String insertColumnNames, String updateColumnNames, String valuePlaceholder, Object[] values, StringBuilder whereSql, Object[] whereValues, StringBuilder sql) {
    sql.append("REPLACE INTO ");
    sql.append(writeDataInfo.getTableName());
    sql.append(" ");
    sql.append(insertColumnNames);
    sql.append(" VALUES ");
    sql.append(valuePlaceholder);
    return values;
  }
}
