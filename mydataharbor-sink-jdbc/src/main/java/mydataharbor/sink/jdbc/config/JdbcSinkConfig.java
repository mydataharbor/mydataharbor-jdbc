package mydataharbor.sink.jdbc.config;

import lombok.Data;
import mydataharbor.classutil.classresolver.MyDataHarborMarker;
import mydataharbor.config.AbstractConfig;

@Data
public class JdbcSinkConfig extends AbstractConfig {

  public static final String MILLI_SECOND = "MILLI_SECOND";

  public static final String SECOND = "SECOND";

  @MyDataHarborMarker(title = "jdbc连接url")
  private String url;

  @MyDataHarborMarker(title = "用户名")
  private String username;

  @MyDataHarborMarker(title = "密码")
  private String password;

  @MyDataHarborMarker(title = "任务创建时和数据库的连接数", defaultValue = "1")
  private Integer initialSize = 1;

  @MyDataHarborMarker(title = "只有当写入数据库时发生IOException时才回滚",des = "默认true，如果为false，则当写入数据库发生任何异常时都回滚重试。")
  private Boolean onlyOnIOExceptionRollback = true;

}