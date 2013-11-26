package eu.lighthouselabs.obd.reader.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import eu.lighthouselabs.obd.reader.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class SettingsActivity extends Activity {
	
	static final int TABLE_ROW_MARGIN = 7;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) 
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.settings);
					
		//format is as follows:
		//Ambient Air Temperature,Engine RPM,Mass Air Flow,Fuel Level,Long Term Fuel Trim Bank 1,
		//Throttle Position,Trouble Codes,Vehicle Speed,Fuel Consumption,Fuel Economy,Fuel Economy
		/*
		data[0] = "Ambient Air Temperature,",20C
		data[1] = "Engine RPM,",1741 RPM
		data[2] = "Mass Air Flow,",11.33g/s
		data[3] = "Fuel Level,",0.0%
		data[4] = "Long Term Fuel Trim Bank 1,",0.00%
		data[5] = "Throttle Position,",14.9%
		data[6] = "Trouble Codes,",
		data[7] = "Vehicle Speed,",23km/h
		data[8] = "Fuel Consumption,",-1.0
		data[9] = "Fuel Economy,",-235.2
		*/

		//keep a table of global averages
		/* Global averages */
		double air_temp = 0;
		double engine_rpm = 0;
		double maf = 0;
		double fuel_level = 0;
		double ltft1 = 0;
		double throt = 0;
		double troub = 0;
		double speed = 0;
		double fuel_cons = 0;
		double fuel_econ = 0;
		int num_entries = 0;

		/* Trip averages */
		double air_temp_trip = 0;
		double engine_rpm_trip = 0;
		double maf_trip = 0;
		double fuel_level_trip = 0;
		double ltft1_trip = 0;
		double throt_trip = 0;
		double troub_trip = 0;
		double speed_trip = 0;
		double fuel_cons_trip = 0;
		double fuel_econ_trip = 0;
		int num_entries_trip = 0;     //or use some variation of num_entries
		
		String s0 = "", s1 = "", s2 = "", s3 = "", s4 = "", s5 = "", s6 = "", s7 = "", s8 = "", s9 = "";
		//int num_entries_trip = 0;   //or use some variation of num_entries

		/* open the CSV file */
		String csvFile = "/sdcard/output_20_9.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
	
		/* Parse the CSV file based on expected input */
	    try {
	    	boolean first_line = true;
	        br = new BufferedReader(new FileReader(csvFile));
	        String[] RowData = null;
	        Toast.makeText(getApplicationContext(), "Read in file", Toast.LENGTH_LONG).show();
	        
	        while ((line = br.readLine()) != null) {
	        	if(!first_line) {
	        		RowData = line.split(cvsSplitBy);
	        		s0 = RowData[0];
	        		s0 = s0.substring(1, s0.length()-1);
	        		air_temp = Double.parseDouble(s0);
	        		air_temp_trip += air_temp;
	        		s1 = RowData[1];
	        		s1 = s1.substring(2, s1.length()-4);
	        		engine_rpm = Double.parseDouble(s1);
	        		engine_rpm_trip += engine_rpm;
	        		s2 = RowData[2];
	        		s2 = s2.substring(2, s2.length()-3);
	        		maf = Double.parseDouble(s2);
	        		maf_trip += maf;
	        		s3 = RowData[3];
	        		s3 = s3.substring(2, s3.length()-1);
	        		fuel_level = Double.parseDouble(s3);
	        		fuel_level_trip += fuel_level;
	        		s4 = RowData[4];
	        		s4 = s4.substring(2, s4.length()-1);
	        		ltft1 = Double.parseDouble(s4);
	        		ltft1_trip += ltft1;
	        		s5 = RowData[5];
	        		s5 = s5.substring(2, s5.length()-1);
	        		throt = Double.parseDouble(s5);
	        		throt_trip += throt;
	        		s6 = RowData[6];
	        		s6 = s6.substring(2, s6.length());
	        		//troub_trip = Double.parseDouble(s6);
	        		//
	        		s7 = RowData[7];
	        		s7 = s7.substring(2, s7.length()-4);
	        		speed = Double.parseDouble(s7);
	        		speed_trip += speed;
	        		s8 = RowData[8];
	        		s8 = s8.substring(2, s8.length());
	        		fuel_cons = Double.parseDouble(s8);
	        		fuel_cons_trip += fuel_cons;
	        		s9 = RowData[9];
	        		s9 = s9.substring(2, s9.length());
	        		fuel_econ = Double.parseDouble(s9);
	        		fuel_econ_trip += fuel_econ;
	        		
	        		//now increment num_entries;
	        		num_entries_trip++;
	        	}
	        	if(first_line) {
	        		RowData = line.split(cvsSplitBy);
	        		//String test = RowData[0];
	        		//Toast.makeText(getApplicationContext(), "" + test, Toast.LENGTH_LONG).show();
	        		first_line = false;
	        	}
			}      
	        
			} catch (FileNotFoundException e) {
				Toast.makeText(getApplicationContext(), "Exception", Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "IOException", Toast.LENGTH_LONG).show();
			} finally {
				if (br != null) {
					try {
						br.close();
						Toast.makeText(getApplicationContext(), "Closing file", Toast.LENGTH_LONG).show();
						
				        //now average the values back out
				        air_temp_trip = air_temp_trip / num_entries_trip;
				        engine_rpm_trip = engine_rpm_trip / num_entries_trip;
				        maf_trip = maf_trip / num_entries_trip;
				        fuel_level_trip = fuel_level_trip / num_entries_trip;
				        ltft1_trip = ltft1_trip / num_entries_trip;
				        throt_trip = throt_trip / num_entries_trip;
				        //nothing for troub
				        speed_trip = speed_trip / num_entries_trip;
				        fuel_cons_trip = fuel_cons_trip / num_entries_trip;
				        fuel_econ_trip = fuel_econ_trip / num_entries_trip;
				        
				        //num_entries += num_entries_trip;
				        /*air_temp += (air_temp + air_temp_trip)/2;
				        engine_rpm += (engine_rpm + fuel_econ_trip)/2;
				        maf += (maf + maf_trip)/2;
				        fuel_level += (fuel_level + fuel_level_trip)/2;
				        ltft1 += (ltft1 + ltft1_trip)/2;
				        throt += (throt + throt_trip)/2;
				        //nothing for troub
				        speed += (speed + speed_trip)/2;
				        fuel_cons += (fuel_cons + fuel_cons_trip)/2;
				        fuel_econ += (fuel_econ + fuel_econ_trip)/2;*/
				        
				        //update averages table!
				        addTableRow("Air Temp", air_temp_trip);
				        addTableRow("MAF", maf_trip);
				        addTableRow("Fuel Level", fuel_level_trip);
				        addTableRow("LTFT1", ltft1_trip);
				        addTableRow("Throttle Position", throt_trip);
				        addTableRow("Speed", speed_trip);
				        addTableRow("Fuel Consumption", fuel_cons_trip);
				        addTableRow("Fuel Economy", fuel_econ_trip);
						
					} catch (IOException e) {
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "IOException2", Toast.LENGTH_LONG).show();
					}
				}
			}
		    
	    	//Toast.makeText(getApplicationContext(), "Done with array", Toast.LENGTH_LONG).show();
	    
		    //now to decide what to do with the data.  i need some ranges or something
		    //look at RPM

		    //look at throttle position (if any of these values are outside of x this is an issue)

		    //look at speed --> if any of these are outside of 75 this is an issue

		    //look at fuel_economy
		
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
	    
	    private void addTableRow (String key, double val) {
			TableLayout tl = (TableLayout) findViewById(R.id.averages_table);
			TableRow tr = new TableRow(this);
			MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(TABLE_ROW_MARGIN, TABLE_ROW_MARGIN, TABLE_ROW_MARGIN,
					TABLE_ROW_MARGIN);
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
			
			tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			if (tl.getChildCount() > 10)
				tl.removeViewAt(0);
		}

}
