package dashpp.obd.reader.activity;

import dashpp.obd.reader.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.content.Intent;
import android.graphics.Color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;
import android.widget.Toast;

public class AlertActivity extends Activity {
	
	static final int TABLE_ROW_MARGIN = 7;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) 
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.profile);

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
		
		if(throttle_oor > 5) {
			//you seem to be accelerating quickly, try letting up on that gas pedal
			//a bit
			LinearLayout lView = new LinearLayout(this);
			TextView myText = new TextView(this);
			myText.setText("You seem to be accelerating quickly, try letting up on that gas pedal!");
			lView.addView(myText);
			setContentView(lView);
			
			bad = true;
		} else {
			//you're doing okay!!  do nothing.
		}
		
		if(speed_oor > 10) {
			//SLOW DOWN THERE BUDDY!!!!
			LinearLayout lView = new LinearLayout(this);
			TextView myText = new TextView(this);
			myText.setText("Slow down there!  Your speed has been over 80 mph too often lately..");
			lView.addView(myText);
			setContentView(lView);
			
			bad = true;
		} else {
			//you're doing okay!  do nothing
		}
		
		if (bad == false) {
			LinearLayout lView = new LinearLayout(this);
			TextView myText = new TextView(this);
			myText.setText("Keep up the good work!!!!");
			lView.addView(myText);
			setContentView(lView);
		}
		
		//anything else we can analyze here?!
	}

}