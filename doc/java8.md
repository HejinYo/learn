# LocalDate、LocalDateTime、LocalTime
## 1、Date与LocalDate的互转

> Date转LocalDate
```$xslt
Date date = new Date();
LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
```
> LocalDate 转 Date:
```$xslt
LocalDateTime localDateTime = LocalDateTime.now();
Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
```
+ LocalDate和Date之间使用了Instant来转换类型，也可以使用其他的方式来转换。LocalDate.of()等等

## 2、LocalDate的一些时间操作
```$xslt
DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
 //字符串转换成LocalDate类型
LocalDate ld = LocalDate.parse("2015-11-23", ymd);
System.out.println("年月日："+ld.getYear()+"-"+ld.getMonthValue()+"-"+ld.getDayOfMonth());
System.ouot.println("从1970年1月1日开始的总天数："+ld.toEpochDay());
ld = LocalDate.of(2015,11,25);
System.out.println("新年月日："+ld.getYear()+"-"+ld.getMonthValue()+"-"+ld.getDayOfMonth());

ld = ld.plusDays(1);
System.out.println("加一天年月日："+ld.getYear()+"-"+ld.getMonthValue()+"-"+ld.getDayOfMonth());

ld = ld.minusDays(2);
System.out.println("减两天年月日："+ld.getYear()+"-"+ld.getMonthValue()+"-"+ld.getDayOfMonth());

ld = ld.plusMonths(1);
System.out.println("加一个月年月日："+ld.getYear()+"-"+ld.getMonthValue()+"-"+ld.getDayOfMonth());

ld = ld.minusMonths(1);
System.out.println("减一个月年月日："+ld.getYear()+"-"+ld.getMonthValue()+"-"+ld.getDayOfMonth());

ld.plusWeeks(1);//加一星期
ld.plusYears(1);//加一年
ld.minusWeeks(1);//减一星期
ld.minusYears(1);//减一年
```

## 3、 对LocalDate的支持
> 依赖
```xml
<dependency> 
 <groupId>org.mybatis</groupId> 
 <artifactId>mybatis-typehandlers-jsr310</artifactId> 
 <version>1.0.1</version> 
</dependency>
```

> Entity里面的Date替换成LocalDate、LocalDateTime
```java
public class User { 
    private Integer id; 
    private String name; 
    private LocalDate createDate; 
     private LocalDateTime createTime; 
}
```

## 4、例子[参考](http://blog.csdn.net/sf_cyl/article/details/51987088)
```java
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
    }
}
```
