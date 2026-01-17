package com.example.cafeteriaapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Pattern;

public class InitialScreen extends AppCompatActivity {
    TextView text;
    DatabaseReference ref ;
    ImageButton next;
    ImageView gifImage_initial;
    String name, name_users, name_user;;
    EditText nameText;
    public int num = 0;

//input filter
    public InputFilter filterAlphaNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅣ가-힣]*$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    //on create

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //필수
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_screen);

        //gif 표기
        gifImage_initial = findViewById(R.id.gif_initial);
        Glide.with(this).load(R.drawable.appicon).into(gifImage_initial);
        //처음 시작 화면 정리
        nameText = findViewById(R.id.name);
        text = findViewById(R.id.text);
        nameText.setVisibility(View.GONE);
        nameText.setEnabled(false);

        //버튼 클릭시 설정
        next = findViewById(R.id.next_button);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (num == 0)
                //시작 화면 표시
                    num += 1;
                else if (num == 1) {
                    //이름 물어보기
                    text.setText("넌 이름이 뭐야?");
                    nameText.setVisibility(View.VISIBLE);
                    nameText.setEnabled(true);
                    num += 1;
                }
                else if (num == 2) {
                    //이름 물어보고 난 버튼
                    nameText.setFilters(new InputFilter[]{filterAlphaNum});
                    name = nameText.getText().toString();

                    //이름이 입력됐는지만 확인
                    if (TextUtils.isEmpty(name)) {
                        nameText.setError("닉네임을 입력해줘!");
                    }
                    else {
                        //shared preferences에 이름 저장
                        SharedPreferences sp = getSharedPreferences("name_of_user", MODE_PRIVATE);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString("name",name);
                        edit.apply();

                        //database 가져와서 이름 중복 확인
                        ref = FirebaseDatabase.getInstance().getReference().child("name");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                name_user = name;
                                //for loop으로 이름이 중복인지 확인
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    //저장된 사용자들 이름 불러오기
                                    name_users = snapshot.getKey();
                                    //이름이 중복일 때,
                                    if (Objects.equals(name_user, name_users)) {
                                        name_user = name_user + "a";
                                    }
                                    if (Objects.equals(name_user, name_users)) {
                                        name_user = name_user + "b";
                                    }

                                    SharedPreferences preferences = getSharedPreferences("name_of_user", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("username", name_user);
                                    editor.apply();
                                    //first run

                                    SharedPreferences settings = getSharedPreferences("prefs",0);
                                    editor = settings.edit();
                                    editor.putBoolean("firstRun",false);
                                    editor.commit();
                                       }
                                Log.d("debug","name: "+name+"username: "+name_user);
                                edit.putString("username",name_user);
                                edit.apply();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("debug", "cancelled!");
                            }
                        });

                        //정리
                        text.setText(name + "(이)구나~ 반가워!");
                        nameText.setVisibility(View.GONE);
                        nameText.setEnabled(false);
                        num += 1;
                    }
                }

                else if (num == 3) {
                    num += 1;
                    text.setText("앱 사용하면서 문제가 생기면 언제든 리뷰로 남겨줘!");
                    text.setTextSize(20);
                } else if(num==4)
                {
                    //여기서 이미 default value로 저장
                    Log.d("debug","name of user: "+name+"username: "+name_user);

                    Intent intent3 = new Intent(InitialScreen.this, MainActivity.class);
                    startActivity(intent3);

                }
            }
        });
    }
    }

