package com.example.cafeteriaapplication;

import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;

import android.widget.TextView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

//gif
import com.bumptech.glide.Glide;

//firebase
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.os.Handler;


public class MainActivity extends AppCompatActivity {
    //firebase access
    FirebaseDatabase rootNode;
    Button submit_button;

  public float total = 0;
    public float total_num = 0;
    DatabaseReference ref;
    private static final String PREFS_NAME = "color_settings";
    int order_num;
    int i = 0;
    float final_rating = 0;
    ImageView plate, display;
    ImageButton gifImage, exitButton;
    TextView dateText, name_view, lunchText, commentSection;
    EditText comment;
    int[] images = {
            R.drawable.red_plate,
            R.drawable.yellow_plate,
            R.drawable.green_plate,
            R.drawable.blue_plate,
            R.drawable.purple_plate,
            R.drawable.pink_plate,
            R.drawable.brown_plate,
            R.drawable.black_plate
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //기본 코드
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);

        //개선함
        SharedPreferences pref = getSharedPreferences("comment_section", MODE_PRIVATE);
        SharedPreferences.Editor editing = pref.edit();
        editing.putInt("is_comment", 0);
        editing.apply();
        comment = findViewById(R.id.comment);
        submit_button = findViewById(R.id.submit_button);
        commentSection = findViewById(R.id.comment_section);
        display = findViewById(R.id.display);
        exitButton = findViewById(R.id.exitButton);
        comment.setEnabled(false);
        commentSection.setEnabled(false);
        display.setEnabled(false);
        submit_button.setEnabled(false);
        exitButton.setEnabled(false);
        comment.setVisibility(View.GONE);
        submit_button.setVisibility(View.GONE);
        commentSection.setVisibility(View.GONE);
        display.setVisibility(View.GONE);
        exitButton.setVisibility(View.GONE);
        //submitting
        submit_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //개선점 가져오기
                String comment_text = comment.getText().toString();
                SharedPreferences namepref = getSharedPreferences("name_of_user",MODE_PRIVATE);

                String username = namepref.getString("username","");

                //개선점 넣기
                rootNode = FirebaseDatabase.getInstance();
                rootNode.getReference("comments").child(username).setValue(comment_text);
                commentSection.setText("개선점이 전달 됐어. 작성해줘서 고마워!");

