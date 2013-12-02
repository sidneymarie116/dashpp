package eu.lighthouselabs.obd.reader.activity;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import eu.lighthouselabs.obd.reader.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/*
 * TODO rename this activity 
 */
public class ProfileActivity extends Activity {
	
	static final int TABLE_ROW_MARGIN = 7;
	
	/* Trip averages */
	double air_temp_trip = 0;
	double engine_rpm_trip = 0;
	double maf_trip = 0;
	double fuel_level_trip = 0;
	double ltft1_trip = 0;
	double throt_trip = 0;
	// double troub_trip = 0;
	double speed_trip = 0;
	double fuel_cons_trip = 0;
	double fuel_econ_trip = 0;
	int num_entries_trip = 0;     // or use some variation of num_entries
	
	@Override
	protected void onCreate (Bundle savedInstanceState) 
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.profile);
		
		calculateTripAverages();
			
		try 
		{
			calculateGlobalAverages();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		/*
		 * TODO:
		 *  now to decide what to do with the data.  i need some ranges or something
		 *  look at RPM
		 *  look at throttle position (if any of these values are outside of x this is an issue)
		 *  look at speed --> if any of these are outside of 75 this is an issue
		 *  look at fuel_economy
		 */
		
    	final Button switchMain = (Button) findViewById (R.id.mainSettingsButton);
    	switchMain.setOnClickListener (new View.OnClickListener() {
			@Override
			public void onClick (View v) {
				// TODO Auto-generated method stub
				Intent act = new Intent (v.getContext(), MainMenuActivity.class);
				startActivity (act);
			}
    	});
	}
	
	private void calculateTripAverages() 
	{				
		double air_temp = 0;
		double engine_rpm = 0;
		double maf = 0;
		double fuel_level = 0;
		double ltft1 = 0;
		double throt = 0;
		// double troub = 0;
		double speed = 0;
		double fuel_cons = 0;
		double fuel_econ = 0;
		int num_entries = 0;
		
		//String s0 = "", s1 = "", s2 = "", s3 = "", s4 = "", s5 = "", s6 = "", s7 = "", s8 = "", s9 = "";
		int num_entries_trip = 0;   //or use some variation of num_entries

		// -- CSV Reader Implementation
		String csvFile = "/sdcard/formatted.csv";	
		CsvReader analytics = null;
		try 
		{
			analytics = new CsvReader(csvFile);
		} 
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
		}
		
		try {
			analytics.readHeaders();
			
			while (analytics.readRecord())
			{
				//now increment num_entries;
        		num_entries_trip++;
        		
				air_temp = Double.parseDouble(analytics.get("AirTemp")); // *
        		air_temp_trip += air_temp;

				engine_rpm = Double.parseDouble(analytics.get("EngineRPM")); // *
        		engine_rpm_trip += engine_rpm;

				maf = Double.parseDouble(analytics.get("MAF")); // *
        		maf_trip += maf;

				fuel_level = Double.parseDouble(analytics.get("FuelLevel")); // *
        		fuel_level_trip += fuel_level;

				ltft1 = Double.parseDouble(analytics.get("LTFT1")); // *
        		ltft1_trip += ltft1;

				throt = Double.parseDouble(analytics.get("Throttle")); // *
        		throt_trip += throt;
        		
        		// troub = Double.parseDouble(analytics.get("Trouble"));
        		// troub_trip += troub;

				speed = Double.parseDouble(analytics.get("Speed")); // *
        		speed_trip += speed;

				fuel_cons = Double.parseDouble(analytics.get("FuelCons")); // *
        		fuel_cons_trip += fuel_cons;
        		
				fuel_econ = Double.parseDouble(analytics.get("FuelEcon")); // *
        		fuel_econ_trip += fuel_econ;
			}			
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			if (analytics != null) 
			{
				analytics.close();
				
				/* calculate trip averages */
				air_temp_trip = air_temp_trip / num_entries_trip;
				engine_rpm_trip = engine_rpm_trip / num_entries_trip;
				maf_trip = maf_trip / num_entries_trip;
				fuel_level_trip = fuel_level_trip / num_entries_trip;
				ltft1_trip = ltft1_trip / num_entries_trip;
				throt_trip = throt_trip / num_entries_trip;
				speed_trip = speed_trip / num_entries_trip;
				fuel_cons_trip = fuel_cons_trip / num_entries_trip;
				fuel_econ_trip = fuel_econ_trip / num_entries_trip;

								        
				//update averages table!
				addTableRow("Air Temp", air_temp_trip);
				addTableRow("Engine RPM", engine_rpm);
				addTableRow("MAF", maf_trip);
				addTableRow("Fuel Level", fuel_level_trip);
				addTableRow("LTFT1", ltft1_trip);
				addTableRow("Throttle Position", throt_trip);
				//addTableRow("Trouble", troub_trip);
				addTableRow("Speed", speed_trip);
				addTableRow("Fuel Consumption", fuel_cons_trip);
				addTableRow("Fuel Economy", fuel_econ_trip);
			}
		}
	}
		
	private void calculateGlobalAverages() throws IOException {		
		//array of doubles to store global averages
		double air_temp = 0;
		double engine_rpm = 0;
		double maf = 0;
		double fuel_level = 0;
		double ltft1 = 0;
		double throt = 0;
		// double troub = 0;
		double speed = 0;
		double fuel_cons = 0;
		double fuel_econ = 0;
		int num_entries = 0;
		double weighted_avg_trip = 0;
		double weighted_avg_tot = 0;
		
		ArrayList<String> myNums = new ArrayList<String>();
		
		//read global average from memory
		File myFile = new File("/sdcard/globally.txt");
		FileInputStream fIn = new FileInputStream(myFile);
		BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
		String myStrings[] = new String [10];
		
		try 
		{
			String aDataRow = "";
			String aBuffer = "";
			for (int i = 0; (aDataRow = myReader.readLine()) != null; i++)
				myStrings[i] = aDataRow;
		}
		finally 
		{
			myReader.close();
		}
		
		num_entries = (int) Double.parseDouble(myStrings[0]);
		air_temp = Double.parseDouble(myStrings[1]); 
		engine_rpm = Double.parseDouble(myStrings[2]); 
		maf = Double.parseDouble(myStrings[3]); 
		fuel_level = Double.parseDouble(myStrings[4]); 
		ltft1 = Double.parseDouble(myStrings[5]); 
		throt = Double.parseDouble(myStrings[6]); 
		// troub = Double.parseDouble(myStrings[7]); 
		speed = Double.parseDouble(myStrings[7]); 
		fuel_cons = Double.parseDouble(myStrings[8]); 
		fuel_econ = Double.parseDouble(myStrings[9]); 

		
		weighted_avg_trip = 0;
		weighted_avg_tot = 0;
		
		//calculate weights
		weighted_avg_trip = num_entries_trip/(num_entries + num_entries_trip);
		weighted_avg_tot = 1 - weighted_avg_trip;
		
		// use weight to calculate weighted averages
		air_temp = (air_temp*weighted_avg_tot) + (air_temp_trip*weighted_avg_trip);
		engine_rpm = (engine_rpm*weighted_avg_tot) + (engine_rpm_trip*weighted_avg_trip);
		maf = (maf*weighted_avg_tot) + (maf_trip*weighted_avg_trip);
		fuel_level = (fuel_level*weighted_avg_tot) + (fuel_level_trip*weighted_avg_trip);
		ltft1 = (ltft1*weighted_avg_tot) + (ltft1_trip*weighted_avg_trip);
		throt = (throt*weighted_avg_tot) + (throt_trip*weighted_avg_trip);
		// troub = (troub*weighted_avg_tot) + (troub_trip*weighted_avg_trip);
		speed = (air_temp*weighted_avg_tot) + (speed_trip*weighted_avg_trip);
		fuel_cons = (fuel_cons*weighted_avg_tot) + (fuel_cons_trip*weighted_avg_trip);
		fuel_econ = (fuel_econ*weighted_avg_tot) + (fuel_econ_trip*weighted_avg_trip);
		num_entries += num_entries_trip;
		
        // update averages table!
        addTableRow("Avg. Air Temp", air_temp);
        addTableRow("Avg. MAF", maf);
        addTableRow("Avg. Fuel Level", fuel_level);
        addTableRow("Avg. LTFT1", ltft1);
        addTableRow("Avg. Throttle Position", throt);
        // addTableRow("Avg. Trouble", troub);
        addTableRow("Avg. Speed", speed);
        addTableRow("Avg. Fuel Consumption", fuel_cons);
        addTableRow("Avg. Fuel Economy", fuel_econ);
		
		//write global average back to memory
        myFile = new File("/sdcard/globally.txt");
		myFile.createNewFile();
		FileOutputStream fOut = new FileOutputStream(myFile);
		OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
		
        try 
        {
			myOutWriter.write(num_entries + "\n");
			myOutWriter.write(air_temp + "\n");
			myOutWriter.write(engine_rpm + "\n");
			myOutWriter.write(maf + "\n");
			myOutWriter.write(fuel_level + "\n");
			myOutWriter.write(ltft1 + "\n");
			myOutWriter.write(throt + "\n");
		    // myOutWriter.write(0 + "\n");
		    myOutWriter.write(speed + "\n");
		    myOutWriter.write(fuel_cons + "\n");
		    myOutWriter.write(fuel_econ + "\n");

        }
        finally 
        {
		    myOutWriter.flush();
			myOutWriter.close();
			fOut.close();
        }
	}
    
    private void addTableRow (String key, double val) 
    {
		TableLayout tl = (TableLayout) findViewById(R.id.averages_table);
		TableRow tr = new TableRow(this);
		MarginLayoutParams params = new ViewGroup.MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(TABLE_ROW_MARGIN, TABLE_ROW_MARGIN, TABLE_ROW_MARGIN, TABLE_ROW_MARGIN);
		tr.setLayoutParams(params);
		tr.setBackgroundColor(Color.BLACK);
		TextView name = new TextView(this);
		name.setGravity(Gravity.RIGHT);
		name.setText(key + ": ");
		TextView value = new TextView(this);
		value.setGravity(Gravity.LEFT);
		value.setText(Double.toString(val));
		tr.addView(name);
        tr.addView(value);
		
		tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		if (tl.getChildCount() > 20)
			tl.removeViewAt(0);
	}

}
