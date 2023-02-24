package com.nio.dd.mer.kibana;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

public class Request {
    private TxtReader txtReader = new TxtReader();
    private HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

    private String cookie;
    private String startTime;
    private String endTime;
    private String index;
    private String url;
    private int size;
    private String kbnVersion;
    private String env;

    private String beforeSize;
    private String beforeSizeWithId;
    private String beforeStartTime;
    private String afterEndTime;

    public Request(String path) throws Exception {
        Map<String, Object> commonParamsAndModuleMaps = txtReader.readTxt(path);

        cookie = (String) commonParamsAndModuleMaps.get(Configuration.COOKIE);
        url = (String) commonParamsAndModuleMaps.get(Configuration.URL);
        startTime = commonParamsAndModuleMaps.get(Configuration.START_TIME) + Configuration.SUFFIX;
        endTime = commonParamsAndModuleMaps.get(Configuration.END_TIME) + Configuration.SUFFIX;
        kbnVersion = (String) commonParamsAndModuleMaps.get(Configuration.KBN_VERSION);
        index = (String) commonParamsAndModuleMaps.get(Configuration.INDEX);
        String sizeString = (String) commonParamsAndModuleMaps.get(Configuration.SIZE);
        size = Integer.parseInt(sizeString);
        env = (String) commonParamsAndModuleMaps.get(Configuration.ENV);

        if ((Configuration.PROD).equals(env)) {
            beforeSize = Configuration.BEFORE_SIZE;
            beforeSizeWithId = Configuration.BEFORE_SIZE_WITH_ID;
            beforeStartTime = Configuration.BEFORE_START_TIME;
            afterEndTime = Configuration.AFTER_END_TIME;
        }
        else {
            beforeSize = Configuration.BEFORE_SIZE_TEST;
            beforeSizeWithId = Configuration.BEFORE_SIZE_TEST;
            beforeStartTime = Configuration.BEFORE_START_TIME_TEST;
            afterEndTime = Configuration.AFTER_END_TIME_TEST;
        }
    }

    public HttpPost buildParam() throws Exception {

        String curParam = Configuration.BEFORE_INDEX + index + beforeSize + size + beforeStartTime +
                timeChange(startTime) + Configuration.BEFORE_END_TIME + timeChange(endTime) + afterEndTime;

        HttpPost post = new HttpPost(url);
        post.setHeader("Cookie", cookie);
        post.setHeader("kbn-version", kbnVersion);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(curParam, ContentType.APPLICATION_JSON));

        return post;
    }

    //进行进制转换的
    public String timeChange(String inTime) throws Exception {
        //1、传入的时间的格式
        SimpleDateFormat dfIn = new SimpleDateFormat(Configuration.TIME_FORMAT_IN);
        //设置传入时间的时区
        dfIn.setTimeZone(TimeZone.getTimeZone(Configuration.TIME_ZONE));

        //2、传出的时间的格式
        SimpleDateFormat dfOut = new SimpleDateFormat(Configuration.TIME_FORMAT_OUT);
        try {
            //解析传出时间
            String outTIme = dfOut.format(dfIn.parse(inTime));

            return outTIme;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new Exception("输入日期有误，请按照格式输入");
    }

    public HttpPost reCreateRequest(String id) throws Exception {
        String withIdParam = Configuration.BEFORE_ID + id + Configuration.BEFORE_INDEX_WITH_ID + index + beforeSize + size + beforeStartTime
                + timeChange(startTime) + Configuration.BEFORE_END_TIME + timeChange(endTime) + afterEndTime;
        HttpPost post = new HttpPost(url);
        try {
            post.setHeader("Cookie", cookie);
            post.setHeader("kbn-version", kbnVersion);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(withIdParam, ContentType.APPLICATION_JSON));
        } catch (Exception e) {
            System.out.println("getRequest：输入有误，请检查参数");
            e.printStackTrace();
        }
        return post;
    }

}
