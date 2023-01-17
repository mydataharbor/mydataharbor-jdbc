package mydataharbor.plugin.jdbc.mysql.source;

import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.source.jdbc.JdbcDataSource;
import mydataharbor.common.jdbc.source.config.JdbcDataSourceConfig;

/**
 * Created by xulang on 2021/8/19.
 */
@MyDataHarborMarker(title = "mysql8.0.x输出源")
public class JdbcMysql80xDataSource extends JdbcDataSource {

  public JdbcMysql80xDataSource(JdbcDataSourceConfig jdbcDataSourceConfig) {
    super(jdbcDataSourceConfig);
  }

  @Override
  public String driverClassName() {
    return "com.mysql.cj.jdbc.Driver";
  }

  @Override
  public String dataSourceType() {
    return super.dataSourceType() + "mysql-8.0.x";
  }
}
