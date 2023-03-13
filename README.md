## 超表的创建

```postgresql
-- 创建普通数据表
CREATE TABLE stocks_real_time (
time TIMESTAMPTZ NOT NULL,
symbol TEXT NOT NULL,
price DOUBLE PRECISION NULL,
day_volume INT NULL
);

-- 通过 time 进行分区将普通表转换为超表
SELECT create_hypertable('stocks_real_time','time');

-- 创建索引以高效查询 symbol 和 time 列
CREATE INDEX ix_symbol_time ON stocks_real_time (symbol, time DESC);

-- timescaleDB 仍然是一个 postgrepSQL 库，可以新建普通的数据表
CREATE TABLE company (
symbol TEXT NOT NULL,
name TEXT NOT NULL
);
```

接下来为创建的超表以及普通数据表导入数据，官方文档中为我们提供了一些实时数据，可以进行下载并按步骤进行数据的导入：股票交易数据导入

数据插入后若day_volume无数据，可以通过下列语句插入模拟数据：

```postgresql
-- 执行两个文件数据的插入
-- 若无交易数量人为补充
UPDATE stocks_real_time SET day_volume=(random()*(100000-1)+1);
```

### 数据简单查询
stocks_real_time表提供了时间、股票代码、交易价格、成交量等数据

company表提供了股票代码和公司名的映射关系

利用 TimescaleDB 提供的一些函数可以实现一些时序数据的快速查询，例如：

```postgresql
-- 查询近 4 天的股票数据
SELECT * FROM stocks_real_time srt
WHERE time > now() - INTERVAL '4 days';

-- 查询 Amazon 公司最近 10 条股票交易信息
SELECT * FROM stocks_real_time srt
WHERE symbol = 'AMZN'
ORDER BY time DESC, day_volume desc
LIMIT 10;

-- 计算近 4 天 Apple 公司的股票平均交易价格
SELECT
avg(price)
FROM stocks_real_time srt
JOIN company c ON c.symbol = srt.symbol
WHERE c.name = 'Apple' AND time > now() - INTERVAL '4 days';

-- first() and last()
-- 获取所有公司近三天来的第一笔成交价和最后一笔成交价
SELECT symbol, first(price,time), last(price, time)
FROM stocks_real_time srt
WHERE time > now() - INTERVAL '3 days'
GROUP BY symbol
ORDER BY symbol;

-- 查询一周内每个公司每天的股票平均交易额
SELECT
time_bucket('1 day', time) AS bucket,
symbol,
avg(price)
FROM stocks_real_time srt
WHERE time > now() - INTERVAL '1 week'
GROUP BY bucket, symbol
ORDER BY bucket, symbol;
```

### 数据连续聚合以及更新策略
TimescaleDB 利用视图提供了连续聚合的方法，并可以通过配置更新策略自动的实现对聚合查询的更新操作。

当首次创建连续聚合时，TimescaleDB 会根据我们指定的聚合标准对现有的元数据进行聚合。即使存在某时间段内的数据不完整的情况，TimescaleDB 也会对该部分数据进行聚合。

当为连续聚合查询配置相应的更新策略后，TimescaleDB 会根据策略中的时间点和指定时间段，自动的更新聚合查询。此时若存在某时间段内数据不完整的情况，TimescaleDB 则不会对该部分数据进行聚合计算，而是等设置的时间范围完整以后再进行聚合。

**TimescaleDB 中进行连续聚合以及更新策略的设置语句如下：**

