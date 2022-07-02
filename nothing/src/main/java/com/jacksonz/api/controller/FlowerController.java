package com.foundation.api.controller;

import com.foundation.api.domain.FlowerSortObject;
import com.foundation.api.utils.ExcelUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping(value = "/flower")
public class FlowerController {

    @RequestMapping(value = "/uploadFile/sort", method = RequestMethod.POST)
    public void uploadExcelForSort (HttpServletResponse httpServletResponse, MultipartFile file) throws Exception {
        String titles = "圆形 类圆形/椭圆形 多边形 不规则形 深分叶 浅分叶 长毛刺 短毛刺 棘突征 边缘光滑 边缘毛糙 边界清晰 边界不清晰 其他";
        ArrayList<String> titleList = new ArrayList<>(Arrays.asList(titles.split(" ")));
        List<List<String>> lists = ExcelUtils.importExcel(file, -1, -1, 9);
        ArrayList<FlowerSortObject> aList = new ArrayList<>();
        //第一层
        lists.forEach(e -> {
            if (!CollectionUtils.isEmpty(e)) {
                FlowerSortObject a = new FlowerSortObject();
                a.setId((String) e.get(0));
                Map<String, String> map = a.getMap();
                titleList.forEach(title -> {

                    if ("其他".equals(title)) {
                        AtomicBoolean isExist = new AtomicBoolean(false);
                        //第二层
                        e.forEach(data -> {
                            if (((String) data).contains("其他")) {
                                map.put(title, "有");
                                isExist.set(true);
                            }
                        });
                        if (!isExist.get()) {
                            map.put(title, "无");
                        }

                    } else {
                        if (e.contains(title)) {
                            map.put(title, "有");
                        } else {
                            map.put(title, "无");
                        }
                    }
                });
                aList.add(a);
            }
        });
        List<String> headers = new ArrayList<>();
        headers.add("编号");
        headers.addAll(titleList);
        List<List<String>> datas = new ArrayList<>();
        aList.forEach(e -> {
            ArrayList<String> data = new ArrayList<>();
            data.add(e.getId());
            Map<String, String> map = e.getMap();
            for (String s : map.keySet()) {
                data.add(map.get(s));
            }
            datas.add(data);
        });
        ExcelUtils.exportExcel(httpServletResponse, "", "",headers, datas);
    }
}
