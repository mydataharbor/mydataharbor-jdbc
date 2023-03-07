package mydataharbor.plugin.jdbc.clickhouse.source;

import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.plugin.jdbc.source.JdbcDataSource;
import mydataharbor.common.jdbc.source.config.JdbcDataSourceConfig;

/**
 * Created by xulang on 2021/8/19.
 */
@MyDataHarborMarker(title = "clickhouse0.1.x数据源")
public class JdbcClickhouse01xDataSource extends JdbcDataSource {

  public JdbcClickhouse01xDataSource(JdbcDataSourceConfig jdbcDataSourceConfig) {
    super(jdbcDataSourceConfig);
  }

  @Override
  public String driverClassName() {
    return "ru.yandex.clickhouse.ClickHouseDriver";
  }

  @Override
  public String dataSourceType() {
    return super.dataSourceType() + "clickhouse-0.1.x";
  }
}
