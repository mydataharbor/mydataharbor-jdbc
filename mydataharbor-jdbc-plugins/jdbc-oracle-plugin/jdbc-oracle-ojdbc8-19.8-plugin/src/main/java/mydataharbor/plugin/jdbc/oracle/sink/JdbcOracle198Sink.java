package mydataharbor.plugin.jdbc.oracle.sink;

import mydataharbor.plugin.jdbc.sink.JdbcSink;
import mydataharbor.common.jdbc.sink.config.JdbcSinkConfig;

/**
 * Created by xulang on 2021/8/19.
 */
public class JdbcOracle198Sink extends JdbcSink {

  public JdbcOracle198Sink(JdbcSinkConfig jdbcSinkConfig) {
    super(jdbcSinkConfig);
  }

  @Override
  public String driverClassName() {
    return "oracle.jdbc.driver.OracleDriver";
  }

  @Override
  public String name() {
    return super.name() + "ojdbc8-19.8.x";
  }
}
