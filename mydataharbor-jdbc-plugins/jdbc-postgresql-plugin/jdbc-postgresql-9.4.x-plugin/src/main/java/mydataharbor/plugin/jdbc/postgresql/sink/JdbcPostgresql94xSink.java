package mydataharbor.plugin.jdbc.postgresql.sink;

import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.common.jdbc.sink.config.JdbcSinkConfig;
import mydataharbor.plugin.jdbc.sink.JdbcSink;

/**
 * Created by xulang on 2021/8/19.
 */
@MyDataHarborMarker(title = "postgresql9.4.x数据源")
public class JdbcPostgresql94xSink extends JdbcSink {

  public JdbcPostgresql94xSink(JdbcSinkConfig jdbcSinkConfig) {
    super(jdbcSinkConfig);
  }

  @Override
  public String driverClassName() {
    return "org.postgresql.Driver";
  }

  @Override
  public String name() {
    return super.name() + "postgresql-9.4.x";
  }

}
