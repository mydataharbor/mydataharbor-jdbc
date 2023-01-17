package mydataharbor.common.jdbc.exception;

/**
 * Created by xulang on 2021/8/18.
 */
public class JdbcDataSourceCreateException extends RuntimeException{
  public JdbcDataSourceCreateException() {
  }

  public JdbcDataSourceCreateException(String message) {
    super(message);
  }

  public JdbcDataSourceCreateException(String message, Throwable cause) {
    super(message, cause);
  }

  public JdbcDataSourceCreateException(Throwable cause) {
    super(cause);
  }

  public JdbcDataSourceCreateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
