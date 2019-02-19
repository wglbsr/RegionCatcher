package com.wgl.tool;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private static String REGION_ID_REG_EXP = "\\d{2,12}(<=.html)";//正则表达式,后发零宽断言

    public static void main(String[] args) {
        DataCatcher dataCatcher = new DataCatcher();
        dataCatcher.start();
    }

    public void start() {
        JSONObject jsonObject = new JSONObject();
        Document document = getHtmlContent(START_URL);
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

    /**
     * @return org.jsoup.select.Elements
     * @Author wanggl(lane)
     * @Description //TODO 获取省份元素
     * @Date 16:23 2019-02-19
     * @Param [document]
     **/
    private Elements getProvincesElement(Document document) {
        Elements provinceLines = document.select("provincetr");
        Elements elements = new Elements();
        for (Element provinceLine : provinceLines) {
            Elements provinces = provinceLine.select("td.a");
            elements.addAll(provinces);
        }
        return elements;
    }


    private List<String> getUrls(Elements elements) {
        List<String> urls = new ArrayList<String>();
        for (Element element : elements) {
            String url = element.attr("href");
            String regionId = findStrByRegEx(url, REGION_ID_REG_EXP);
        }
        return urls;
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
