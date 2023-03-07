package mydataharbor.plugin.jdbc.hive.source;

import mydataharbor.plugin.jdbc.source.JdbcDataSource;
import mydataharbor.common.jdbc.source.config.JdbcDataSourceConfig;

/**
 * Created by xulang on 2021/8/19.
 */
public class JdbcHive31xDataSource extends JdbcDataSource {

  public JdbcHive31xDataSource(JdbcDataSourceConfig jdbcDataSourceConfig) {
    super(jdbcDataSourceConfig);
  }

  @Override
  public String driverClassName() {
    return "org.apache.hive.jdbc.HiveDriver";
  }

  @Override
  public String dataSourceType() {
    return super.dataSourceType() + "hive-3.1.x";
  }
}
