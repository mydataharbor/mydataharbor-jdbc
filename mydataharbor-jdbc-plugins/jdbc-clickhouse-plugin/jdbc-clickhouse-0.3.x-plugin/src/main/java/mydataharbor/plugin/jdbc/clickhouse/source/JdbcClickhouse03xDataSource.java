package mydataharbor.plugin.jdbc.clickhouse.source;

import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.plugin.jdbc.source.JdbcDataSource;
import mydataharbor.common.jdbc.source.config.JdbcDataSourceConfig;

/**
 * Created by xulang on 2021/8/19.
 */
@MyDataHarborMarker(title = "clickhouse-0.3.x输入源")
public class JdbcClickhouse03xDataSource extends JdbcDataSource {

  public JdbcClickhouse03xDataSource(JdbcDataSourceConfig jdbcDataSourceConfig) {
    super(jdbcDataSourceConfig);
  }

  @Override
  public String driverClassName() {
    return "ru.yandex.clickhouse.ClickHouseDriver";
  }

  @Override
  public String dataSourceType() {
    return super.dataSourceType() + "clickhouse-0.3.x";
  }
}
