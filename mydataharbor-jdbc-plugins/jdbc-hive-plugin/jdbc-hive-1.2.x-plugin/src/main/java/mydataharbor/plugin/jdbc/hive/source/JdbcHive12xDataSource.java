package mydataharbor.plugin.jdbc.hive.source;

import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.plugin.jdbc.source.JdbcDataSource;
import mydataharbor.common.jdbc.source.config.JdbcDataSourceConfig;

/**
 * Created by xulang on 2021/8/19.
 */
@MyDataHarborMarker(title = "hive-1.2.x输入源")
public class JdbcHive12xDataSource extends JdbcDataSource {

  public JdbcHive12xDataSource(JdbcDataSourceConfig jdbcDataSourceConfig) {
    super(jdbcDataSourceConfig);
  }

  @Override
  public String driverClassName() {
    return "org.apache.hive.jdbc.HiveDriver";
  }

  @Override
  public String dataSourceType() {
    return super.dataSourceType() + "hive-1.2.x";
  }
}
