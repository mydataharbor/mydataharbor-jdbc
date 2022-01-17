package mydataharbor.plugin.jdbc.mysql;

import lombok.Data;
import mydataharbor.*;
import mydataharbor.exception.ResetException;
import mydataharbor.pipline.CommonDataPipline;
import mydataharbor.plugin.base.util.JsonUtil;
import mydataharbor.plugin.jdbc.mysql.sink.JdbcMysql51xSink;
import mydataharbor.plugin.jdbc.mysql.source.JdbcMysql51xDataSource;
import mydataharbor.setting.BaseSettingContext;
import mydataharbor.sink.jdbc.JdbcSinkReq;
import mydataharbor.sink.jdbc.config.JdbcSinkConfig;
import mydataharbor.source.jdbc.JdbcResult;
import mydataharbor.source.jdbc.config.JdbcDataSourceConfig;
import mydataharbor.source.jdbc.protocal.JdbcProtocalConvertor;
import mydataharbor.source.jdbc.protocal.JdbcProtocalData;
import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;

/**
 * Created by xulang on 2021/12/8.
 */
@Extension
public class MysqlToMysqlCreator implements IDataSinkCreator<MysqlToMysqlCreator.MysqlToMysqlPipelineCreatorConfig, BaseSettingContext>, ExtensionPoint {

  @Override
  public String type() {
    return "mysql-2-mysql";
  }

  @Override
  public IDataPipline createPipline(MysqlToMysqlPipelineCreatorConfig config, BaseSettingContext settingContext) throws Exception {
    CommonDataPipline commonDataPipline = CommonDataPipline.builder()
      .dataSource(new JdbcMysql51xDataSource(config.jdbcDataSourceConfig))
      .protocalDataConvertor(new JdbcProtocalConvertor())
      .dataConvertor(new IDataConvertor<JdbcProtocalData, JdbcSinkReq, BaseSettingContext>() {
        @Override
        public JdbcSinkReq convert(JdbcProtocalData record, BaseSettingContext settingContext) throws ResetException {
          JdbcSinkReq jdbcSinkReq = new JdbcSinkReq();
          jdbcSinkReq.setWriteDataInfos();
          return null;
        }
      })
      .sink(new JdbcMysql51xSink(config.jdbcSinkConfig)).build();
    return commonDataPipline;
  }

  @Override
  public <T> T parseJson(String json, Class<T> clazz) {
    return JsonUtil.jsonToObject(json, clazz);
  }

  @Data
  public static class MysqlToMysqlPipelineCreatorConfig {
    private JdbcDataSourceConfig jdbcDataSourceConfig;

    private JdbcSinkConfig jdbcSinkConfig;
  }
}


