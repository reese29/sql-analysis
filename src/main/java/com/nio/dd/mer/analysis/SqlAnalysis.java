package com.nio.dd.mer.analysis;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nio.dd.mer.kibana.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SqlAnalysis {
    private String path;
    private Json2Md json2Md;

    public SqlAnalysis(String path) {
        this.path = path;
        json2Md = new Json2Md();
    }

    public void runAnalysis() throws IOException {
        String soarOutPut = readToString(new File(path));
        JSONArray jsonArray = JSONArray.parseArray(soarOutPut);
        handleExplainInfo(jsonArray);
        json2Md.exec(jsonArray);
    }

    private String readToString(File file) {
        Long fileLength = file.length();
        byte[] fileContent = new byte[fileLength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(fileContent);
    }

    public JSONArray handleExplainInfo(JSONArray jsonArray) {
        for(int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONArray explainInfos = jsonObject.getJSONArray("Explain");
            JSONObject explainInfo = explainInfos.getJSONObject(0);
            String content = (String) explainInfo.get("Content");
            String[] explain = content.split("\\n");
            boolean allIndex = true;
            // 前两行为列名与分隔符
            for (int j = 2; j < explain.length; j++) {
                String[] params = explain[j].split("\\|");
                // params[0]为""
                String type = params[5].trim();
                if (Configuration.INDEX.equals(type) || Configuration.ALL.equals(type)) {
                    allIndex = false;
                }
            }
            if(!allIndex) {
                explainInfo.put("IndexInfo","查询未全部走索引，需要修改");
            } else {
                explainInfo.put("IndexInfo",null);
            }
        }
        return jsonArray;
    }

    public static void main(String[] args) throws IOException {
        String txt = "[\n" +
                " {\n" +
                "  \"ID\": \"8630AE0802757285\",\n" +
                "  \"Fingerprint\": \"select * from fulfillment_info where status = ? and retry \\u003c= ? and next_time \\u003c= now() and id \\u003e ? limit ?\",\n" +
                "  \"Score\": 65,\n" +
                "  \"Sample\": \"select * from fulfillment_info where status = 0 and retry \\u003c= 16 and next_time \\u003c= now() and id \\u003e 0 limit 20\",\n" +
                "  \"Explain\": [\n" +
                "    {\n" +
                "      \"Item\": \"EXP.000\",\n" +
                "      \"Severity\": \"L0\",\n" +
                "      \"Summary\": \"Explain信息\",\n" +
                "      \"Content\": \"| id | select\\\\_type | table | partitions | type | possible_keys | key | key\\\\_len | ref | rows | filtered | scalability | Extra |\\n|---|---|---|---|---|---|---|---|---|---|---|---|---|\\n| 1  | SIMPLE | *fulfillment\\\\_info* | NULL | range | PRIMARY,\\u003cbr\\u003eidx\\\\_nexttime\\\\_status\\\\_retry | PRIMARY | 8 | NULL | 300 | 0.00% | ☠️ **O(log n)+** | Using where |\\n\\n\",\n" +
                "      \"Case\": \"### Explain信息解读\\n\\n#### SelectType信息解读\\n\\n* **SIMPLE**: 简单SELECT(不使用UNION或子查询等).\\n\\n#### Type信息解读\\n\\n* **range**: 只检索给定范围的行, 使用一个索引来选择行. key列显示使用了哪个索引. key_len包含所使用索引的最长关键元素.\\n\\n#### Extra信息解读\\n\\n* **Using where**: WHERE条件用于筛选出与下一个表匹配的数据然后返回给客户端. 除非故意做的全表扫描, 否则连接类型是ALL或者是index, 且在Extra列的值中没有Using Where, 则该查询可能是有问题的.\\n\",\n" +
                "      \"Position\": 0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"HeuristicRules\": [\n" +
                "    {\n" +
                "      \"Item\": \"COL.001\",\n" +
                "      \"Severity\": \"L1\",\n" +
                "      \"Summary\": \"不建议使用 SELECT * 类型查询\",\n" +
                "      \"Content\": \"当表结构变更时，使用 * 通配符选择所有列将导致查询的含义和行为会发生更改，可能导致查询返回更多的数据。\",\n" +
                "      \"Case\": \"select * from tbl where id=1\",\n" +
                "      \"Position\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"Item\": \"FUN.001\",\n" +
                "      \"Severity\": \"L2\",\n" +
                "      \"Summary\": \"避免在 WHERE 条件中使用函数或其他运算符\",\n" +
                "      \"Content\": \"虽然在 SQL 中使用函数可以简化很多复杂的查询，但使用了函数的查询无法利用表中已经建立的索引，该查询将会是全表扫描，性能较差。通常建议将列名写在比较运算符左侧，将查询过滤条件放在比较运算符右侧。也不建议在查询比较条件两侧书写多余的括号，这会对阅读产生比较大的困扰。\",\n" +
                "      \"Case\": \"select id from t where substring(name,1,3)='abc'\",\n" +
                "      \"Position\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"Item\": \"RES.002\",\n" +
                "      \"Severity\": \"L4\",\n" +
                "      \"Summary\": \"未使用 ORDER BY 的 LIMIT 查询\",\n" +
                "      \"Content\": \"没有 ORDER BY 的 LIMIT 会导致非确定性的结果，这取决于查询执行计划。\",\n" +
                "      \"Case\": \"select col1,col2 from tbl where name=xx limit 10\",\n" +
                "      \"Position\": 0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"IndexRules\": null,\n" +
                "  \"Tables\": [\n" +
                "    \"`mer_order_sit`.`fulfillment_info`\"\n" +
                "  ]\n" +
                "},\n" +
                "{\n" +
                "  \"ID\": \"DC9CFEBFE4EF55AF\",\n" +
                "  \"Fingerprint\": \"select * from whole_order where whole_order_no = ? for update\",\n" +
                "  \"Score\": 95,\n" +
                "  \"Sample\": \"select * from whole_order where whole_order_no = '818512207127559602' for update\",\n" +
                "  \"Explain\": [\n" +
                "    {\n" +
                "      \"Item\": \"EXP.000\",\n" +
                "      \"Severity\": \"L0\",\n" +
                "      \"Summary\": \"Explain信息\",\n" +
                "      \"Content\": \"| id | select\\\\_type | table | partitions | type | possible_keys | key | key\\\\_len | ref | rows | filtered | scalability | Extra |\\n|---|---|---|---|---|---|---|---|---|---|---|---|---|\\n| 1  | SIMPLE | *whole\\\\_order* | NULL | const | whole\\\\_order\\\\_no\\\\_unique | whole\\\\_order\\\\_no\\\\_unique | 62 | const | 1 | 0.00% | O(1) |  |\\n\\n\",\n" +
                "      \"Case\": \"### Explain信息解读\\n\\n#### SelectType信息解读\\n\\n* **SIMPLE**: 简单SELECT(不使用UNION或子查询等).\\n\\n#### Type信息解读\\n\\n* **const**: const用于使用常数值比较PRIMARY KEY时, 当查询的表仅有一行时, 使用system. 例:SELECT * FROM tbl WHERE col = 1.\\n\",\n" +
                "      \"Position\": 0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"HeuristicRules\": [\n" +
                "    {\n" +
                "      \"Item\": \"COL.001\",\n" +
                "      \"Severity\": \"L1\",\n" +
                "      \"Summary\": \"不建议使用 SELECT * 类型查询\",\n" +
                "      \"Content\": \"当表结构变更时，使用 * 通配符选择所有列将导致查询的含义和行为会发生更改，可能导致查询返回更多的数据。\",\n" +
                "      \"Case\": \"select * from tbl where id=1\",\n" +
                "      \"Position\": 0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"IndexRules\": null,\n" +
                "  \"Tables\": [\n" +
                "    \"`mer_order_sit`.`whole_order`\"\n" +
                "  ]\n" +
                "},\n" +
                "{\n" +
                "  \"ID\": \"44F7A6DB0B0712AD\",\n" +
                "  \"Fingerprint\": \"select id,refund_no,storage_status,is_virtual_refund,warehouse_name,warehouse_address,warehouse_phone,warehouse_contact,send_back,express_company,logistics_no,remark,is_delete,create_user,create_time,update_user,update_time,is_inform_write_logistics,warehouse_code from refund_storage where refund_no = ? and is_delete = ?\",\n" +
                "  \"Score\": 100,\n" +
                "  \"Sample\": \"SELECT id,refund_no,storage_status,is_virtual_refund,warehouse_name,warehouse_address,warehouse_phone,warehouse_contact,send_back,express_company,logistics_no,remark,is_delete,create_user,create_time,update_user,update_time,is_inform_write_logistics,warehouse_code FROM refund_storage WHERE refund_no = '861342302073613545' AND is_delete = 0\",\n" +
                "  \"Explain\": [\n" +
                "    {\n" +
                "      \"Item\": \"EXP.000\",\n" +
                "      \"Severity\": \"L0\",\n" +
                "      \"Summary\": \"Explain信息\",\n" +
                "      \"Content\": \"| id | select\\\\_type | table | partitions | type | possible_keys | key | key\\\\_len | ref | rows | filtered | scalability | Extra |\\n|---|---|---|---|---|---|---|---|---|---|---|---|---|\\n| 1  | SIMPLE | *refund\\\\_storage* | NULL | const | refund\\\\_no\\\\_unique\\\\_index | refund\\\\_no\\\\_unique\\\\_index | 62 | const | 1 | 0.00% | O(1) |  |\\n\\n\",\n" +
                "      \"Case\": \"### Explain信息解读\\n\\n#### SelectType信息解读\\n\\n* **SIMPLE**: 简单SELECT(不使用UNION或子查询等).\\n\\n#### Type信息解读\\n\\n* **const**: const用于使用常数值比较PRIMARY KEY时, 当查询的表仅有一行时, 使用system. 例:SELECT * FROM tbl WHERE col = 1.\\n\",\n" +
                "      \"Position\": 0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"HeuristicRules\": null,\n" +
                "  \"IndexRules\": null,\n" +
                "  \"Tables\": [\n" +
                "    \"`mer_order_sit`.`refund_storage`\"\n" +
                "  ]\n" +
                "},\n" +
                "{\n" +
                "  \"ID\": \"0E08114E894FAB44\",\n" +
                "  \"Fingerprint\": \"select id, refund_no, pickup_order_no, order_status, cancel_operator, pickup_province, pickup_city, pickup_region, pickup_address, pickup_address_id, pickup_name, pickup_tel, pickup_start_time, pickup_end_time, courier_name, courier_tel, pickup_code, customer_code from jd_pickup_order where order_status =? order by id desc limit ?,?\",\n" +
                "  \"Score\": 100,\n" +
                "  \"Sample\": \"select id, refund_no, pickup_order_no, order_status, cancel_operator, pickup_province, pickup_city, pickup_region, pickup_address, pickup_address_id, pickup_name, pickup_tel, pickup_start_time, pickup_end_time, courier_name, courier_tel, pickup_code, customer_code from jd_pickup_order where order_status =2 order by id desc limit 0,200\",\n" +
                "  \"Explain\": [\n" +
                "    {\n" +
                "      \"Item\": \"EXP.000\",\n" +
                "      \"Severity\": \"L0\",\n" +
                "      \"Summary\": \"Explain信息\",\n" +
                "      \"Content\": \"| id | select\\\\_type | table | partitions | type | possible_keys | key | key\\\\_len | ref | rows | filtered | scalability | Extra |\\n|---|---|---|---|---|---|---|---|---|---|---|---|---|\\n| 1  | SIMPLE | *jd\\\\_pickup\\\\_order* | NULL | index | NULL | PRIMARY | 4 | NULL | 200 | 0.00% | ☠️ **O(n)** | Using where |\\n\\n\",\n" +
                "      \"Case\": \"### Explain信息解读\\n\\n#### SelectType信息解读\\n\\n* **SIMPLE**: 简单SELECT(不使用UNION或子查询等).\\n\\n#### Type信息解读\\n\\n* ☠️ **index**: 全表扫描, 只是扫描表的时候按照索引次序进行而不是行. 主要优点就是避免了排序, 但是开销仍然非常大.\\n\\n#### Extra信息解读\\n\\n* **Using where**: WHERE条件用于筛选出与下一个表匹配的数据然后返回给客户端. 除非故意做的全表扫描, 否则连接类型是ALL或者是index, 且在Extra列的值中没有Using Where, 则该查询可能是有问题的.\\n\",\n" +
                "      \"Position\": 0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"HeuristicRules\": null,\n" +
                "  \"IndexRules\": null,\n" +
                "  \"Tables\": [\n" +
                "    \"`mer_order_sit`.`jd_pickup_order`\"\n" +
                "  ]\n" +
                "} \n" +
                "]\n";
        JSONArray jsonArray = JSONArray.parseArray(txt);
        SqlAnalysis sqlAnalysis = new SqlAnalysis(Configuration.TXT_PATH);
        sqlAnalysis.handleExplainInfo(jsonArray);
        Json2Md json2Md = new Json2Md();
        json2Md.exec(jsonArray);
    }
}
