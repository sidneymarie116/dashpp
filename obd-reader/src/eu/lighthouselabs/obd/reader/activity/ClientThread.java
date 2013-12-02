package eu.lighthouselabs.obd.reader.activity;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import eu.lighthouselabs.obd.reader.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ClientThread extends Service 
{
	private static final int SERVERPORT = 1035;
	private static final String SERVER_IP = "192.168.42.1";
	
	private boolean stopped = false;
	private Thread serverThread; 
	private ServerSocket ss;
	private Socket socket;
	
	@Override 
	public IBinder onBind (Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate () 
	{
		Log.d(getClass().getSimpleName(), "onCreate");
		serverThread = new Thread (new Runnable() {
			public void run()
			{
				Looper.prepare();
				InetAddress serverAddress = null;
				try {
					serverAddress = InetAddress.getByName(SERVER_IP);
				} catch (UnknownHostException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
				try {
					socket = new Socket(serverAddress, SERVERPORT);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				try {
					socket.setReuseAddress(true);
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				socket.setPerformancePreferences(100, 100, 1);
				try {
					socket.setKeepAlive(true);
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				DataInputStream _in = null;
				
				try {
					_in = new DataInputStream(new BufferedInputStream(socket.getInputStream(), 1024));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				while (!stopped)
				{		
					int method = 0;
					try {
						method = _in.read();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					switch (method) 
					{
					case 1: 
						try {
							doNotification(_in);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
				}
				
				try { ss.close(); }
				catch (IOException e) { Log.e(getClass().getSimpleName(), "Keep it simple"); }
			}
		}, "Server Thread");
		serverThread.start();
	}
	
	private void doNotification (DataInputStream in) throws IOException 
	{
		String id = in.readUTF();
		displayNotification(id);
	}
	
	@Override 
	public void onDestroy () 
	{
		stopped = true;
		try { ss.close(); } 
		catch (IOException e) {}
		serverThread.interrupt();
		try { serverThread.join(); }
		catch (InterruptedException e) {}
	}
	
	public void displayNotification (String notificationString) 
	{
		int icon = R.drawable.stop;
		CharSequence tickerText = notificationString;
		long when = System.currentTimeMillis();
		Context context = getApplicationContext();
		CharSequence contentTitle = notificationString;
		CharSequence contentText = "Hello World!";
		
		Intent notificationIntent = new Intent (this, MainMenuActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		Notification notification = new Notification (icon, tickerText, when);
		notification.vibrate = new long[] {0,100,200,300};
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		
		mNotificationManager.notify(1, notification);
	}
}

	
/*
public class ClientThread extends Activity {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
			super.onCreate (savedInstanceState);
			setContentView (R.layout.client);
			
			final Button switchMain = (Button) findViewById (R.id.mainCollisionButton);
			switchMain.setOnClickListener (new View.OnClickListener() {
				
				@Override
				public void onClick (View v) {
					// TODO Auto-generated method stub
					Intent act = new Intent (v.getContext(), MainMenuActivity.class);
					startActivity (act);
				}
			});
	}
}
*/