```postgresql
-- 普通聚合查询：查询各个股票每天的开盘价格、闭盘价格、以及最高、最低交易价格
SELECT
time_bucket('1 day', "time") AS day,
symbol,
max(price) AS high,
first(price, time) AS open,
last(price, time) AS close,
min(price) AS low
FROM stocks_real_time srt
GROUP BY day, symbol
ORDER BY day DESC, symbol;

-- 连续聚合查询
CREATE MATERIALIZED VIEW stock_candlestick_daily
WITH (timescaledb.continuous) AS
SELECT
time_bucket('1 day', "time") AS day,
symbol,
max(price) AS high,
first(price, time) AS open,
last(price, time) AS close,
min(price) AS low
FROM stocks_real_time srt
GROUP BY day, symbol;

-- 注意：当连续聚合被创建时，其默认开启实时聚合功能，即未被聚合到视图中的数据将被实时聚合并记录到视图当中。若实时插入的数据量非常庞大，则会影响查询的性能
-- 可以通过下列语句关闭实时聚合功能
ALTER MATERIALIZED VIEW stock_candlestick_daily SET (timescaledb.materialized_only = true);

-- 查询聚合结果
SELECT * FROM stock_candlestick_daily
ORDER BY day DESC, symbol;

-- 设置自动更新策略
-- 时间段为 3天前 到 1小时前，以当前时间为基准
-- 策略每天执行 1 次，由 schedule_interval 设置
-- 策略执行的查询是连续聚合 stock_candlestick_daily 中定义的查询
SELECT add_continuous_aggregate_policy('stock_candlestick_daily',
start_offset => INTERVAL '3 days',
end_offset => INTERVAL '1 hour',
schedule_interval => INTERVAL '1 days');

-- 设置手动更新策略
-- 用于更新自动更新时间范围外的聚合数据，如掉线重连后发送的历史数据
-- 下列策略将更新 1周前 到 现在 的连续聚合
-- 手动刷新仅会更新一次连续聚合
CALL refresh_continuous_aggregate(
'stock_candlestick_daily',
now() - INTERVAL '1 week',
now()
);

-- 连续聚合的详细信息查询
SELECT * FROM timescaledb_information.continuous_aggregates;

```

### 数据压缩策略
TimescaleDB 提供了数据压缩的功能，使用户在查询和分析大量的历史数据的同时可以节约存储空间，PostgreSQL 提供的所有数据类型均可被压缩。

TimescaleDB 中设置数据压缩以及压缩策略的语句如下：

```postgresql
-- 数据压缩
-- 首先需要设置允许超表进行数据压缩
ALTER TABLE stocks_real_time SET (
timescaledb.compress,
timescaledb.compress_orderby = 'time DESC',
timescaledb.compress_segmentby = 'symbol'
);
-- 查看确认超表的压缩参数
SELECT * FROM timescaledb_information.compression_settings;

-- 自动压缩策略
-- 自动对两周之前的数据进行压缩，并创建循环规则
-- 被压缩的分区可以执行数据的插入，但是无法更新和删除
SELECT add_compression_policy('stocks_real_time', INTERVAL '2 weeks');
-- 查看压缩策略的细节
SELECT * FROM timescaledb_information.jobs;
-- 查看压缩策略的统计信息
SELECT * FROM timescaledb_information.job_stats;

-- 手动压缩策略
-- 对两周之前的数据进行压缩
-- 策略最好加入 if_not_compressed=>true 语句，否则如果对已经压缩过的分区进行压缩，数据库将会报错
SELECT compress_chunk(i, if_not_compressed=>true)
FROM show_chunks('stocks_real_time', older_than => INTERVAL ' 2 weeks') i;

-- 查看压缩后数据的结果
SELECT pg_size_pretty(before_compression_total_bytes) as "before compression",
pg_size_pretty(after_compression_total_bytes) as "after compression"
FROM hypertable_compression_stats('stocks_real_time');

```

### 数据保留策略
在开发中，随着新产生的数据越来越多，旧数据的价值也就越来越小，并逐渐变得很少去更新甚至查询。为此，TimescaleDB 提供了数据保留的策略，可以通过设置保留策略定期清理旧的数据，释放存储空间。

值得注意的一点是，旧数据的清理仅仅会清理元数据，而不会影响自动聚合中的聚合数据，只要在删除数据期间不执行聚合的更新即可！



**TimescaleDB 中设置数据保留策略的语句如下：**

```postgresql

-- 自动保留策略
-- 自动删除 3周 之前的数据，并创建循环规则
SELECT add_retention_policy('stocks_real_time', INTERVAL '3 weeks');
-- 查询保留策略的相关细节
SELECT * FROM timescaledb_information.jobs;
-- 查询保留策略的统计信息
SELECT * FROM timescaledb_information.job_stats;


-- 手动保留策略
-- 手动删除 3周 之前的数据
SELECT drop_chunks('stocks_real_time', INTERVAL '3 weeks');
-- 手动删除 2周 之前，3周 之内的数据
SELECT drop_chunks(
'stocks_real_time',
older_than => INTERVAL '2 weeks',
newer_than => INTERVAL '3 weeks'
);

-- 查询保留策略的信息
SELECT * FROM timescaledb_information.jobs;
SELECT * FROM timescaledb_information.job_stats;
```

