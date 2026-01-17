package com.example.cafeteriaapplication;


import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LunchGet {
    //url 생성
    public interface ResultCallback {
        void onResult(String value);
        void onError(Exception e);
    }



    public static void lunch(ResultCallback callback) {
        //요일 가져오기
        String lunchDate;
        SimpleDateFormat tdy = new SimpleDateFormat("yyyyMMdd");
        String OPEN_API_KEY = BuildConfig.OPEN_API_KEY;

        int newDate = 0;
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        long now = System.currentTimeMillis();
        int days = calendar.get(Calendar.DAY_OF_WEEK);
        Date date = new Date(now); //Date 생성
        Log.d("debug", String.valueOf(days));
        //토, 일요일일 때 월요일 급식 보여주기

         if (days == 7) {
            newDate = Integer.parseInt(tdy.format(date));
            newDate += 2;
            lunchDate = String.valueOf(newDate);
        } else if (days == 1) {
            newDate = Integer.parseInt(tdy.format(date));
            newDate += 1;
            lunchDate = String.valueOf(newDate);

        } else {
            lunchDate = tdy.format(date);
        }

        Log.d("debug","newDate: "+newDate+"lunchDate: "+lunchDate+"date: "+date);


        new Thread(() -> {

            String url = "https://open.neis.go.kr/hub/mealServiceDietInfo?Type=json&ATPT_OFCDC_SC_CODE=J10&SD_SCHUL_CODE=7530185&KEY="+OPEN_API_KEY+"&MLSV_YMD=" + lunchDate;
            URL Url = null;
            //url 링크로 변환

            try {
                Url = new URL(url);

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            String user = "";
            //read json
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(Url);
                if (root.has("RESULT")) {
                    user = "unavailable";
                } else {
                    JsonNode rows = root.get("mealServiceDietInfo").get(1).get("row");

                    for (JsonNode row : rows) {
                        JsonNode dishNode = row.get("DDISH_NM");
                        user = dishNode.toString();

                    }

                }
                Log.d("debug","from lunch get:"+user);
                callback.onResult(user);
            } catch (Exception e) {

                Log.d("DEBUG", Log.getStackTraceString(e));
                throw new RuntimeException(e);
            }
        }).start();

    }


}

