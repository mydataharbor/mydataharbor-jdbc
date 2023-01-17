package mydataharbor.plugin.jdbc.oracle.sink;

import mydataharbor.sink.jdbc.JdbcSink;
import mydataharbor.common.jdbc.sink.config.JdbcSinkConfig;

/**
 * Created by xulang on 2021/8/19.
 */
public class JdbcOracle1911xSink extends JdbcSink {

  public JdbcOracle1911xSink(JdbcSinkConfig jdbcSinkConfig) {
    super(jdbcSinkConfig);
  }

  @Override
  public String driverClassName() {
    return "oracle.jdbc.driver.OracleDriver";
  }

  @Override
  public String name() {
    return super.name() + "ojdbc8-19.11.x";
  }
}
