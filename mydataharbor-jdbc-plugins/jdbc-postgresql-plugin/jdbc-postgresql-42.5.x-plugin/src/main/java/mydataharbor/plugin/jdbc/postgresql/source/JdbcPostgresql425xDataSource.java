package mydataharbor.plugin.jdbc.postgresql.source;

import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.common.jdbc.source.config.JdbcDataSourceConfig;
import mydataharbor.source.jdbc.JdbcDataSource;

/**
 * Created by xulang on 2021/8/19.
 */
@MyDataHarborMarker(title = "postgresql-42.5.x输出源")
public class JdbcPostgresql425xDataSource extends JdbcDataSource {

  public JdbcPostgresql425xDataSource(JdbcDataSourceConfig jdbcDataSourceConfig) {
    super(jdbcDataSourceConfig);
  }

  @Override
  public String driverClassName() {
    return "org.postgresql.Driver";
  }

  @Override
  public String dataSourceType() {
    return super.dataSourceType() + "postgresql-42.5.x";
  }
}
