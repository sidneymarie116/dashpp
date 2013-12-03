package dashpp.obd.reader.activity;

import dashpp.obd.reader.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import android.widget.Toast;

public class AlertActivity extends Activity {
	
	public static int MONITOR = 100; // 0 | 50 | 100
	
	static final int TABLE_ROW_MARGIN = 7;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) 
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.alert);
		
		try {
			makeProfile();
		} catch (FileNotFoundException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void makeProfile() throws FileNotFoundException 
	{				
		// read global average from memory
		File myFile = new File("/sdcard/globally.txt");
		FileInputStream fIn = new FileInputStream(myFile);
		BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
		String myStrings[] = new String[12];
		
		try 
		{
			String aDataRow = "";
			for (int i = 0; (aDataRow = myReader.readLine()) != null; i++)
				myStrings[i] = aDataRow;
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		finally 
		{
			try {
				myReader.close();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	
		int throttle_oor = (int) Double.parseDouble(myStrings[10]);
		int speed_oor = (int) Double.parseDouble(myStrings[11]);
		boolean bad = false;
		
		if (throttle_oor > 5) {
			//you seem to be accelerating quickly, try letting up on that gas pedal
			//a bit
			updateTextView("You seem to be accelerating quickly, try letting up on that gas pedal!");
			MONITOR = 75;
			/*
			LinearLayout lView = new LinearLayout(this);
			TextView myText = new TextView(this);
			myText.setText("You seem to be accelerating quickly, try letting up on that gas pedal!");
			lView.addView(myText);
			setContentView(lView);
			*/
			
			bad = true;
		} else {
			//you're doing okay!!  do nothing.
		}
		
		if (speed_oor > 10) {
			//SLOW DOWN THERE BUDDY!!!!
			updateTextView("Slow down there!  Your speed has been over 80 mph too often lately..");
			MONITOR = 50;
			/*
			LinearLayout lView = new LinearLayout(this);
			TextView myText = new TextView(this);
			myText.setText("Slow down there!  Your speed has been over 80 mph too often lately..");
			lView.addView(myText);
			setContentView(lView);
			*/
			
			bad = true;
		} else {
			//you're doing okay!  do nothing
		}
		
		if (bad == false) {
			//Doing well driver, you go you!!!!
			updateTextView("Keep up the safe driving. You deserve an award!");
			MONITOR = 100;
			/*
			LinearLayout lView = new LinearLayout(this);
			TextView myText = new TextView(this);
			myText.setText("Keep up the good work!!!!");
			lView.addView(myText);
			setContentView(lView);
			*/
		}
		
		//anything else we can analyze here?!
	}
	
	public void updateTextView(String toThis) {

	    TextView textView = (TextView) findViewById(R.id.alert_text);
	    textView.setText(toThis);

	    return;
	}

}