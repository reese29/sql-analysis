[
 {
  "ID": "44F7A6DB0B0712AD",
  "Fingerprint": "select id,refund_no,storage_status,is_virtual_refund,warehouse_name,warehouse_address,warehouse_phone,warehouse_contact,send_back,express_company,logistics_no,remark,is_delete,create_user,create_time,update_user,update_time,is_inform_write_logistics,warehouse_code from refund_storage where refund_no = ? and is_delete = ?",
  "Score": 100,
  "Sample": "SELECT id,refund_no,storage_status,is_virtual_refund,warehouse_name,warehouse_address,warehouse_phone,warehouse_contact,send_back,express_company,logistics_no,remark,is_delete,create_user,create_time,update_user,update_time,is_inform_write_logistics,warehouse_code FROM refund_storage WHERE refund_no = '861342301179678831' AND is_delete = 0",
  "Explain": [
    {
      "Item": "EXP.000",
      "Severity": "L0",
      "Summary": "Explain信息",
      "Content": "| id | select\\_type | table | partitions | type | possible_keys | key | key\\_len | ref | rows | filtered | scalability | Extra |\n|---|---|---|---|---|---|---|---|---|---|---|---|---|\n| 1  | SIMPLE | *refund\\_storage* | NULL | const | refund\\_no\\_unique\\_index | refund\\_no\\_unique\\_index | 62 | const | 1 | 0.00% | O(1) |  |\n\n",
      "Case": "### Explain信息解读\n\n#### SelectType信息解读\n\n* **SIMPLE**: 简单SELECT(不使用UNION或子查询等).\n\n#### Type信息解读\n\n* **const**: const用于使用常数值比较PRIMARY KEY时, 当查询的表仅有一行时, 使用system. 例:SELECT * FROM tbl WHERE col = 1.\n",
      "Position": 0
    }
  ],
  "HeuristicRules": null,
  "IndexRules": null,
  "Tables": [
    "`mer_order_sit`.`refund_storage`"
  ]
},
{
  "ID": "0E08114E894FAB44",
  "Fingerprint": "select id, refund_no, pickup_order_no, order_status, cancel_operator, pickup_province, pickup_city, pickup_region, pickup_address, pickup_address_id, pickup_name, pickup_tel, pickup_start_time, pickup_end_time, courier_name, courier_tel, pickup_code, customer_code from jd_pickup_order where order_status =? order by id desc limit ?,?",
  "Score": 100,
  "Sample": "select id, refund_no, pickup_order_no, order_status, cancel_operator, pickup_province, pickup_city, pickup_region, pickup_address, pickup_address_id, pickup_name, pickup_tel, pickup_start_time, pickup_end_time, courier_name, courier_tel, pickup_code, customer_code from jd_pickup_order where order_status =2 order by id desc limit 0,200",
  "Explain": [
    {
      "Item": "EXP.000",
      "Severity": "L0",
      "Summary": "Explain信息",
      "Content": "| id | select\\_type | table | partitions | type | possible_keys | key | key\\_len | ref | rows | filtered | scalability | Extra |\n|---|---|---|---|---|---|---|---|---|---|---|---|---|\n| 1  | SIMPLE | *jd\\_pickup\\_order* | NULL | index | NULL | PRIMARY | 4 | NULL | 200 | 0.00% | ☠️ **O(n)** | Using where |\n\n",
      "Case": "### Explain信息解读\n\n#### SelectType信息解读\n\n* **SIMPLE**: 简单SELECT(不使用UNION或子查询等).\n\n#### Type信息解读\n\n* **index**: 全表扫描, 只是扫描表的时候按照索引次序进行而不是行. 主要优点就是避免了排序, 但是开销仍然非常大.\n\n#### Extra信息解读\n\n* **Using where**: WHERE条件用于筛选出与下一个表匹配的数据然后返回给客户端. 除非故意做的全表扫描, 否则连接类型是ALL或者是index, 且在Extra列的值中没有Using Where, 则该查询可能是有问题的.\n",
      "Position": 0
    }
  ],
  "HeuristicRules": null,
  "IndexRules": null,
  "Tables": [
    "`mer_order_sit`.`jd_pickup_order`"
  ]
},
{
  "ID": "5D4F6B7722085D18",
  "Fingerprint": "select id, account_id, cancel_no, refund_no, out_order_no, out_biz_no, refund_payment_no, refund_source, refund_point, refund_point_result, refund_cash, refund_cash_result, status, auto_refund_times, refund_type, is_delete, create_user, create_time, update_user, update_time, biz_type from refund_money_record where status in(?+) and auto_refund_times \u003c? order by id desc limit ?,?",
  "Score": 100,
  "Sample": "select id, account_id, cancel_no, refund_no, out_order_no, out_biz_no, refund_payment_no, refund_source, refund_point, refund_point_result, refund_cash, refund_cash_result, status, auto_refund_times, refund_type, is_delete, create_user, create_time, update_user, update_time, biz_type from refund_money_record where status in ( 11 , 21 , 40 , -1 ) and auto_refund_times \u003c5 order by id desc limit 0,50",
  "Explain": [
    {
      "Item": "EXP.000",
      "Severity": "L0",
      "Summary": "Explain信息",
      "Content": "| id | select\\_type | table | partitions | type | possible_keys | key | key\\_len | ref | rows | filtered | scalability | Extra |\n|---|---|---|---|---|---|---|---|---|---|---|---|---|\n| 1  | SIMPLE | *refund\\_money\\_record* | NULL | index | NULL | PRIMARY | 8 | NULL | 50 | 0.00% | ☠️ **O(n)** | Using where |\n\n",
      "Case": "### Explain信息解读\n\n#### SelectType信息解读\n\n* **SIMPLE**: 简单SELECT(不使用UNION或子查询等).\n\n#### Type信息解读\n\n* **index**: 全表扫描, 只是扫描表的时候按照索引次序进行而不是行. 主要优点就是避免了排序, 但是开销仍然非常大.\n\n#### Extra信息解读\n\n* **Using where**: WHERE条件用于筛选出与下一个表匹配的数据然后返回给客户端. 除非故意做的全表扫描, 否则连接类型是ALL或者是index, 且在Extra列的值中没有Using Where, 则该查询可能是有问题的.\n",
      "Position": 0
    }
  ],
  "HeuristicRules": null,
  "IndexRules": null,
  "Tables": [
    "`mer_order_sit`.`refund_money_record`"
  ]
} 
]
