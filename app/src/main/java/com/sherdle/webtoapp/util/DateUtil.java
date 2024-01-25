package com.sherdle.webtoapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String getDateTimeNow() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = dateFormat.format(currentDate);
        return currentDateTime;
    }
}
