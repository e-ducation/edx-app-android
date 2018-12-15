package org.edx.mobile.eliteu.util;

import android.annotation.SuppressLint;

import com.google.gson.internal.bind.util.ISO8601Utils;

import org.edx.mobile.logger.Logger;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateUtil {
    private static final Logger logger = new Logger(DateUtil.class.getName());

    /*
     * Converting Date in string format to Date object and converting the Current
     * Stamp
     */
    public static Date convertToDate(String date) {
        if(date==null){
            return null;
        }

        Date parsedate = null;
        final ParsePosition parsePosition = new ParsePosition(0);
        try {
            parsedate = ISO8601Utils.parse(date, parsePosition);
            logger.debug("Parsed Data"+parsedate);
            return parsedate;

        } catch (ParseException e) {
            logger.error(e);
        }
        return parsedate;
    }

    /**
     *  格式：2018年10月1日
     */
    public static String formatVipInfoDate(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = DateUtil.convertToDate(date);

            String formattedDate = dateFormat.format(startDate);
            return formattedDate;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

}
