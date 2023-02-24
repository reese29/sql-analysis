package com.nio.dd.mer.kibana;


import lombok.Data;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
@Data
public class TxtReader {
    public Map<String, Object> readTxt(String path) throws Exception{
        Map<String, Object> commonParamsAndModuleMaps = new HashMap<>();

        //读取txt文本
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String s = null;
            //使用readLine方法，一次读一行
            while((s = br.readLine())!=null){
                //忽略换行和注释
                if (s.trim().length() <= 1 || s.startsWith(Configuration.IGNORE)) {continue;}

                if (s.startsWith(Configuration.ENV)) {
                    commonParamsAndModuleMaps.put(Configuration.ENV, s.substring(Configuration.ENV.length() + 1).trim());
                }
                if (s.startsWith(Configuration.COOKIE)) {
                    commonParamsAndModuleMaps.put(Configuration.COOKIE, s.substring(Configuration.COOKIE.length() + 1).trim());
                }
                if (s.startsWith(Configuration.KBN_VERSION)) {
                    commonParamsAndModuleMaps.put(Configuration.KBN_VERSION, s.substring(Configuration.KBN_VERSION.length() + 1).trim());
                }

                if (s.startsWith(Configuration.START_TIME)) {
                    commonParamsAndModuleMaps.put(Configuration.START_TIME, s.substring(Configuration.START_TIME.length() + 1).trim());
                }
                if (s.startsWith(Configuration.END_TIME)) {
                    commonParamsAndModuleMaps.put(Configuration.END_TIME, s.substring(Configuration.END_TIME.length() + 1).trim());
                }
                if (s.startsWith(Configuration.INDEX)) {
                    commonParamsAndModuleMaps.put(Configuration.INDEX, s.substring(Configuration.INDEX.length() + 1).trim());
                }
                if (s.startsWith(Configuration.MODULE)) {
                    commonParamsAndModuleMaps.put(Configuration.MODULE, s.substring(Configuration.MODULE.length() + 1).trim());
                }
                if (s.startsWith(Configuration.SIZE)) {
                    commonParamsAndModuleMaps.put(Configuration.SIZE, s.substring(Configuration.SIZE.length() + 1).trim());
                }
            }
            switch ((String) commonParamsAndModuleMaps.get(Configuration.ENV)) {
                case Configuration.PROD :
                    commonParamsAndModuleMaps.put(Configuration.URL, Configuration.URL_PROD);
                    break;
                case Configuration.SIT :
                    commonParamsAndModuleMaps.put(Configuration.URL, Configuration.URL_SIT);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return commonParamsAndModuleMaps;
    }
}

