package com.example.elite;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import custom.classes.DBCalss;

public class SplashScreen extends Activity
{
	Timer timer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.splash_screen);
		copyDBFromAssetToApp();		
		
		timer = new Timer();
		timer.schedule(new TimerTask()
		{
			public void run() 
			{
				Intent i = new Intent(SplashScreen.this, MainActivity.class);
				startActivity(i);
				finish();
			}

		}, 5000);
	}
	
	
	private void copyDBFromAssetToApp()
	{
		DBCalss dp = new DBCalss(this);
		try
		{
			dp.createDataBase();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
