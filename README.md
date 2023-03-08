# mydataharbor-jdbc
# 作者

MyDataHarbor([1053618636@qq.com](mailto:1053618636@qq.com))

# 项目介绍

该项目是为MyDataHarbor实现jdbc的DataSource 和 Sink，让使用者可以从数据库抽取数据，或者将数据写入数据库。

jdbc抽取数据有三种模式，全量/增量/先全量再增量 ，❗ 此种方式无法同步删除的数据。

# 实现版本

| 中间件/协议 | 数据源（DataSource）                                         | 写入源（Sink）                                          |
| ----------- | ------------------------------------------------------------ | ------------------------------------------------------- |
| jdbc        | ✅mysql全部版本<br>✅oracle全部版本 <br>✅hive全部版本 <br>✅PostgreSQL<br>✅clickhouse | ✅mysql全部版本 <br>✅oracle全部版本 <br>✅PostgreSQL <br>✅clickhouse|

# 配置

## DataSource配置

```json
{
    "speed": 100,
    "url": "",
    "username": "",
    "password": "",
    "querySql": "",
    "maxPollRecords": 1,
    "model": "INCREMENT",
    "countSql": "",
    "rollingFieldName": "updated_at",
    "startTime": "2021-02-22 15:47:00",
    "timeFormat": "yyyy-MM-dd HH:mm:ss",
    "primaryKeys": [
      "id"
    ]
  }
```

## Sink配置

```json
{
    "url": "",
    "username": "",
    "password": "",
    "defaultTableName": ""
  }
```
## 更新记录
### 2.0.0
1、offset数据持久化，输入源bug修复，只能运行在2.x的主程序上
2、新增clickhouse和postgresql类型的驱动

