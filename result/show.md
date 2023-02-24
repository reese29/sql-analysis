# 执行Sql语句
```sql
SELECT *
FROM fulfillment_info
WHERE status = 0
	AND retry <= 16
	AND next_time <= now()
	AND id > 0
LIMIT 20
```
# Explain信息
| id | select\_type | table | partitions | type | possible_keys | key | key\_len | ref | rows | filtered | scalability | Extra |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 1  | SIMPLE | *fulfillment\_info* | NULL | range | PRIMARY,<br>idx\_nexttime\_status\_retry | PRIMARY | 8 | NULL | 300 | 0.00% | ☠️ **O(log n)+** | Using where |

### Explain信息解读

#### SelectType信息解读

* **SIMPLE**: 简单SELECT(不使用UNION或子查询等).

#### Type信息解读

* **range**: 只检索给定范围的行, 使用一个索引来选择行. key列显示使用了哪个索引. key_len包含所使用索引的最长关键元素.

#### Extra信息解读

* **Using where**: WHERE条件用于筛选出与下一个表匹配的数据然后返回给客户端. 除非故意做的全表扫描, 否则连接类型是ALL或者是index, 且在Extra列的值中没有Using Where, 则该查询可能是有问题的.

### 不建议使用 SELECT * 类型查询
* 当表结构变更时，使用 * 通配符选择所有列将导致查询的含义和行为会发生更改，可能导致查询返回更多的数据。
* Case: select * from tbl where id=1
### 避免在 WHERE 条件中使用函数或其他运算符
* 虽然在 SQL 中使用函数可以简化很多复杂的查询，但使用了函数的查询无法利用表中已经建立的索引，该查询将会是全表扫描，性能较差。通常建议将列名写在比较运算符左侧，将查询过滤条件放在比较运算符右侧。也不建议在查询比较条件两侧书写多余的括号，这会对阅读产生比较大的困扰。
* Case: select id from t where substring(name,1,3)='abc'
### 未使用 ORDER BY 的 LIMIT 查询
* 没有 ORDER BY 的 LIMIT 会导致非确定性的结果，这取决于查询执行计划。
* Case: select col1,col2 from tbl where name=xx limit 10
# 执行Sql语句
```sql
SELECT *
FROM whole_order
WHERE whole_order_no = '818512207127559602'
FOR UPDATE
```
# Explain信息
| id | select\_type | table | partitions | type | possible_keys | key | key\_len | ref | rows | filtered | scalability | Extra |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 1  | SIMPLE | *whole\_order* | NULL | const | whole\_order\_no\_unique | whole\_order\_no\_unique | 62 | const | 1 | 0.00% | O(1) |  |

### Explain信息解读

#### SelectType信息解读

* **SIMPLE**: 简单SELECT(不使用UNION或子查询等).

#### Type信息解读

* **const**: const用于使用常数值比较PRIMARY KEY时, 当查询的表仅有一行时, 使用system. 例:SELECT * FROM tbl WHERE col = 1.

### 不建议使用 SELECT * 类型查询
* 当表结构变更时，使用 * 通配符选择所有列将导致查询的含义和行为会发生更改，可能导致查询返回更多的数据。
* Case: select * from tbl where id=1
# 执行Sql语句
```sql
SELECT id, refund_no, storage_status, is_virtual_refund, warehouse_name
	, warehouse_address, warehouse_phone, warehouse_contact, send_back, express_company
	, logistics_no, remark, is_delete, create_user, create_time
	, update_user, update_time, is_inform_write_logistics, warehouse_code
FROM refund_storage
WHERE refund_no = '861342302073613545'
	AND is_delete = 0
```
# Explain信息
| id | select\_type | table | partitions | type | possible_keys | key | key\_len | ref | rows | filtered | scalability | Extra |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 1  | SIMPLE | *refund\_storage* | NULL | const | refund\_no\_unique\_index | refund\_no\_unique\_index | 62 | const | 1 | 0.00% | O(1) |  |

### Explain信息解读

#### SelectType信息解读

* **SIMPLE**: 简单SELECT(不使用UNION或子查询等).

#### Type信息解读

* **const**: const用于使用常数值比较PRIMARY KEY时, 当查询的表仅有一行时, 使用system. 例:SELECT * FROM tbl WHERE col = 1.

# 执行Sql语句
```sql
SELECT id, refund_no, pickup_order_no, order_status, cancel_operator
	, pickup_province, pickup_city, pickup_region, pickup_address, pickup_address_id
	, pickup_name, pickup_tel, pickup_start_time, pickup_end_time, courier_name
	, courier_tel, pickup_code, customer_code
FROM jd_pickup_order
WHERE order_status = 2
ORDER BY id DESC
LIMIT 0, 200
```
# Explain信息
| id | select\_type | table | partitions | type | possible_keys | key | key\_len | ref | rows | filtered | scalability | Extra |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 1  | SIMPLE | *jd\_pickup\_order* | NULL | index | NULL | PRIMARY | 4 | NULL | 200 | 0.00% | ☠️ **O(n)** | Using where |

### 索引问题
查询未全部走索引，需要修改
### Explain信息解读

#### SelectType信息解读

* **SIMPLE**: 简单SELECT(不使用UNION或子查询等).

#### Type信息解读

* ☠️ **index**: 全表扫描, 只是扫描表的时候按照索引次序进行而不是行. 主要优点就是避免了排序, 但是开销仍然非常大.

#### Extra信息解读

* **Using where**: WHERE条件用于筛选出与下一个表匹配的数据然后返回给客户端. 除非故意做的全表扫描, 否则连接类型是ALL或者是index, 且在Extra列的值中没有Using Where, 则该查询可能是有问题的.

