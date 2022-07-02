package com.foundation.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateUtils {

    /**
     * 获取起止日期
     * @param sdf 需要显示的日期格式
     * @param date 需要参照的日期
     * @param n 最近n周
     * @param option 0 开始日期；1 结束日期
     * @param k 0 包含本周 1 不包含本周
     * @return
     */
    public static String getFromToDate(SimpleDateFormat sdf, Date date, int n, int option, int k) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int offset = 0 == option ? 1 - dayOfWeek : 7 - dayOfWeek;
        int amount = 0 == option ? offset - (n - 1  + k) * 7 : offset - k * 7;
        calendar.add(Calendar.DATE, amount);
        return sdf.format(calendar.getTime());
    }
}
