package mydataharbor.plugin.jdbc.source.protocol;

import mydataharbor.IProtocolDataConverter;
import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.common.jdbc.source.JdbcResult;
import mydataharbor.common.jdbc.source.protocol.JdbcProtocolData;
import mydataharbor.exception.ResetException;
import mydataharbor.setting.BaseSettingContext;

/**
 * Created by xulang on 2021/8/22.
 */
@MyDataHarborMarker(title = "jdbc输入源默认协议数据转换器")
public class JdbcProtocolConvertor implements IProtocolDataConverter<JdbcResult, JdbcProtocolData, BaseSettingContext> {
  @Override
  public JdbcProtocolData convert(JdbcResult record, BaseSettingContext settingContext) throws ResetException {
    return new JdbcProtocolData(record);
  }
}
