package com.wgl.tool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: lane
 * @Date: 2019-02-19 16:00
 * @Description:
 * @Version 1.0.0
 */
public class DataCatcher {
    private static final String BASE_URL = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/";
    private static final String START_URL = "index.html";
    private static String REGION_ID_REG_EXP_FIRST = "^\\d{2,12}(?=\\.html)";//后发零宽断言,抓取形如"*1101.html"里面的1101

    private static final int MAX_LEVEL = 1;
    private static String[] ELE_LIST = new String[MAX_LEVEL + 1];

    public static void main(String[] args) {
        DataCatcher dataCatcher = new DataCatcher();
        dataCatcher.start();
    }

    public void start() {
        ELE_LIST[0] = "tr.provincetr td a";
        ELE_LIST[1] = "tr.citytr td a";
//        ELE_LIST[2] = "tr.counrytr td a";
//        ELE_LIST[3] = "tr.towntr td a";
//        ELE_LIST[4] = "tr.villagetr td";
        JSONObject jsonObject = new JSONObject();
        get(START_URL, 0, jsonObject);
        System.out.println(jsonObject.toJSONString());
    }


    private Document getHtmlContent(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(BASE_URL + url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    private String filter(int level, Element element, String key) {
        if (level == 0) {

        }
        return null;
    }

    private static final String KEY_ID = "id";
    private static final String KEY_PARENT_ID = "parent_id";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_NAME = "name";
    private static final String KEY_CHILDREN = "children";

    private void get(String url, int level, JSONObject regionJsonObj) {
        Document document = getHtmlContent(url);
        String parentId = findStrByRegEx(url, REGION_ID_REG_EXP_FIRST);
        Elements elements = document.select(ELE_LIST[level]);
        JSONArray jsonArray = new JSONArray();
        int index = 0;
        int divisor = level > 0 ? (level == MAX_LEVEL ? 3 : 2) : 1;
        for (Element element : elements) {
            //区分不同级别元素的位置
            //区分不同级别元素的位置
            //区分不同级别元素的位置
            //level      id       parentId        name
            //省        0 href      固定          0 text
            //市        0 text    parent_url     1 text
            //区        0 text    parent_url     1 text
            //镇、街道   0 text   parent_url     1 text
            //村委       0 text   parent_url     2 text

            int thisLevel = level;
            JSONObject jsonObject = new JSONObject();
            String id = element.text();
            jsonObject.put(KEY_PARENT_ID, parentId);//**


            String name = element.text();
            jsonObject.put(KEY_NAME, name);





            jsonObject.put(KEY_ID, id);



            jsonObject.put(KEY_LEVEL, thisLevel);
            System.out.println("level:" + thisLevel);
            if (thisLevel < MAX_LEVEL) {
                String nextUrl = element.attr("href");
                thisLevel++;
                get(nextUrl, thisLevel, jsonObject);
            }
            jsonArray.add(jsonObject);
            index++;
        }
        regionJsonObj.put(KEY_CHILDREN, jsonArray);
    }


    public static String findStrByRegEx(String content, String regEx) {
        if (content == null || content.equals("")) {
            return null;
        }
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(content);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

}
