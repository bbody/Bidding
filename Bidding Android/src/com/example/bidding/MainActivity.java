package com.example.bidding;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import de.tavendo.autobahn.Autobahn.CallHandler;
import de.tavendo.autobahn.Autobahn.EventHandler;
import de.tavendo.autobahn.AutobahnConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import de.tavendo.autobahn.WebSocketOptions;

public class MainActivity extends Activity {
	private Spinner spinner;
	private EditText editText;
	ListView ls;
	ArrayAdapter aa;
	String bidder;
	String address;
	AutobahnConnection mConnection = new AutobahnConnection();
	Button sendButton;
	Stack<String> history = new Stack<String>();
	boolean auction1 = false;
	boolean auction2 = false;
	boolean auction3 = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get preferences
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		address = sharedPrefs.getString("address", "localhost");
		bidder = sharedPrefs.getString("bidder", "Bidder");
		
		// Setup listview
		ls = (ListView)findViewById(R.id.listView1);
		aa = new ArrayAdapter<String>(this, R.layout.element, history);
		ls.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				
				mConnection.call("http://example.com/auction#highest", Integer.class, new CallHandler() {

					@Override
					public void onResult(Object result) {
						int res = (Integer) result;
						if (ls.getSelectedItemPosition() == arg2){
							editText.setText("" + res);
						}

					}

					@Override
					public void onError(String errorId, String errorInfo) {
						//alert("calc:add RPC error - " + errorInfo);
					}
				}, arg2);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		ls.setAdapter(aa);



		spinner = (Spinner)findViewById(R.id.spinner1);
		List<String> list = new ArrayList<String>();
		list.add("Auction 1");
		list.add("Auction 2");
		list.add("Auction 3");
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		
		
		editText = (EditText)findViewById(R.id.editText1);
		sendButton = (Button)findViewById(R.id.button1);
		setupNotSending();
	}

	private void setupNotSending(){
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mConnection.isConnected()){
					Toast.makeText(getApplicationContext(), "Warning: Not connected to server", Toast.LENGTH_SHORT).show();
				} else {
					int auction = spinner.getSelectedItemPosition();
					String bid = editText.getText().toString();
					Integer bidAmount = new Integer(bid);
					MyEvent1 evt = new MyEvent1();
					evt.amount = bidAmount;
					evt.auction = auction;
					evt.bidder = bidder;
					mConnection.publish("http://example.com/auction#bidding", evt	);
				}
			}
		});
	}


	public void connect(){
		final String wsuri = "ws://"+address+":8080/Bidding/wamp";
		try {
			WebSocketOptions wo = new WebSocketOptions();
			wo.setReceiveTextMessagesRaw(true);
			mConnection.connect(wsuri, new WebSocketHandler(){
				@Override
				public void onOpen() {

					Toast.makeText(getApplicationContext(), "Connected...", Toast.LENGTH_SHORT).show();
					connectionItem.setTitle(getResources().getString(R.string.action_connect2));

					// We establish a prefix to use for writing URIs using shorthand CURIE notation.
					//mConnection.prefix("auction", "http://example.com/auction#bidding");

					mConnection.subscribe("http://example.com/auction#bidding", MyEvent1.class, new EventHandler() {

						@Override
						public void onEvent(String topicUri, Object event) {

							// when we get an event, we safely can cast to the type we specified previously
							MyEvent1 evt = (MyEvent1) event;

							String line = "Auction " + (evt.auction + 1) + ": " + evt.bidder	 + " with $" + evt.amount; 
							
							
							history.push(line);
							aa.notifyDataSetChanged();
						}
					});
					connectionItem.setTitle(getResources().getString(R.string.action_connect2));
				}

				@Override
				public void onClose(int code, String reason) {
					Toast.makeText(getApplicationContext(), "Disconnected...", Toast.LENGTH_SHORT).show();
					connectionItem.setTitle(getResources().getString(R.string.action_connect1)); 
				}

			}, wo);
		} catch (WebSocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	MenuItem connectionItem;
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// action with ID action_refresh was selected
		case R.id.action_settings:
			Intent prefIntent = new Intent(getBaseContext(), Settings.class);
			startActivity(prefIntent);
			break;
			// action with ID action_settings was selected
		case R.id.action_connect:
			connectionItem = item;
			if (!mConnection.isConnected()){
				connect();
				connectionItem.setTitle("Loading...");
			} else {
				mConnection.disconnect();
				item.setTitle(getResources().getString(R.string.action_connect1));
			}
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		//settings = (MenuItem)findViewById(R.id.action_settings);

		//connect = (MenuItem)findViewById(R.id.action_connect);
		return true;
	}

	private static class MyEvent1 {

		public int auction;
		public int amount;
		public String bidder;

		@Override
		public String toString() {
			return "{auction: " + auction +
					", amount: " + amount +
					", bidder:" + bidder.toString() + "}";
		}
	}

}
