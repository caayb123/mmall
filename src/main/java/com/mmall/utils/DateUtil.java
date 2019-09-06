package com.mmall.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static final String NORMAL_FORMATE="yyyy-MM-dd HH:mm:ss";
    //str->date
    public static Date strToDate(String datetime,String formate) throws Exception{
        if (datetime==null){
            return null;
        }
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(formate);
       return simpleDateFormat.parse(datetime);

    }
    //date->str
    public static String dateToStr(Date date,String formate){
        if (date==null){
            return null;
        }
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(formate);
        return simpleDateFormat.format(date);
    }

    //str->date
    public static Date strToDate(String datetime) {
        if (datetime==null){
            return null;
        }
        try {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat(NORMAL_FORMATE);
            return simpleDateFormat.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
         return null;
    }
    //date->str
    public static String dateToStr(Date date){
        if (date==null){
            return null;
        }
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(NORMAL_FORMATE);
        return simpleDateFormat.format(date);
    }
    public static void main(String[] args) throws Exception {
        String s = DateUtil.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss");
        System.out.println(s);

        Date date = DateUtil.strToDate(s, "yyyy-MM-dd HH:mm:ss");
        System.out.println(date);
    }
}
