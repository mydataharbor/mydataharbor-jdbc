package mydataharbor.plugin.jdbc.postgresql.sink;

import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.common.jdbc.sink.JdbcSinkReq;
import mydataharbor.common.jdbc.sink.config.JdbcSinkConfig;
import mydataharbor.sink.jdbc.JdbcSink;

/**
 * Created by xulang on 2021/8/19.
 */
@MyDataHarborMarker(title = "postgresql42.5.x数据源")
public class JdbcPostgresql425xSink extends JdbcSink {

  public JdbcPostgresql425xSink(JdbcSinkConfig jdbcSinkConfig) {
    super(jdbcSinkConfig);
  }

  @Override
  public String driverClassName() {
    return "org.postgresql.Driver";
  }

  @Override
  public String name() {
    return super.name() + "postgresql-42.5.x";
  }

}
