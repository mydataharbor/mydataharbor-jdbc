package mydataharbor.common.jdbc.source;

public enum JdbcDataSourceType {
    oracle_19_3("mydataharbor.common.jdbc-oracle-ojdbc8-19.3-plugin", "mydataharbor.plugin.mydataharbor.common.jdbc.oracle.source.JdbcOracle193DataSource"),
    oracle_19_6("mydataharbor.common.jdbc-oracle-ojdbc8-19.6-plugin", "mydataharbor.plugin.mydataharbor.common.jdbc.oracle.source.JdbcOracle196DataSource"),
    mysql_8_0_x("mydataharbor.common.jdbc-mysql-8.0.x-plugin", "mydataharbor.plugin.mydataharbor.common.jdbc.mysql.source.JdbcMysql80xDataSource"),
    clickhouse_0_3_x("mydataharbor.common.jdbc-clickhouse-0.3.x-plugin","mydataharbor.plugin.mydataharbor.common.jdbc.clickhouse.source.JdbcClickhouse03xDataSource"),
    ;

    private String pluginId;
    private String clazz;

    JdbcDataSourceType(String pluginId, String clazz) {
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