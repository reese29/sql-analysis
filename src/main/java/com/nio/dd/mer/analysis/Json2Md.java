package com.nio.dd.mer.analysis;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Json2Md {
    public static final String h1 = "# ";
    public static final String h2 = "## ";
    public static final String h3 = "### ";
    public static final String star = "* ";


    public void exec(JSONArray jsonArray) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter("result/show.md"));
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject module = jsonArray.getJSONObject(i);
            // sql代码块
            String rawSql = module.getString("Sample");
            String execSql = SQLUtils.formatMySql(rawSql);
            out.write(h1 + "执行Sql语句\n");
            out.write("```sql\n" + execSql + "\n```\n");
            JSONArray explainInfoArray = module.getJSONArray("Explain");
            JSONObject explainInfo = explainInfoArray.getJSONObject(0);
            // Explain信息
            out.write(h1 + explainInfo.getString("Summary") + "\n");

            out.write(explainInfo.getString("Content"));
            if(explainInfo.get("IndexInfo") != null) {
                out.write(h3 + "索引问题\n");
                out.write(explainInfo.getString("IndexInfo") + "\n");
            }
            out.write(explainInfo.getString("Case") + "\n");
            if(module.getJSONArray("HeuristicRules") == null) {
                continue;
            }
            JSONArray heuristicRules = module.getJSONArray("HeuristicRules");
            for (int j = 0; j < heuristicRules.size(); j++) {
                JSONObject rule = heuristicRules.getJSONObject(j);
                out.write(h3 + rule.getString("Summary") + "\n");
                out.write(star + rule.getString("Content") + "\n");
                out.write(star + "Case: " + rule.getString("Case") + "\n");
            }
        }
        out.close();
    }
}
