package mydataharbor.plugin.jdbc.oracle.source;

import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.plugin.jdbc.source.JdbcDataSource;
import mydataharbor.common.jdbc.source.config.JdbcDataSourceConfig;

/**
 * Created by xulang on 2021/8/19.
 */
@MyDataHarborMarker(title = "Oracle19.11.x输入源")
public class JdbcOracle1911xDataSource extends JdbcDataSource {

  public JdbcOracle1911xDataSource(JdbcDataSourceConfig jdbcDataSourceConfig) {
    super(jdbcDataSourceConfig);
  }

  @Override
  public String driverClassName() {
    return "oracle.jdbc.driver.OracleDriver";
  }

  @Override
  public String dataSourceType() {
    return super.dataSourceType() + "ojdbc8-19.11.x";
  }
}
