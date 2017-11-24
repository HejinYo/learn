package cn.hejinyo.learn.localdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * @author : heshuangshuang
 * @date : 2017/11/24 12:50
 */
public class localdate {
    public static void main(String[] args) {
        LocalDate localDate = LocalDate.now();
        System.out.println("当前日期:" + localDate);
        System.out.println("本月最后一天: " + localDate.lengthOfMonth());
        System.out.println("本月名称: " + localDate.getMonth().name());
        System.out.println("加两天的日期：" + localDate.plus(2, ChronoUnit.DAYS));
        System.out.println("减两天的日期：" + localDate.minus(2, ChronoUnit.DAYS));
        System.out.println("字符串转日期：" + LocalDate.parse("2017-08-08"));
        System.out.println("======================================");
        LocalTime localTime = LocalTime.now();
        System.out.println("当前时间:" + localTime);
        System.out.println("当前小时: " + localTime.getHour());
        System.out.println("加两个小时:" + localTime.plus(2, ChronoUnit.HOURS));
        System.out.println("加6分钟:" + localTime.plusMinutes(6));
        System.out.println("减两小时:" + localTime.minus(2, ChronoUnit.HOURS));
        System.out.println("字符串转时间" + LocalTime.parse("08:08:08"));
        System.out.println("======================================");
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println("日期时间:" + localDateTime);
        System.out.println("获得本页总天数: " + localDateTime.getMonth().length(true));
        System.out.println("本月名称: " + localDateTime.getMonth().name());
        System.out.println("加两天：" + localDateTime.plus(2, ChronoUnit.DAYS));
        System.out.println("减两天：" + localDateTime.minus(2, ChronoUnit.DAYS));
        System.out.println(LocalDateTime.parse(LocalDate.now().toString() + " " + "09:00:09",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