### TimescaleDB – 常用 API
- 超表以及块 chunks 相关
```postgresql

-- create_hypertable(relation, time_column_name)：基于pg表创建超表，pg表不能是已经分区的表，如果时非空表需要使用migrate_data进行数据迁移；
SELECT create_hypertable('conditions', 'time');

-- show_chunks(relation)：获得超表的块列表
SELECT show_chunks('conditions');

-- drop_chunks(relation, older_than)：删除指定时间标准的数据块
SELECT drop_chunks('conditions', INTERVAL '3 months');

-- reorder_chunk(chunk, index)：按索引顺序对单个数据块重新排序
SELECT reorder_chunk('_timescaledb_internal._hyper_1_10_chunk', 'conditions_device_id_time_idx');

-- move_chunk(chunk, destination_tablespace, index_destination_tablespace)：将数据和索引移动到新的表空间
SELECT move_chunk(
chunk => '_timescaledb_internal._hyper_1_4_chunk',
destination_tablespace => 'tablespace_2',
index_destination_tablespace => 'tablespace_3',
reorder_index => 'conditions_device_id_time_idx',
verbose => TRUE
);

-- reorder_chunk(hypertable, index_name)：创建一个策略根据索引对超表进行重新排序，每个超表只能存在一个重排策略
SELECT add_reorder_policy('conditions', 'conditions_device_id_time_idx');

-- remove_reorder_policy(hypertable)：删除超表的重排策略
SELECT remove_reorder_policy('conditions', if_exists => true);

-- attach_tablespace(tablespace, hypertable)：为超表附加一个表空间并用它存储块数据
SELECT attach_tablespace('disk1', 'conditions');

-- detach_tablespace(tablespace)：从一个或多个表中分离表空间
SELECT detach_tablespace('disk1', 'conditions');

-- detach_tablespaces(hypertable)：分离指定超表的所有表空间
SELECT detach_tablespaces('conditions');

-- show_tablespaces(hypertable)：展示超表所有的表空间
SELECT * FROM show_tablespaces('conditions');

-- set_chunk_time_interval(hypertable, chunk_time_interval)：设置超表的分区时间间隔，在创建新数据块时使用新间隔，已有数据块时间间隔不变
SELECT set_chunk_time_interval('conditions', INTERVAL '24 hours');

-- set_integer_now_func(main_table, integer_now_func)：此函数仅适用于具有整数时间值的超表，它设置了一个函数，该函数以数值型返回当前时间now
SELECT set_integer_now_func('test_table_bigint', 'unix_now');

-- add_dimension(hypertable, column_name)：向超表添加额外的分区维度
SELECT add_dimension('conditions', 'location', number_partitions => 4);
SELECT add_dimension('conditions', 'time_received', chunk_time_interval => INTERVAL '1 day');

-- CREATE INDEX ... WITH (timescaledb.transaction_per_chunk, ...); 该语句扩展了 create index，使其可以对要创建索引的每一个数据分块单独的使用事务，而不是对整个超表使用单个事务
CREATE INDEX ON conditions(time, device_id)
WITH (timescaledb.transaction_per_chunk);

-- hypertable_size(hypertable)：获取超表的总磁盘空间，以字节为单位返回
SELECT hypertable_size('conditions') ;

-- hypertable_detailed_size(hypertable)：获取有关超表使用磁盘空间的详细信息
SELECT * FROM hypertable_detailed_size('stocks_real_time') ORDER BY node_name;

-- hypertable_index_size(index_name)：获取超表上索引使用的磁盘空间
SELECT hypertable_index_size('stocks_real_time_time_idx');

-- chunks_detailed_size(hypertable)：获取属于超表的块的所有磁盘空间信息
SELECT * FROM chunks_detailed_size('stocks_real_time')
ORDER BY chunk_name, node_name;
```

- 连续聚合相关

