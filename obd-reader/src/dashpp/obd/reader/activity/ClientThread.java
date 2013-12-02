package dashpp.obd.reader.activity;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.widget.Toast;
import dashpp.obd.reader.R;

public class ClientThread extends Service implements OnInitListener
{
	private static Bitmap largeIcon;
	private static TextToSpeech myTTS;
	private static final int SERVERPORT = 1035;
	private static final String SERVER_IP = "192.168.42.1";
	public static ClientThread instance;
	
	private boolean stopped = false;
	private Thread serverThread; 
	private ServerSocket ss;
	private Socket socket;
	
	@Override 
	public IBinder onBind (Intent intent) {
		System.out.println("BLAH BLAH BLAH");
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			if (largeIcon == null) {
				largeIcon = drawableToBitmap(getResources().getDrawable( R.drawable.icon ));
			}
			holder();
			if (myTTS == null) {
				myTTS = new TextToSpeech(this, this);
			}
		}catch(Exception e) {
			e.printStackTrace();
			displayNotification("Dash++ Error", e.getMessage(), false, false);
		}
		return 0;
	}

	@Override
	public void onCreate() 
	{
		super.onCreate();
	}
	
	private void holder() {
		if (instance == null) {
			instance = this;
		}
		Log.d(getClass().getSimpleName(), "holder");
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
					displayNotification("Dash++ Error", "Failed to connect to camera", false, false);
					return;
				}
				try {
					socket = new Socket(serverAddress, SERVERPORT);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
					displayNotification("Dash++ Error", "Failed to connect to camera", false, false);
					return;
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
					displayNotification("Dash++ Error", "Failed to read from camera", false, false);
					return;
				}
				long before = System.currentTimeMillis();
				long after = 0;
				while (!stopped)
				{
					try {
							String id = _in.readLine();
							if (id == null) {
								stopped = true;
							}
							if (Math.abs(before - after) > 5000 && (id.equalsIgnoreCase("stop") || id.equalsIgnoreCase("stop\n"))) {
								displayNotification("Dash++", "There is a stop sign approaching", true, true);
								if (after == 0 ) {
									after = System.currentTimeMillis();
								}
								before = after;
						} else {
							Thread.sleep(100);
						}
						after = System.currentTimeMillis();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				try { ss.close(); }
				catch (IOException e) { Log.e(getClass().getSimpleName(), "Keep it simple"); }
			}
		}, "Server Thread");
		serverThread.start();
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
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void displayNotification(String notificationTitle,String notificationString, boolean selfDestruct, boolean sayOutloud) 
	{
		int icon = R.drawable.stop;
		if (largeIcon == null) {
			largeIcon = drawableToBitmap(getResources().getDrawable( icon ));
		}
		CharSequence tickerText = notificationString;
		long when = System.currentTimeMillis();
		Context context = getApplicationContext();
		CharSequence contentTitle = notificationTitle;
		CharSequence contentText = notificationString;

		Intent notificationIntent = new Intent (this, MainMenuActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		Notification notification = new Notification.Builder(context)
         .setContentTitle(contentTitle)
         .setContentText(contentText)
         .setSmallIcon(icon)
         .setLargeIcon(largeIcon)
         //.setContentIntent(contentIntent)
         .setVibrate(new long[] {0,100,200,300})
         //.setLargeIcon(aBitmap)
         .getNotification();

		String ns = Context.NOTIFICATION_SERVICE;
		final NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		if (sayOutloud) {
			speak(notificationString);
		}
		mNotificationManager.notify(1, notification);
		if (selfDestruct) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mNotificationManager.cancelAll();
				}
				
			}).start();
		}
	}
	
	public void speak(String toSpeak) {
		if (myTTS != null) {
			myTTS.speak (toSpeak, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	public static Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}

	// setup TTS
	public void onInit(int initStatus) {
        	//check for successful instantiation
			if (initStatus == TextToSpeech.SUCCESS) {
				if (myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
						myTTS.setLanguage(Locale.US);
			}
			else if (initStatus == TextToSpeech.ERROR) {
				Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
			}
	}
}
