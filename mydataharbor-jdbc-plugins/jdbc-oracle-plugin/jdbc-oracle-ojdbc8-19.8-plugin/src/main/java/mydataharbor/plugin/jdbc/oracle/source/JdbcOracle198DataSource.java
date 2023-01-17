package mydataharbor.plugin.jdbc.oracle.source;

import mydataharbor.source.jdbc.JdbcDataSource;
import mydataharbor.common.jdbc.source.config.JdbcDataSourceConfig;

/**
 * Created by xulang on 2021/8/19.
 */
public class JdbcOracle198DataSource extends JdbcDataSource {

  public JdbcOracle198DataSource(JdbcDataSourceConfig jdbcDataSourceConfig) {
    super(jdbcDataSourceConfig);
  }

  @Override
  public String driverClassName() {
    return "oracle.jdbc.driver.OracleDriver";
  }

  @Override
  public String dataSourceType() {
    return super.dataSourceType() + "ojdbc8-19.8.x";
  }
}
