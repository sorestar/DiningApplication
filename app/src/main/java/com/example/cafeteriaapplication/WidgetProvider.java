package com.example.cafeteriaapplication;

import static android.content.Context.MODE_PRIVATE;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.util.Log;
import android.widget.RemoteViews;


import androidx.core.content.res.ResourcesCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WidgetProvider extends AppWidgetProvider {

    long now = System.currentTimeMillis(); //현재 시간

    SimpleDateFormat tdy;
    Date date = new Date(now); //Date 생성
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
    int month = calendar.get(Calendar.MONTH) + 1;  // 0 = January → so add 1
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int days = calendar.get(Calendar.DAY_OF_WEEK);
    String dayName="";
    //bitmap생성 및 글꼴 변환
    public static Bitmap createTextBitmap(Context context, String text) {

        //paint 기본설정
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(33f);
        paint.setColor(Color.DKGRAY);
        paint.setTextAlign(Paint.Align.CENTER);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.bdh); // res/font/my_font.ttf
        paint.setTypeface(typeface);

        //줄 나누기(\n)사용 불가
        String[] lines = text.split("\n");
        float maxWidth = 0;

        for(String line : lines){
            int lineWidth = (int) paint.measureText(line);
            if (lineWidth > maxWidth) maxWidth = lineWidth;
        }
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float lineHeight = fontMetrics.descent - fontMetrics.ascent;




       // float baseline = -paint.ascent(); // ascent() is negative

       // int width = (int) (paint.measureText(text) + 0.5f);
       // int height = (int) (baseline + paint.descent() + 0.5f);
        int width = (int) (maxWidth + 0.5f);
        int height = (int) ((lineHeight + 15f) * lines.length);

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        float y = -fontMetrics.ascent +2f ; // baseline
        int i = 0;
        for (String line : lines) {
            if(i<9) {
                i++;
                canvas.drawText(line, width / 2f, y, paint);
                y += lineHeight +12f;
            }
        }
        return image;
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);


        switch(days){
            case 2: dayName = "월요일 "; break;
            case 3: dayName = "화요일 "; break;
            case 4: dayName = "수요일 "; break;
            case 5: dayName = "목요일 "; break;
            case 6: dayName = "금요일 "; break;
        }
        Log.i("hi","hello");

        tdy = new SimpleDateFormat("🌀 M월 d일", Locale.KOREA);

        for (int appWidgetID : appWidgetIds) {
            //눌렀을때 앱 여는거
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.openApplication, pendingIntent);

            //급식 정보 가져오는거
            String text = prefs.getString("widget_text", "₍^. .^₎⟆ \n 이것은 단순한 고양이가 아닙니다. \n 에러입니다. \n @hyxx.nn으로 연락주세요....");
            Log.d("debug","widget text: "+text);
            String finaltext="";
            //토,일 요일에 다른 텍스트 띄우기
            if(days==1||days==7) {
                finaltext="\uD83C\uDF00월요일 급식 맛보기\n"+text;
            }
            else{
                finaltext=tdy.format(date)+" "+dayName+"급식\n"+text;

            }
            //글꼴 바꾸기
            Bitmap bitmap = createTextBitmap(context, finaltext);
            views.setImageViewBitmap(R.id.lunch, bitmap);
            //update
            appWidgetManager.updateAppWidget(appWidgetID, views);

        }



    }
}
