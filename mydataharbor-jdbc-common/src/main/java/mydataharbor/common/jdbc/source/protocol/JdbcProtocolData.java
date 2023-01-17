package mydataharbor.common.jdbc.source.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mydataharbor.IProtocolData;
import mydataharbor.common.jdbc.source.JdbcResult;

/**
 * Created by xulang on 2021/8/22.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JdbcProtocolData implements IProtocolData {

  private JdbcResult jdbcResult;

  @Override
  public String protocolName() {
    return "mydataharbor.common.jdbc-protocal-data";
  }

}
