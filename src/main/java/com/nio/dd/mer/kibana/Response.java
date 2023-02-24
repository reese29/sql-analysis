package com.nio.dd.mer.kibana;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.nio.charset.Charset;

public class Response {
    private final HttpClientBuilder builder = HttpClientBuilder.create();

    private int maxRetryTimes = 10;

    public void exec(HttpPost post) throws Exception {
        CloseableHttpClient client = builder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        String res;
        String id;
        int retryTimes = 0;
        boolean flag = false;
        try {
            do {
                HttpResponse resp = client.execute(post);
                InputStream respIs = resp.getEntity().getContent();
                byte[] respBytes = IOUtils.toByteArray(respIs);
                res = new String(respBytes, Charset.forName(Configuration.CHARSET));

                id = getId(res);
                if (id != null) {
                    flag = true;
                }
                retryTimes++;
            } while (retryTimes < maxRetryTimes && !flag);

            Request request = new Request(Configuration.TXT_PATH);
            HttpPost newPost = request.reCreateRequest(id);
            HttpResponse newResp = client.execute(newPost);
            InputStream newRespIs = newResp.getEntity().getContent();
            byte[] newRespBytes = IOUtils.toByteArray(newRespIs);
            res = new String(newRespBytes, Charset.forName(Configuration.CHARSET));
            getHits(res);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getHits(String res) throws IOException {
        Configuration configuration = new Configuration();
        try {
            JSONObject resJSON = JSONObject.parseObject(res);
            JSONObject result = resJSON.getJSONObject("result");
            JSONObject rawResponse = result.getJSONObject("rawResponse");
            JSONObject hit = rawResponse.getJSONObject("hits");
            JSONArray hits = hit.getJSONArray("hits");

            BufferedWriter writer = new BufferedWriter(new FileWriter(Configuration.FILE_PATH + configuration.FILE_NAME + Configuration.FILE_TYPE));
            for(Object curJSON : hits) {
                JSONObject fields = ((JSONObject) curJSON).getJSONObject("fields");
                JSONArray info = (JSONArray) fields.get("logInfo");
                String sqlExpr = info.getString(0);
                JSONObject sqlInfo = JSONObject.parseObject(sqlExpr.split(" : ")[1]);
                String sql = sqlInfo.getString("sql");
                writer.write(sql + "; \n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(configuration.FILE_NAME + Configuration.SHELL_TYPE));
        String exec = Configuration.FILE_PATH + "soar -query=" + Configuration.FILE_PATH + configuration.FILE_NAME + Configuration.FILE_TYPE
                + " -report-type=json >> " + Configuration.FILE_PATH + configuration.FILE_NAME + Configuration.MARK_DOWN;
        out.write(exec);
        out.close();
        runCmd(configuration);
    }

    private String getId(String res) {
        try {
            JSONObject respJson = JSONObject.parseObject(res);
            JSONObject result = (JSONObject) respJson.get("result");
            return (String) result.get("id");
        } catch (Exception e) {
            return null;
        }
    }

    private void runCmd(Configuration configuration) {
        try {
            String command1 = "chmod 777 " + configuration.FILE_NAME + Configuration.SHELL_TYPE;
            String command2 = "./" + configuration.FILE_NAME + Configuration.SHELL_TYPE;
            String[] cmd1 = {"/bin/sh", "-c", command1};
            String[] cmd2 = {"/bin/sh", "-c", command2};
            Runtime.getRuntime().exec(cmd1);
            Runtime.getRuntime().exec(cmd2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
