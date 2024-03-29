package mydataharbor.plugin.jdbc.mysql.sink;

import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.plugin.jdbc.sink.JdbcSink;
import mydataharbor.common.jdbc.sink.JdbcSinkReq;
import mydataharbor.common.jdbc.sink.config.JdbcSinkConfig;

/**
 * Created by xulang on 2021/8/19.
 */
@MyDataHarborMarker(title = "mysql5.1.x输出源")
public class JdbcMysql51xSink extends JdbcSink {

  public JdbcMysql51xSink(JdbcSinkConfig jdbcSinkConfig) {
    super(jdbcSinkConfig);
  }

  @Override
  public String driverClassName() {
    return "com.mysql.jdbc.Driver";
  }

  @Override
  public String name() {
    return super.name() + "mysql-5.1.x";
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
