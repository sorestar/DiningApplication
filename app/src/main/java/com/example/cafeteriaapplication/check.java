package com.example.cafeteriaapplication;
import android.app.Activity;

import android.content.Intent;

import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;

public class check extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        SharedPreferences settings=getSharedPreferences("prefs",0);
        boolean firstRun=settings.getBoolean("firstRun",true);
        Log.d("debug","firstrun: "+firstRun);
        if(firstRun!=false)//if running for first time
        //Splash will load for first time
        {
            Intent i=new Intent(check.this,InitialScreen.class);
            startActivity(i);
            finish();
        }
        else
        {

            Intent a=new Intent(check.this,MainActivity.class);
            startActivity(a);
            finish();
        }
    }

}