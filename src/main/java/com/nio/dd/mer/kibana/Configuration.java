package com.nio.dd.mer.kibana;

import java.util.Map;

public class Configuration {
    private TxtReader txtReader;

    public static final String URL_PROD = "http://kibana-qc.nioint.com:5601/s/ddlog/internal/bsearch";
    public static final String URL_SIT = "http://kibana-qc-test.nioint.com:5601/internal/bsearch";
    public static final String SIT = "sit";
    public static final String PROD = "prod";

    public static final String BEFORE_INDEX = "{\"batch\":[{\"request\":{\"params\":{\"index\":\"";
    public static final String BEFORE_SIZE = "\",\"body\":{\"sort\":[{\"@timestamp\":{\"order\":\"desc\",\"unmapped_type\":\"boolean\"}}],\"fields\":[{\"field\":\"*\",\"include_unmapped\":\"true\"},{\"field\":\"@timestamp\",\"format\":\"strict_date_optional_time\"},{\"field\":\"json.time\",\"format\":\"strict_date_optional_time\"}],\"size\":";
    public static final String BEFORE_START_TIME = ",\"version\":true,\"script_fields\":{},\"stored_fields\":[\"*\"],\"runtime_mappings\":{},\"_source\":false,\"query\":{\"bool\":{\"must\":[],\"filter\":[{\"multi_match\":{\"type\":\"phrase\",\"query\":\"\\\"type\\\":\\\"sql_test\\\"\",\"lenient\":true}},{\"range\":{\"json.time\":{\"format\":\"strict_date_optional_time\",\"gte\":\"";
    public static final String BEFORE_END_TIME = "\",\"lte\":\"";
    public static final String AFTER_END_TIME = "\"}}}],\"should\":[],\"must_not\":[]}},\"highlight\":{\"pre_tags\":[\"@kibana-highlighted-field@\"],\"post_tags\":[\"@/kibana-highlighted-field@\"],\"fields\":{\"*\":{}},\"fragment_size\":2147483647}},\"track_total_hits\":false,\"preference\":1675649725293}},\"options\":{\"sessionId\":\"\",\"isRestore\":false,\"strategy\":\"ese\",\"isStored\":false,\"executionContext\":{\"type\":\"application\",\"name\":\"discover\",\"description\":\"fetch documents\",\"url\":\"/s/ddlog/app/discover\",\"id\":\"\"}}}]}";

    public static final String BEFORE_ID = "{\"batch\":[{\"request\":{\"id\":\"";
    public static final String BEFORE_INDEX_WITH_ID = "\",\"params\":{\"index\":\"";
    public static final String BEFORE_SIZE_WITH_ID = "\",\"body\":{\"sort\":[{\"@timestamp\":{\"order\":\"desc\",\"unmapped_type\":\"boolean\"}}],\"fields\":[{\"field\":\"*\",\"include_unmapped\":\"true\"},{\"field\":\"@timestamp\",\"format\":\"strict_date_optional_time\"},{\"field\":\"json.time\",\"format\":\"strict_date_optional_time\"}],\"size\":";

    public static final String AFTER_END_TIME_TEST = "\",\"format\":\"strict_date_optional_time\"}}}],\"should\":[],\"must_not\":[]}},\"highlight\":{\"pre_tags\":[\"@kibana-highlighted-field@\"],\"post_tags\":[\"@/kibana-highlighted-field@\"],\"fields\":{\"*\":{}},\"fragment_size\":2147483647}},\"preference\":1675665242612}},\"options\":{\"strategy\":\"ese\",\"sessionId\":\"\",\"isRestore\":false,\"isStored\":false}}]}";
    public static final String BEFORE_START_TIME_TEST = ",\"sort\":[{\"@timestamp\":{\"order\":\"desc\",\"unmapped_type\":\"boolean\"}}],\"version\":true,\"fields\":[{\"field\":\"*\",\"include_unmapped\":\"true\"},{\"field\":\"@timestamp\",\"format\":\"strict_date_optional_time\"},{\"field\":\"json.time\",\"format\":\"strict_date_optional_time\"}],\"script_fields\":{},\"stored_fields\":[\"*\"],\"runtime_mappings\":{},\"_source\":false,\"query\":{\"bool\":{\"must\":[],\"filter\":[{\"multi_match\":{\"type\":\"phrase\",\"query\":\"\\\"type\\\":\\\"sql_test\\\"\",\"lenient\":true}},{\"range\":{\"json.time\":{\"gte\":\"";
    public static final String BEFORE_SIZE_TEST = "\",\"body\":{\"size\":";

    //下面是从txt读取的常量字段
    public static final String ENV = "env";
    public static final String URL = "url";
    public static final String COOKIE = "cookie";
    public static final String MODULE = "module";
    public static final String INDEX = "index";
    public static final String START_TIME= "startTime";
    public static final String END_TIME= "endTime";
    public static final String IGNORE= "#";
    public static final String CHARSET =  "UTF-8";
    public static final String SIZE = "size";

    //下面是请求和响应中的常量字段
    public static final String KBN_VERSION = "kbn-version";


    //下面是时间相关的常量字段
    public static final String TIME_FORMAT_IN = "yyyy-MM-dd'-'HH:mm:ss.SSS'Z'";
    public static final String TIME_FORMAT_OUT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String SUFFIX = ":00.000Z";
    public static final String TIME_ZONE = "GMT+16";

    //下面是和文件路径相关的
    public static final String FILE_TYPE = ".sql";
    public String FILE_NAME;
    public static final String FILE_PATH = "result/";
    public static final String TXT_PATH = "param.txt";
    public static final String HTML_TYPE = ".html";
    public static final String SHELL_TYPE = ".sh";
    public static final String MARK_DOWN = ".md";

    public static final String ALL = "ALL";

    public Configuration() {
        this.txtReader = new TxtReader();
        Map<String, Object> commonParamsAndModuleMaps = null;
        try {
            commonParamsAndModuleMaps = txtReader.readTxt(Configuration.TXT_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.FILE_NAME = (String) commonParamsAndModuleMaps.get("module");
    }
}

