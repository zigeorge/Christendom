package christian.network.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by User on 11-Apr-16.
 */
public class UserNChurchUtils {

    public static String prepareChurchJsonParam(String user_id, String church_id) {
        String jSONParam = "{\"" + StaticData.USER_ID + "\":\"" + user_id + "\",\"" + StaticData.CHURCH_ID + "\":\"" + church_id + "\"}";
        try {
            jSONParam = URLEncoder.encode(jSONParam, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return jSONParam;
    }

    public static String prepareUserJsonParam(String user_id, String nyf) {
        String jSONParam = "{\"" + StaticData.USER_ID + "\":\"" + user_id + "\",\"nyf\":\"" + nyf + "\"}";
        try {
            jSONParam = URLEncoder.encode(jSONParam, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return jSONParam;
    }

    public static String prepareJsonParam(HashMap<String, String> hmParams) {
        String jSonParam = "{";
        int index = 0;
        int range = hmParams.size();
        for (String key : hmParams.keySet()
                ) {
            jSonParam += "\"" + key + "\":\"" + hmParams.get(key) + "\"";
            if (index < range - 1) {
                index++;
                jSonParam += ",";
            } else {
                jSonParam += "}";
            }
        }
        try {
            jSonParam = URLEncoder.encode(jSonParam, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return jSonParam;
    }

    public static String getPostTime(long commentTImeMs, long currentTimeMs) {
        long remainingTime = currentTimeMs - commentTImeMs;
        int day = (int) ((((remainingTime / 1000) / 60) / 60) / 24);
        remainingTime = remainingTime - (long) day * 24 * 60 * 60 * 1000;
        int hour = (int) (((remainingTime / 1000) / 60) / 60);
        remainingTime = remainingTime - (long) hour * 60 * 60 * 1000;
        int minute = (int) ((remainingTime / 1000) / 60);
        String rTime = "";
        if (day > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(commentTImeMs);
            rTime = String.format("%02d-%s",calendar.get(Calendar.DAY_OF_MONTH),getMonthName(calendar.get(Calendar.MONTH))) +
                    " at " + String.format("%02d:%02d ",calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE))
                    + getAMPM(calendar.get(Calendar.AM_PM));
        } else if (day < 1 && hour > 0) {
            rTime = hour + " hrs";
        } else if (day < 1 && hour < 1 && minute >= 1) {
            rTime = minute + " mins";
        } else{
            rTime = "Just Now";
        }

        return rTime;
    }

    private static String getMonthName(int month) {
        switch (month) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
            default:
                return "Jan";
        }
    }

    private static String getAMPM(int ampm) {
        if (ampm == 0) {
            return "a.m";
        } else {
            return "p.m";
        }
    }

}
