package com.wgl.tool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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
    private static String REGION_ID_REG_EXP = "(<=/)\\d{2,12}(<=.html)";//正则表达式,后发和先行零宽断言
    private static final int MAX_LEVEL = 5;
    private static String[] ELE_LIST = new String[MAX_LEVEL];

    public static void main(String[] args) {
        DataCatcher dataCatcher = new DataCatcher();
        dataCatcher.start();
    }

    public void start() {
        ELE_LIST[0] = "tr.provincetr td a";
        ELE_LIST[1] = "tr.citytr td a";
        ELE_LIST[2] = "tr.counrytr td a";
        ELE_LIST[3] = "tr.towntr td a";
        ELE_LIST[4] = "tr.villagetr td";

        getProvincesElement(START_URL);
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
    //provincetr,citytr,countytr,towntr,villagetr

    /**
     * @return org.jsoup.select.Elements
     * @Author wanggl(lane)
     * @Description //TODO 获取省份元素
     * @Date 16:23 2019-02-19
     * @Param [document]
     **/
    private JSONObject getProvincesElement(String url) {
        JSONObject jsonObject = new JSONObject();
        Document document = getHtmlContent(url);
        Elements provinceLines = document.select("tr.provincetr td a");

        return jsonObject;
    }

    private static final String KEY_ID = "id";
    private static final String KEY_PARENT_ID = "parent_id";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_NAME = "name";
    private static final String KEY_CHILDREN = "children";

    private JSONArray get(String url, int level, JSONObject regionJsonObj) {
        Document document = getHtmlContent(url);
        String parentId = findStrByRegEx(url, REGION_ID_REG_EXP);
        Elements elements = document.select(ELE_LIST[level]);
        for (Element element : elements) {
            JSONObject jsonObject = new JSONObject();
            String name = element.text();
            jsonObject.put(KEY_PARENT_ID, parentId);
            jsonObject.put(KEY_NAME, name);
            jsonObject.put(KEY_LEVEL, level);

            String nextUrl = element.attr("href");
        }


        return null;
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
