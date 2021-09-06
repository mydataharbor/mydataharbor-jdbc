package mydataharbor.plugin.jdbc.mysql.sink;

import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.sink.jdbc.JdbcSink;
import mydataharbor.sink.jdbc.config.JdbcSinkConfig;
import mydataharbor.source.jdbc.JdbcDataSource;
import mydataharbor.source.jdbc.config.JdbcDataSourceConfig;

/**
 * Created by xulang on 2021/8/19.
 */
@MyDataHarborMarker(title = "mysql5.1.x数据源")
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
}
