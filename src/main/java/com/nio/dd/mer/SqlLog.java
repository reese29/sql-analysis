package com.nio.dd.mer;

import com.nio.dd.mer.analysis.SqlAnalysis;
import com.nio.dd.mer.kibana.Configuration;
import com.nio.dd.mer.kibana.Request;
import com.nio.dd.mer.kibana.Response;
import org.apache.http.client.methods.HttpPost;

public class SqlLog {
    public static void main(String[] args) throws Exception {
        Request request = new Request(Configuration.TXT_PATH);
        Response response = new Response();
        SqlAnalysis sqlAnalysis = new SqlAnalysis("result/order-middle.md");
        HttpPost post = request.buildParam();
        response.exec(post);
        sqlAnalysis.runAnalysis();
    }
}
