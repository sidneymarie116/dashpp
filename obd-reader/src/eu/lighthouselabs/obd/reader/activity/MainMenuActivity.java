package eu.lighthouselabs.obd.reader.activity;

import java.net.Socket;

import eu.lighthouselabs.obd.reader.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends Activity {
	
	// -- Camera connection variables -- //
	//static Socket socket;
	//static final int SERVERPORT = 1035;
	//static final String SERVER_IP = "192.168.42.1";
	
		@Override
		protected void onCreate (Bundle savedInstanceState) {
				super.onCreate (savedInstanceState);
				setContentView (R.layout.main_menu);
				// -- Camera connection -- //
				if (ClientThread.instance == null) {
					Intent srv = new Intent(this, ClientThread.class);
					startService(srv);
				}
				
				final Button switchActOne = (Button) findViewById (R.id.obdButton);
				switchActOne.setOnClickListener (new View.OnClickListener() {
					
					@Override
					public void onClick (View v) {
						// TODO Auto-generated method stub
						Intent act = new Intent (v.getContext(), MainActivity.class);
						((Button)v).setBackgroundColor(Color.WHITE);
						startActivity (act);
					}
				});
				
				final Button switchActTwo = (Button) findViewById (R.id.streamButton);
				switchActTwo.setOnClickListener (new View.OnClickListener() {
					
					@Override
					public void onClick (View v) {
						// TODO Auto-generated method stub
						Intent act = new Intent (v.getContext(), StreamActivity.class);
						((Button)v).setBackgroundColor(Color.WHITE);
						startActivity (act);
					}
				});
				
				final Button switchActThree = (Button) findViewById (R.id.alertButton);
				switchActThree.setOnClickListener (new View.OnClickListener() {
					
					@Override
					public void onClick (View v) {
						// TODO Auto-generated method stub
						Intent act = new Intent (v.getContext(), AlertActivity.class);
						((Button)v).setBackgroundColor(Color.WHITE);
						startActivity (act);
					}
				});
				
				final Button switchActFour = (Button) findViewById (R.id.collisionButton);
				switchActFour.setOnClickListener (new View.OnClickListener() {
					
					@Override
					public void onClick (View v) {
						// TODO Auto-generated method stub
						Intent act = new Intent (v.getContext(), ClientThread.class);
						((Button)v).setBackgroundColor(Color.WHITE);
						startActivity (act);
					}
				});
				
				final Button switchActFive = (Button) findViewById (R.id.settingsButton);
				switchActFive.setOnClickListener (new View.OnClickListener() {
					
					@Override
					public void onClick (View v) {
						// TODO Auto-generated method stub
						Intent act = new Intent (v.getContext(), ProfileActivity.class);
						((Button)v).setBackgroundColor(Color.WHITE);
						startActivity (act);
					}
				});
				
				final Button switchActSix = (Button) findViewById (R.id.usageButton);
				switchActSix.setOnClickListener (new View.OnClickListener() {
					
					@Override
					public void onClick (View v) {
						// TODO Auto-generated method stub
						Intent act = new Intent (v.getContext(), UsageActivity.class);
						((Button)v).setBackgroundColor(Color.WHITE);
						startActivity (act);
					}
				});
		}
}
