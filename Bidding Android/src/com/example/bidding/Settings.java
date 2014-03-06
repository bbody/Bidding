package com.example.bidding;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends Activity{
	Button saveButton;
	Button cancelButton;
	EditText address;
	EditText sender;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        address = (EditText)findViewById(R.id.editText1);
        address.setText(sharedPrefs.getString("address", "10.180.24.123"));
        sender = (EditText)findViewById(R.id.editText2);
        sender.setText(sharedPrefs.getString("bidder", "Bidder"));
        saveButton = (Button)findViewById(R.id.button1);
        saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String a = address.getText().toString();
				String s = sender.getText().toString();
				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				Editor edit = sharedPrefs.edit();
				edit.putString("bidder", s);
				edit.putString("address", a);
				edit.apply();
				quitApplication();
			}
		});
        cancelButton = (Button)findViewById(R.id.button2);
        cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				quitApplication();
			}
		});
    }
	
	private void quitApplication(){
		Intent prefIntent = new Intent(getBaseContext(), MainActivity.class);
		startActivity(prefIntent);
	}

}