                //개선점을 넣었는지 확인해 계속 넣을 수 없게 하기
                SharedPreferences pref = getSharedPreferences("comment_section", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("is_comment", 1);
                editor.apply();

                //개선점 일시 중단
                comment.setEnabled(false);
                submit_button.setEnabled(false);
                submit_button.setVisibility(View.GONE);
                comment.setVisibility(View.GONE);

            }
        });

        //특정 기념일 챙기기


        //day가져오기
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("KST"));
        int days = calendar.get(Calendar.DAY_OF_WEEK);

                //lunch data 부르기
                LunchGet.lunch(new LunchGet.ResultCallback() {
                    @Override
                    public void onResult(String value) {
                        runOnUiThread(() -> {
                            SharedPreferences lunchData = getSharedPreferences("lunchData", MODE_PRIVATE); //d이거 확인
                            String lunchList = lunchData.getString("lunchData", "페이지를 다시 새로고침해줘!");


                            if (lunchList.equals("unavailable")) {
                                long now = System.currentTimeMillis(); //현재 시간
                                Date date = new Date(now);
                                SimpleDateFormat sd = new SimpleDateFormat("MMdd");
                                SimpleDateFormat ad = new SimpleDateFormat("yyyyMMdd");
                                SimpleDateFormat wb = new SimpleDateFormat("yyyyMM");
                                String annualDays = sd.format(date);
                                String specialDays = ad.format(date);
                                String WinterBreak = wb.format(date);

                                String noLunch = "오늘은 급식 정보가 없어!";
                                if (annualDays.equals("1225")) noLunch = "Merry christmas!";
                                if (specialDays.equals("20251231"))
                                    noLunch = "안여 방학을 축하해(*ˊᵕˋ*)੭";

                                Log.d("debug",WinterBreak);
                                if(WinterBreak.equals("202601")) {
                                    noLunch = "안여 방학을 축하해(*ˊᵕˋ*)੭";
                                    Log.d("debug","yes");
                                }
                                Log.d("debug",noLunch);
                                lunchText = findViewById(R.id.lunchdata);
                                lunchText.setText(noLunch);
                            } else {
                                new Thread(() -> {

                                    //급식 정리
                                    String[] splitText = lunchList.split(" |\\<br/>");
                                    List<String> myList = new ArrayList<>();
                                    myList.add(splitText[0].replaceAll("^\"|\"$", ""));
                                    for (int i = 2; i < splitText.length; ) {
                                        myList.add(splitText[i]);
                                        i += 2;
                                    }
                                    runOnUiThread(() -> {
                                        String lastLunch = "";
                                        for (int i = 0; i < myList.size(); ) {
                                            lastLunch = lastLunch + myList.get(i) + "\n";
                                            i += 1;
                                        }

                                        //위젯에 저장
                                        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE); //d이거 확인
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("widget_text", lastLunch);
                                        editor.apply();

                                        //앱에 띄우는 텍스트
                                        lunchText = findViewById(R.id.lunchdata);
                                        if (days == 1 || days == 7) {
                                            lunchText.setText("\uD83C\uDFAF월요일 급식\n\n" + lastLunch);
                                        } else {
                                            lunchText.setText("\uD83C\uDFAF오늘의 급식\n\n" + lastLunch);
                                        }
                                    });

                                }).start();

                            }
                            SharedPreferences lunchDataa = getSharedPreferences("lunchData", MODE_PRIVATE); //d이거 확인
                            SharedPreferences.Editor editor = lunchDataa.edit();
                            editor.putString("lunchData", value);
                            editor.apply();
                            Log.d("debug", "lunchdata: " + value);
                        });

                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });


        //식판 색 변경
        SharedPreferences spp = getSharedPreferences("brush_color", MODE_PRIVATE);
        order_num = spp.getInt("brush_color", 0);
        plate = findViewById(R.id.plate);
        plate.setImageResource(images[order_num]);

        //요술봉 눌러 색 바꾸기
        ImageButton colorChanger;
        colorChanger = findViewById(R.id.color_change);
        colorChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                order_num += 1;
                if (order_num == 7) order_num = 0;
                //색상 저장
                SharedPreferences preferences_color = getSharedPreferences("brush_color", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences_color.edit();
                editor.putInt("brush_color", order_num);
                editor.apply();

                //색상 변경
                plate.setImageResource(images[order_num]);
            }

        });

        //gif 그림 표기
        gifImage = findViewById(R.id.gif);
        Glide.with(this).load(R.drawable.appicon).into(gifImage);

        //gif 그림 눌러 개선함 - 시작시 안보이게


        //gif 눌렀을 때
        gifImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SharedPreferences preferences = getSharedPreferences("comment_section", MODE_PRIVATE);
                int commentTrue = preferences.getInt("is_comment", 0);
                if(commentTrue == 1){
                    commentSection.setEnabled(true);
                    display.setEnabled(true);
                    exitButton.setEnabled(true);
                    commentSection.setVisibility(View.VISIBLE);
                    display.setVisibility(View.VISIBLE);
                    exitButton.setVisibility(View.VISIBLE);
                }
                else {
                    comment.setEnabled(true);
                    commentSection.setEnabled(true);
                    display.setEnabled(true);
                    exitButton.setEnabled(true);
                    submit_button.setEnabled(true);

                    submit_button.setVisibility(View.VISIBLE);
                    comment.setVisibility(View.VISIBLE);
                    commentSection.setVisibility(View.VISIBLE);
                    display.setVisibility(View.VISIBLE);
                    exitButton.setVisibility(View.VISIBLE);
                }
                }
        });
        //exit button clicked
            exitButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    comment.setEnabled(false);
                    commentSection.setEnabled(false);
                    display.setEnabled(false);
                    exitButton.setEnabled(false);
                    submit_button.setEnabled(false);

                    submit_button.setVisibility(View.GONE);
                    comment.setVisibility(View.GONE);
                    commentSection.setVisibility(View.GONE);
                    display.setVisibility(View.GONE);
                    exitButton.setVisibility(View.GONE);
                }

            });

        //날짜설정
        long now = System.currentTimeMillis(); //현재 시간
        Date date = new Date(now);
        dateText = findViewById(R.id.date);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MM월 dd일"); //형식 설정
        String today_date = sdf.format(date);
        dateText.setText(today_date);

        //ratingbar
        TextView speech = findViewById(R.id.speechText);
        TextView speech2 = findViewById(R.id.speechText2);
        RatingBar ratingBar = findViewById(R.id.ratingbar);
        ImageButton submit = findViewById(R.id.ratingSubmit);

        name_view = findViewById(R.id.name_place);
        SharedPreferences namepref = getSharedPreferences("name_of_user",MODE_PRIVATE);

        String username = namepref.getString("username","");
        String name = namepref.getString("name","");
            name_view.setText(name + "(이)의 급식표");
        //initial data value

        Log.d("debug","username is: "+username);
        rootNode = FirebaseDatabase.getInstance();
        SharedPreferences pref_rated = getSharedPreferences("is_rated",MODE_PRIVATE);
        Boolean rated = pref_rated.getBoolean("is_rated",false);
        Log.d("debug","rated: "+rated);
        if(!rated && username!=null) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("ratings", 0);
            data.put("date", -1);
            Log.d("debug","username:"+username);
            rootNode.getReference("name").child(username).setValue(data);
        }
            ref = FirebaseDatabase.getInstance().getReference().child("name");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                //initial rating
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int rating = snapshot.child("ratings").getValue(Integer.class);
                    int date = snapshot.child("date").getValue(Integer.class);
                    if (date == -1){

                    }
                    else if (days != date) {
                         snapshot.getRef()
                                 .child("date")
                                 .setValue(-1);
                    } else if (days == date) {
                        total += rating;
                        total_num += 1;
                    }
                }
                //final rating 계산
                if (total_num == 0) {
                    final_rating = 0;
                }
                else final_rating = total / total_num;
                final_rating=Math.round(final_rating * 10) / 10.0f;

              //  String final_rating2 = String.format("%.1f", final_rating);
                if(total_num == 0)speech2.setText("아직 아무도 급식 평가를 안했어!");
                else speech2.setText("오늘 " + (int) total_num + "명이 평가한 급식 평균은" + final_rating + "점이야!");
                speech2.setTextSize(20);
                total_num = 0;
                total = 0;
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //rating submit 이후
        submit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences pref = getSharedPreferences("is_rated",MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean("is_rated",true);
                edit.apply();
                //get rating
                int rating = (int) ratingBar.getRating();

                //store ratings

                HashMap<String, Object> data = new HashMap<>();
                data.put("ratings", rating);
                data.put("date", days);

                SharedPreferences preferences = getSharedPreferences("name_of_user", MODE_PRIVATE);
                String username = preferences.getString("username", "default_value");
                rootNode = FirebaseDatabase.getInstance();
                if(username!=null) {
                    rootNode.getReference("name").child(username).setValue(data);
                }
                // 불러오기
                ref = FirebaseDatabase.getInstance().getReference().child("name");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            int rating = snapshot.child("ratings").getValue(Integer.class);
                            int date = snapshot.child("date").getValue(Integer.class);
                            String name_user = snapshot.getKey();

                            //   date = snapshot.child("date").getValue(Integer.class);
                            if (date == -1) {

                            }
                            else if (days != date) {
                                rootNode.getReference("name").child(name_user).child("ratings").setValue(-1);
                            }
                            else if (days == date) {
                                total += rating;
                                total_num += 1;
                            }
                        }

                        //fimal rating 계산
                        if (total_num == 0) final_rating = 0;
                        else final_rating = total / total_num;
                      //  String final_rating2 = String.format("%.1f", final_rating);
                        final_rating=Math.round(final_rating * 10) / 10.0f;


                        speech2.setText("오늘 " + (int) total_num + "명이 평가한 급식 평균은" + final_rating + "점이야!");
                        speech2.setTextSize(20);
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                //submit 이후 화면
                submit.setEnabled(false);
                ratingBar.setEnabled(false);
                submit.setVisibility(View.GONE);
                ratingBar.setVisibility(View.GONE);

                //대사 바꾸기

                speech.setText("평가해줘서 고마워!");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        speech.setText("앱에 대해 할 말이 있어? 나를 눌러봐!");
                    }
                }, 3000);   //3 seconds
            }
        });
    }
}

