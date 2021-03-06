package mydataharbor.source.jdbc.protocal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mydataharbor.IProtocalData;
import mydataharbor.source.jdbc.JdbcResult;

/**
 * Created by xulang on 2021/8/22.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JdbcProtocalData implements IProtocalData {

  private JdbcResult jdbcResult;

  @Override
  public String protocalName() {
    return "jdbc-protocal-data";
  }

}