```postgresql
-- create materialized view 语句用于创建连续聚合
CREATE MATERIALIZED VIEW continuous_aggregate_daily( timec, minl, sumt, sumh )
WITH (timescaledb.continuous) AS
SELECT time_bucket('1day', timec), min(location), sum(temperature), sum(humidity)
FROM conditions
GROUP BY time_bucket('1day', timec)

-- alter materialized view 语句用于修改连续聚合视图的某些选项
ALTER MATERIALIZED VIEW contagg_view SET (timescaledb.materialized_only = true);
ALTER MATERIALIZED VIEW contagg_view SET (timescaledb.compress = true);

-- drop materialized view 语句用于删除连续聚合
DROP MATERIALIZED VIEW contagg_view;

-- refresh_continuous_aggregate(continuous_aggregate, window_start, window_end)：手动刷新指定连续聚合视图的指定时间窗口的数据
CALL refresh_continuous_aggregate('conditions', '2020-01-01', '2020-02-01');

-- add_continuous_aggregate_policy(continuous_aggregate, start_offset, end_offset, schedule_interval)：向指定的连续聚合添加连续聚合自动聚合策略
SELECT add_continuous_aggregate_policy('conditions_summary',
start_offset => INTERVAL '1 month',
end_offset => INTERVAL '1 hour',
schedule_interval => INTERVAL '1 hour');

-- remove_continuous_aggregate_policy(continuous_aggregate)：删除指定连续聚合的刷新策略
SELECT remove_continuous_aggregate_policy('cpu_view');

-- add_policies(continuous_aggregate)：一次性的设置连续聚合的刷新、压缩以及数据保留策略。添加的策略仅适用于连续聚合而不适用于原始超表
SELECT timescaledb_experimental.add_policies(
'example_continuous_aggregate',
refresh_start_offset => '1 day'::interval,
refresh_end_offset => '2 day'::interval,
compress_after => '20 days'::interval,
drop_after => '1 year'::interval
);

-- alter_policies(continuous_aggregate)：更改连续聚合的刷新、压缩以及数据保留策略。策略仅适用于连续聚合而不适用于原始超表
SELECT timescaledb_experimental.alter_policies(
'continuous_agg_max_mat_date',
compress_after => '16 days'::interval
);

-- show_policies(continuous_aggregate)：展示指定连续聚合所有的策略
SELECT timescaledb_experimental.show_policies('example_continuous_aggregate');

-- remove_policies(continuous_aggregate)：删除指定连续聚合的刷新、压缩以及保留策略（仅适用于连续聚合而不适用于原始超表）
SELECT timescaledb_experimental.remove_policies(
'example_continuous_aggregate',
false,
'policy_refresh_continuous_aggregate',
'policy_retention'
);

-- remove_all_policies(continuous_aggregate)：删除指定连续聚合的所有策略
SELECT timescaledb_experimental.remove_all_policies('example_continuous_aggregate');

```

- 压缩策略相关
```postgresql
-- ALTER TABLE <table_name> SET (timescaledb.compress)：alert table 语句被用来打开压缩并设置压缩选项
ALTER TABLE metrics SET (timescaledb.compress, timescaledb.compress_orderby = 'time DESC', timescaledb.compress_segmentby = 'device_id');

-- add_compression_policy(hypertable, compress_after)：设置一个压缩策略，对设定的指定时间的数据进行压缩
SELECT add_compression_policy('cpu', INTERVAL '60d');

-- remove_compression_policy(hypertable)：移除超表压缩策略
SELECT remove_compression_policy('cpu');

-- compress_chunk(chunk_name)：对特定数据分区块进行压缩（推荐使用）
SELECT compress_chunk('_timescaledb_internal._hyper_1_2_chunk');

-- decompress_chunk(chunk_name)：对特定数据分区进行解压缩，若需要修改或添加新数据到已经压缩的块，则必须先解压缩
SELECT decompress_chunk('_timescaledb_internal._hyper_2_2_chunk');

-- recompress_chunk(chunk_name)：重新压缩某数据分区，用于对添加了新数据的已经压缩过的分区进行重压缩
CALL recompress_chunk('_timescaledb_internal._hyper_1_2_chunk');

-- hypertable_compression_stats(hypertable)：获取与超表压缩相关的统计信息，以字节为单位返回
SELECT * FROM hypertable_compression_stats('conditions');

-- chunk_compression_stats(hypertable)：获取与超表压缩相关的区块统计信息，以字节为单位返回
SELECT * FROM chunk_compression_stats('conditions');

```

