package com.foundation.crawler.headlesschrom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 无头浏览器模拟点击
 *
 * @author : jacksonz
 * @date : 2022/6/26 11:40
 */
public class HWebCrawler {

    public static void craw(String base) {
        // 加载chrom浏览驱动
        System.setProperty("webdriver.chrome.driver", "D:/微信/WeChat/chromedriver.exe");
        // 挂代理

//        String base = "http://3527c.com";

        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        options.addArguments("no-sandbox");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        WebDriver driver = new ChromeDriver(options);

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();


        Map<String, String> idMap = new HashMap<>(16);
        Map<String, String> m3u8Map = new HashMap<>(16);

        int index = 1;
        int maxPageSize = 3;

        // 爬取列表页数据
        while (index < maxPageSize) {
            driver.get(base + "/type/1" + "/" + index);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            List<WebElement> titleEles = driver.findElements(By.cssSelector("[class=\"video-title text-overflow-2\"]"));
            for (WebElement titleEle : titleEles) {
                WebElement element = titleEle.findElement(By.tagName("a"));
                String title = element.getText();
                String id = element.getAttribute("href");
                id = id.replace(base, "").replace("/detail", "");
                System.out.println(title + ":" + id);
                idMap.put(title, id);
            }
            System.out.println();
            System.out.println();
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            index++;
        }

        System.out.println();

        // 爬取m3u8连接
        for (String key : idMap.keySet()) {
            String id = idMap.get(key);
            driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
            driver.manage().window().maximize();
            driver.get(base + "/play" + id + "/1/1");
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            WebElement element3 = driver.findElement(By.tagName("iframe"));
            String attribute = element3.getAttribute("src");
            m3u8Map.put(key,attribute);
            System.out.println(key + ":" + attribute);
        }
        driver.close();

        // 保存数据库

    }


}
