package mydataharbor.common.jdbc.sink;

public enum JdbcDataSinkType {
    oracle_19_3("mydataharbor.common.jdbc-oracle-ojdbc8-19.3-plugin", "mydataharbor.plugin.mydataharbor.common.jdbc.oracle.sink.JdbcOracle193Sink"),
    oracle_19_6("mydataharbor.common.jdbc-oracle-ojdbc8-19.6-plugin", "mydataharbor.plugin.mydataharbor.common.jdbc.oracle.sink.JdbcOracle196Sink"),
    mysql_8_0_x("mydataharbor.common.jdbc-mysql-8.0.x-plugin", "mydataharbor.plugin.mydataharbor.common.jdbc.mysql.sink.JdbcMysql80xSink"),
    clickhouse_0_3_x("mydataharbor.common.jdbc-clickhouse-0.3.x-plugin", "mydataharbor.plugin.mydataharbor.common.jdbc.clickhouse.sink.JdbcClickhouse03xSink"),
    ;

    private String pluginId;
    private String clazz;

    JdbcDataSinkType(String pluginId, String clazz) {
        this.pluginId = pluginId;
        this.clazz = clazz;
    }

    public String getPluginId() {
        return pluginId;
    }

    public String getClazz() {
        return clazz;
    }
}