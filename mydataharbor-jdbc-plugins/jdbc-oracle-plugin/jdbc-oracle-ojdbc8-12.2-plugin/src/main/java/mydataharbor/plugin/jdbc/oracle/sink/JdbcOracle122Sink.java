package mydataharbor.plugin.jdbc.oracle.sink;

import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.plugin.jdbc.sink.JdbcSink;
import mydataharbor.common.jdbc.sink.config.JdbcSinkConfig;

/**
 * Created by xulang on 2021/8/19.
 */
@MyDataHarborMarker(title = "Oracle12.2.x输出源")
public class JdbcOracle122Sink extends JdbcSink {

  public JdbcOracle122Sink(JdbcSinkConfig jdbcSinkConfig) {
    super(jdbcSinkConfig);
  }

  @Override
  public String driverClassName() {
    return "oracle.jdbc.driver.OracleDriver";
  }

  @Override
  public String name() {
    return super.name() + "ojdbc8-21.1.x";
  }
}
