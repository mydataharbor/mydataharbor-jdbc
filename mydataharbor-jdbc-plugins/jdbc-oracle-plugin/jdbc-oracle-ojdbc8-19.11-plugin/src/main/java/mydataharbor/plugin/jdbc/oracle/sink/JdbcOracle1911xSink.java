package mydataharbor.plugin.jdbc.oracle.sink;

import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.plugin.jdbc.sink.JdbcSink;
import mydataharbor.common.jdbc.sink.config.JdbcSinkConfig;

/**
 * Created by xulang on 2021/8/19.
 */
@MyDataHarborMarker(title = "Oracle19.11.x输出源")
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