- 保留策略相关
```postgresql
-- add_retention_policy(hypertable or continuous_aggregate, drop_after)：创建数据保留策略，定期删除指定时间的超表或连续聚合数据，每个超表只能存在一个保留策略		
SELECT add_retention_policy('conditions', INTERVAL '6 months');

-- remove_retention_policy(hypertable or continuous_aggregate)：移除保留策略
SELECT remove_retention_policy('conditions');
```

- 自动化任务相关
```postgresql
-- add_job(proc, schedule_interval)：添加一个用户自定义的定时任务
CREATE OR REPLACE PROCEDURE user_defined_action(job_id int, config jsonb) LANGUAGE PLPGSQL AS
$$
BEGIN
RAISE NOTICE 'Executing action % with config %', job_id, config;
END
$$;

SELECT add_job('user_defined_action','1m');
-- alter_job(job_id)：修改用户自定义的定时任务，需要通过任务的 id 进行修改，任务的数据记录在视图 timescaledb_information.jobs 中
SELECT alter_job(1000, schedule_interval => INTERVAL '2 days');

-- delete_job(job_id)：删除一个用户自定义的任务
SELECT delete_job(1000);

-- run_job(job_id)：在当前会话中运行以前注册过的任务，run_job 通过存储过程实现，故不能在 select 内执行，必须用 call
SET client_min_messages TO DEBUG1;
CALL run_job(1000);

```

- 超函数相关
```postgresql
-- approximate_row_count(Hypertable or regular PostgreSQL table)：获取超表、分布式超表或普通ps数据表的近似行数
SELECT * FROM approximate_row_count('stocks_real_time');

-- first(value, time)：根据指定的时间列获得聚合中最早的值
SELECT device_id, first(temp, time)
FROM metrics
GROUP BY device_id;

-- last(value, time)：根据指定的之间列获得聚合中最后一个值
SELECT device_id, time_bucket('5 minutes', time) AS interval,
last(temp, time)
FROM metrics
WHERE time > now () - INTERVAL '1 day'
GROUP BY device_id, interval
ORDER BY interval DESC;

-- histogram(value, min, max, nbuckets)：获取一组被聚合的数据的直方图？
SELECT device_id, histogram(battery_level, 20, 60, 5)
FROM readings
GROUP BY device_id
LIMIT 10;

-- time_bucket(bucket_width, ts)：获取任意时间间隔内的数据
SELECT time_bucket('5 minutes', time) AS five_min, avg(cpu)
FROM metrics
GROUP BY five_min
ORDER BY five_min DESC LIMIT 10;
```

- 信息视图相关
```postgresql
-- timescaledb_information.chunks：获取超表数据块的元数据
SELECT * FROM timescaledb_information.chunks;

-- timescaledb_information.continuous_aggregates：获取连续聚合的元数据以及设置信息
SELECT * FROM timescaledb_information.continuous_aggregates;

-- timescaledb_information.compression_settings：获取超表压缩相关的设置信息
SELECT * FROM timescaledb_information.compression_settings;

-- timescaledb_information.data_nodes：获取有关数据节点的信息
SELECT * FROM timescaledb_information.data_nodes;

-- timescaledb_information.dimensions：获取有关超表维度的元数据，将超表的每一个维度返回一行元数据
SELECT * FROM timescaledb_information.dimensions;

-- timescaledb_information.hypertables：获取超表相关的元数据
SELECT * FROM timescaledb_information.hypertables;

-- timescaledb_information.jobs：显示自动化框架中注册的所有任务信息
SELECT * FROM timescaledb_information.jobs;

-- timescaledb_information.job_stats：显示自动化框架中所有运行任务的作业信息和统计信息
SELECT * FROM timescaledb_information.job_stats;

-- timescaledb_experimental.policies：显示连续聚合上设置的所有策略信息
SELECT * FROM timescaledb_experimental.policies;
```