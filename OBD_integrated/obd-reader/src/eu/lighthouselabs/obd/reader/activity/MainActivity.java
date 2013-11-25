/*
 * TODO put header
 */
package eu.lighthouselabs.obd.reader.activity;

import java.io.BufferedWriter;
import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
//import eu.lighthouselabs.obd.commands.ObdBaseCommand;
import eu.lighthouselabs.obd.commands.SpeedObdCommand;
import eu.lighthouselabs.obd.commands.control.CommandEquivRatioObdCommand;
import eu.lighthouselabs.obd.commands.control.TroubleCodesObdCommand;
import eu.lighthouselabs.obd.commands.engine.EngineRPMObdCommand;
import eu.lighthouselabs.obd.commands.engine.MassAirFlowObdCommand;
import eu.lighthouselabs.obd.commands.engine.ThrottlePositionObdCommand;
import eu.lighthouselabs.obd.commands.fuel.FuelConsumptionObdCommand;
import eu.lighthouselabs.obd.commands.fuel.FuelEconomyObdCommand;
import eu.lighthouselabs.obd.commands.fuel.FuelEconomyWithMAFObdCommand;
import eu.lighthouselabs.obd.commands.fuel.FuelLevelObdCommand;
import eu.lighthouselabs.obd.commands.fuel.FuelTrimObdCommand;
import eu.lighthouselabs.obd.commands.temperature.AmbientAirTemperatureObdCommand;
import eu.lighthouselabs.obd.enums.AvailableCommandNames;
import eu.lighthouselabs.obd.enums.FuelTrim;
import eu.lighthouselabs.obd.enums.FuelType;
import eu.lighthouselabs.obd.reader.IPostListener;
import eu.lighthouselabs.obd.reader.R;
import eu.lighthouselabs.obd.reader.io.ObdCommandJob;
import eu.lighthouselabs.obd.reader.io.ObdGatewayService;
import eu.lighthouselabs.obd.reader.io.ObdGatewayServiceConnection;

/**
 * The main activity.
 */

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	/*
	 * TODO put description
	 */
	static final int NO_BLUETOOTH_ID = 0;
	static final int BLUETOOTH_DISABLED = 1;
	static final int NO_GPS_ID = 2;
	static final int START_LIVE_DATA = 3;
	static final int STOP_LIVE_DATA = 4;
	static final int SETTINGS = 5;
	static final int COMMAND_ACTIVITY = 6;
	static final int TABLE_ROW_MARGIN = 7;
	static final int NO_ORIENTATION_SENSOR = 8;

	private Handler mHandler = new Handler();

	/**
	 * Callback for ObdGatewayService to update UI.
	 */
	private IPostListener mListener = null;
	private Intent mServiceIntent = null;
	private ObdGatewayServiceConnection mServiceConnection = null;

	private SensorManager sensorManager = null;
	private Sensor orientSensor = null;
	private SharedPreferences prefs = null;

	private PowerManager powerManager = null;
	private PowerManager.WakeLock wakeLock = null;

	private boolean preRequisites = true;

	private int speed = 1;
	private double maf = 1;
	private String mpg1 = "";
	private float ltft = 1;
	private double equivRatio = 1;
	private String throt = "";
	private String troub = "";
	private String air = "";
	private String fuel = "";
	private boolean air_b = false;
	
	int num_entries_trip = 0;
	
	//***************************************//
	ArrayList<String> validCommands = new ArrayList<String>();

	private final SensorEventListener orientListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent event) {
			float x = event.values[0];
			String dir = "";
			if (x >= 337.5 || x < 22.5) {
				dir = "N";
			} else if (x >= 22.5 && x < 67.5) {
				dir = "NE";
			} else if (x >= 67.5 && x < 112.5) {
				dir = "E";
			} else if (x >= 112.5 && x < 157.5) {
				dir = "SE";
			} else if (x >= 157.5 && x < 202.5) {
				dir = "S";
			} else if (x >= 202.5 && x < 247.5) {
				dir = "SW";
			} else if (x >= 247.5 && x < 292.5) {
				dir = "W";
			} else if (x >= 292.5 && x < 337.5) {
				dir = "NW";
			}
			TextView compass = (TextView) findViewById(R.id.compass_text);
			updateTextView(compass, dir);
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}
	};

	public void updateTextView(final TextView view, final String txt) {
		new Handler().post(new Runnable() {
			public void run() {
				view.setText(txt);
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    
		/*
		 * TODO clean-up this upload thing
		 * 
		 * ExceptionHandler.register(this,
		 */
		setContentView(R.layout.main);
		
		// -- Button to return to Main -- //
		
		final Button switchMain = (Button) findViewById (R.id.mainOBDButton);
		switchMain.setOnClickListener (new View.OnClickListener() {
			
			@Override
			public void onClick (View v) {
				// TODO Auto-generated method stub
				Intent act = new Intent (v.getContext(), MainMenuActivity.class);
				startActivity (act);
			}
		});

		mListener = new IPostListener() {
			public void stateUpdate(ObdCommandJob job) {
				
				//debugging 
				/*try {
					Toast.makeText(getApplicationContext(), "start cmds section", Toast.LENGTH_SHORT).show();
				}
				catch (Exception e) {
					Toast.makeText(getApplicationContext(), "FAIL start cmds section", Toast.LENGTH_SHORT).show();
				}*/
				
				String cmdName = job.getCommand().getName();
				String cmdResult = job.getCommand().getFormattedResult();

				Log.d(TAG, FuelTrim.LONG_TERM_BANK_1.getBank() + " equals " + cmdName + "?");
				
				//-------------------------- other commands --------------------------
				/*ArrayList<ObdBaseCommand> cmds = ConfigActivity.getObdCommands(prefs);
				String outputcmds = "\n";
				for (int i=0; i<cmds.size(); i++)
				{
					outputcmds += (cmds.get(i) + "\n");
				}
				String myFile = "/sdcard/validcommands.txt";
				//boolean exists = new File(csv).exists();
				
				try {
					PrintWriter writer = new PrintWriter("/sdcard/validcommands.txt", "UTF-8");
					writer.println(outputcmds);
					writer.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}*/
				//--------------------------- end other commands --------------------------
				/*validCommands.add(airTemp.getCommand().getName());
				validCommands.add(speed.getCommand().getName());
				validCommands.add(fuelEcon.getCommand().getName());
				validCommands.add(rpm.getCommand().getName());
				validCommands.add(maf.getCommand().getName());
				validCommands.add(fuelLevel.getCommand().getName());
				validCommands.add(ltft1.getCommand().getName());
				//validCommands.add(ltft2.getCommand().getName());
				//validCommands.add(stft1.getCommand().getName());
				//validCommands.add(stft2.getCommand().getName());
				validCommands.add(throt.getCommand().getName());
				validCommands.add(troub.getCommand().getName());*/
				
				if (AvailableCommandNames.ENGINE_RPM.getValue().equals(cmdName)) 
				{
					TextView tvRpm = (TextView) findViewById(R.id.rpm_text);
					tvRpm.setText(cmdResult);
					//new from sid
					//cmdResult = cmdResult.substring(1 , cmdResult.length()-5);
					addTableRow(cmdName, cmdResult);
				} 
				else if (AvailableCommandNames.SPEED.getValue().equals(cmdName)) 
				{
					TextView tvSpeed = (TextView) findViewById(R.id.spd_text);
					tvSpeed.setText(cmdResult);
					speed = ((SpeedObdCommand) job.getCommand()).getMetricSpeed();
					//new from sid
					//cmdResult = cmdResult.substring(1 , cmdResult.length()-5);
					addTableRow(cmdName, cmdResult);
				} 
				else if (AvailableCommandNames.MAF.getValue().equals(cmdName)) 
				{
					maf = ((MassAirFlowObdCommand) job.getCommand()).getMAF();
					//cmdResult = cmdResult.substring(1 , cmdResult.length()-4);
					addTableRow(cmdName, cmdResult);
				} 
				else if (FuelTrim.LONG_TERM_BANK_1.getBank().equals(cmdName)) 
				{
					ltft = ((FuelTrimObdCommand) job.getCommand()).getValue();
					//cmdResult = cmdResult.substring(1 , cmdResult.length()-2);
					addTableRow(cmdName, cmdResult);
				} 
				else if (AvailableCommandNames.FUEL_ECONOMY.getValue().equals(cmdName)) 
				{
					mpg1 = ((FuelEconomyObdCommand) job.getCommand()).getFormattedResult();
					float mpg2 = ((FuelEconomyObdCommand) job.getCommand()).getMilesPerUSGallon();
					//float mpg3 = ((FuelEconomyObdCommand) job.getCommand()).
					//cmdResult = cmdResult.substring(1 , cmdResult.length()-1);
					addTableRow(cmdName, cmdResult);
					//addTableRow(cmdName, mpg2 + "");
				} 
				else if (AvailableCommandNames.THROTTLE_POS.getValue().equals(cmdName)) 
				{
					throt = ((ThrottlePositionObdCommand) job.getCommand()).getFormattedResult();
					//cmdResult = cmdResult.substring(1 , cmdResult.length()-2);
					addTableRow(cmdName, cmdResult);
				} 
				else if (AvailableCommandNames.TROUBLE_CODES.getValue().equals(cmdName))
				{
					troub = ((TroubleCodesObdCommand) job.getCommand()).getFormattedResult();
					addTableRow(cmdName, cmdResult);
				}
				else if (AvailableCommandNames.AMBIENT_AIR_TEMP.getValue().equals(cmdName))
				{
					if(air_b == false)
						air_b = true;
					else
					{
						air = ((AmbientAirTemperatureObdCommand) job.getCommand()).getFormattedResult();
						//cmdResult = cmdResult.substring(1, cmdResult.length()-2);
						addTableRow(cmdName, cmdResult);
					}
				}
				else if (AvailableCommandNames.FUEL_LEVEL.getValue().equals(cmdName))
				{
					fuel = ((FuelLevelObdCommand) job.getCommand()).getFormattedResult();
					//cmdResult = cmdResult.substring(1 , cmdResult.length()-1);
					addTableRow(cmdName, cmdResult);
				}
				else if (AvailableCommandNames.FUEL_CONSUMPTION.getValue().equals(cmdName)) 
				{
					String fuel_consumption = ((FuelConsumptionObdCommand) job.getCommand()).getFormattedResult();
					//cmdResult = cmdResult.substring(1 , cmdResult.length()-1);
					addTableRow(cmdName, fuel_consumption);
				} 
				else if (AvailableCommandNames.FUEL_ECONOMY_WITH_MAF.getValue().equals(cmdName)) 
				{
					//String fuel_econ_maf = ((FuelEconomyWithMAFObdCommand) job.getCommand()).getFormattedResult();
					//FuelType fuelType, int speed, double maf, float ltft, boolean useImperial
					FuelEconomyWithMAFObdCommand fuelEconCmd2 = new FuelEconomyWithMAFObdCommand(
							FuelType.DIESEL, speed, maf, ltft, true);
					String mpergallon = String.format("%.2f", fuelEconCmd2.getMPG());
					addTableRow(cmdName, mpergallon);
				}
				else 
				{
					//addTableRow(cmdName, cmdResult);
				}
			}
		};

		/*
		 * Validate GPS service.
		 */
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.getProvider(LocationManager.GPS_PROVIDER) == null) {
			/*
			 * TODO for testing purposes we'll not make GPS a pre-requisite.
			 */
			// preRequisites = false;
			showDialog(NO_GPS_ID);
		}

		/*
		 * Validate Bluetooth service.
		 */
		// Bluetooth device exists?
		final BluetoothAdapter mBtAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBtAdapter == null) {
			preRequisites = false;
			showDialog(NO_BLUETOOTH_ID);
		} else {
			// Bluetooth device is enabled?
			if (!mBtAdapter.isEnabled()) {
				preRequisites = false;
				showDialog(BLUETOOTH_DISABLED);
			}
		}

		/*
		 * Get Orientation sensor.
		 */
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sens = sensorManager
				.getSensorList(Sensor.TYPE_ORIENTATION);
		if (sens.size() <= 0) {
			showDialog(NO_ORIENTATION_SENSOR);
		} else {
			orientSensor = sens.get(0);
		}

		// validate app pre-requisites
		if (preRequisites) {
			/*
			 * Prepare service and its connection
			 */
			mServiceIntent = new Intent(this, ObdGatewayService.class);
			mServiceConnection = new ObdGatewayServiceConnection();
			mServiceConnection.setServiceListener(mListener);

			// bind service
			Log.d(TAG, "Binding service..");
			bindService(mServiceIntent, mServiceConnection,
					Context.BIND_AUTO_CREATE);
		}
		/*try {
			writer.flush();
			writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} */
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		releaseWakeLockIfHeld();
		mServiceIntent = null;
		mServiceConnection = null;
		mListener = null;
		mHandler = null;

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "Pausing..");
		releaseWakeLockIfHeld();
	}

	/**
	 * If lock is held, release. Lock will be held when the service is running.
	 */
	private void releaseWakeLockIfHeld() {
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}
	}

	protected void onResume() {
		super.onResume();

		Log.d(TAG, "Resuming..");

		sensorManager.registerListener(orientListener, orientSensor,
				SensorManager.SENSOR_DELAY_UI);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"ObdReader");
	}

	private void updateConfig() {
		Intent configIntent = new Intent(this, ConfigActivity.class);
		startActivity(configIntent);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, START_LIVE_DATA, 0, "Start Live Data");
		menu.add(0, COMMAND_ACTIVITY, 0, "Run Command");
		menu.add(0, STOP_LIVE_DATA, 0, "Stop");
		menu.add(0, SETTINGS, 0, "Settings");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case START_LIVE_DATA:
			startLiveData();
			return true;
		case STOP_LIVE_DATA:
			stopLiveData();
			return true;
		case SETTINGS:
			updateConfig();
			return true;
			// case COMMAND_ACTIVITY:
			// staticCommand();
			// return true;
		}
		return false;
	}

	// private void staticCommand() {
	// Intent commandIntent = new Intent(this, ObdReaderCommandActivity.class);
	// startActivity(commandIntent);
	// }

	private void startLiveData() {
		Log.d(TAG, "Starting live data..");

		if (!mServiceConnection.isRunning()) {
			Log.d(TAG, "Service is not running. Going to start it..");
			startService(mServiceIntent);
		}

		// start command execution
		mHandler.post(mQueueCommands);

		// screen won't turn off until wakeLock.release()
		wakeLock.acquire();
	}

	private void stopLiveData() {
		Log.d(TAG, "Stopping live data..");

		if (mServiceConnection.isRunning())
			stopService(mServiceIntent);

		// remove runnable
		mHandler.removeCallbacks(mQueueCommands);

		releaseWakeLockIfHeld();
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder build = new AlertDialog.Builder(this);
		switch (id) {
		case NO_BLUETOOTH_ID:
			build.setMessage("Sorry, your device doesn't support Bluetooth.");
			return build.create();
		case BLUETOOTH_DISABLED:
			build.setMessage("You have Bluetooth disabled. Please enable it!");
			return build.create();
		case NO_GPS_ID:
			build.setMessage("Sorry, your device doesn't support GPS.");
			return build.create();
		case NO_ORIENTATION_SENSOR:
			build.setMessage("Orientation sensor missing?");
			return build.create();
		}
		return null;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem startItem = menu.findItem(START_LIVE_DATA);
		MenuItem stopItem = menu.findItem(STOP_LIVE_DATA);
		MenuItem settingsItem = menu.findItem(SETTINGS);
		MenuItem commandItem = menu.findItem(COMMAND_ACTIVITY);

		// validate if preRequisites are satisfied.
		if (preRequisites) {
			if (mServiceConnection.isRunning()) {
				startItem.setEnabled(false);
				stopItem.setEnabled(true);
				settingsItem.setEnabled(false);
				commandItem.setEnabled(false);
			} else {
				stopItem.setEnabled(false);
				startItem.setEnabled(true);
				settingsItem.setEnabled(true);
				commandItem.setEnabled(false);
			}
		} else {
			startItem.setEnabled(false);
			stopItem.setEnabled(false);
			settingsItem.setEnabled(false);
			commandItem.setEnabled(false);
		}

		return true;
	}

	private void addTableRow(String key, String val) {
			TableLayout tl = (TableLayout) findViewById(R.id.data_table);
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
			value.setText(val);
			tr.addView(name);
			
			// -- try adding csv entry here -- //
			String csv = "/sdcard/output_20_8.csv";
			boolean exists = new File(csv).exists();
			
			try {
				CsvWriter writer = new CsvWriter (new FileWriter (csv, true), ',');
				
				if (!exists) {
					//list all of the validCommands
					//
					for(int i=0; i<validCommands.size(); i++)
					{
						writer.write(validCommands.get(i));
					}

					writer.endRecord();
				}
				
				writer.write(key + ",");
				writer.write(val);
				writer.endRecord();
				if(key.equals(validCommands.get(validCommands.size()-1)))
					writer.endRecord();
				
				writer.close();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
			
			// -- try adding csv entry here -- //
			String csv1 = "/sdcard/output_20_9.csv";
			boolean exists1 = new File(csv1).exists();
			
			try {
				CsvWriter writer1 = new CsvWriter (new FileWriter (csv1, true), ',');
				
				if (!exists1) {
					//list all of the validCommands
					//
					for(int i=0; i<validCommands.size(); i++)
					{
						writer1.write(validCommands.get(i));
					}

					writer1.endRecord();
				}
				
				//writer1.write(key + ",");
				writer1.write(val + ",");
				//writer1.endRecord();
				if(key.equals(validCommands.get(validCommands.size()-1)))
					writer1.endRecord();
				
				writer1.close();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
			
			/*
			 * String stest = (key + "\n" + value + "\n");
		     * try {
					FileWriter mynewwriter = new FileWriter("/sdcard/nov7_mynewwriter.csv");
					mynewwriter.append(stest);
					mynewwriter.flush();
					//mynewwriter.close();
			 * } catch(IOException e) {
					Toast.makeText(getApplicationContext(), "" +e.getMessage(), 100).show();
			 * }
				
			 * try {
					fw.append(stest);
			 * } catch(IOException e) {
					Toast.makeText(getApplicationContext(), "" +e.getMessage(), 100).show();
			 * } 
			 */
			
			tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			
			/*
			 * TODO remove this hack
			 * 
			 * let's define a limit number of rows
			 */
			if (tl.getChildCount() > 10)
				tl.removeViewAt(0);
	}
	
	/**
	 * 
	 */
	private Runnable mQueueCommands = new Runnable() {
		public void run() {
			/*
			 * If values are not default, then we have values to calculate MPG
			 */
			Log.d(TAG, "SPD:" + speed + ", MAF:" + maf + ", LTFT:" + ltft);
			//if (speed > 1 && maf > 1 && ltft != 0) {
			{
				//why the crack doesnt this work..
				//what else is there TO-DO??
				FuelEconomyWithMAFObdCommand fuelEconCmd = new FuelEconomyWithMAFObdCommand(
						FuelType.DIESEL, speed, maf, ltft, false /* TODO */);
				TextView tvMpg = (TextView) findViewById(R.id.fuel_econ_text);
				String liters100km = String.format("%.2f", fuelEconCmd.getLitersPer100Km());
				String mpergallon = String.format("%.2f", fuelEconCmd.getMPG());
				//tvMpg.setText("" + liters100km);
				tvMpg.setText("" + mpergallon);
				//Log.d(TAG, "FUELECON:" + liters100km);
				Log.d(TAG, "FUELECON:" + mpergallon);
				
				//mServiceConnection.addJobToQueue(fuelEconCmd);
			}
			 		
			if (mServiceConnection.isRunning())
				queueCommands();
	
			// run again in 2s
			mHandler.postDelayed(mQueueCommands, 2000);
		}
	};

	/**
	 * 
	 */
	private void queueCommands() {
		final ObdCommandJob airTemp = new ObdCommandJob(
				new AmbientAirTemperatureObdCommand());
		final ObdCommandJob rpm = new ObdCommandJob(new EngineRPMObdCommand());
		final ObdCommandJob maf = new ObdCommandJob(new MassAirFlowObdCommand());
		final ObdCommandJob fuelLevel = new ObdCommandJob(
				new FuelLevelObdCommand());
		final ObdCommandJob ltft1 = new ObdCommandJob(new FuelTrimObdCommand(
				FuelTrim.LONG_TERM_BANK_1));
		final ObdCommandJob ltft2 = new ObdCommandJob(new FuelTrimObdCommand(
				FuelTrim.LONG_TERM_BANK_2));
		final ObdCommandJob stft1 = new ObdCommandJob(new FuelTrimObdCommand(
				FuelTrim.SHORT_TERM_BANK_1));
		final ObdCommandJob stft2 = new ObdCommandJob(new FuelTrimObdCommand(
				FuelTrim.SHORT_TERM_BANK_2));
		final ObdCommandJob equiv = new ObdCommandJob(new CommandEquivRatioObdCommand());
		
		//add more commands here
		//create ObdCommandJobs from this:
		//ArrayList<ObdBaseCommand> cmds = ConfigActivity.getObdCommands(prefs);
		
		final ObdCommandJob throt = new ObdCommandJob(new ThrottlePositionObdCommand());
		final ObdCommandJob troub = new ObdCommandJob(new TroubleCodesObdCommand(1));
		final ObdCommandJob speed = new ObdCommandJob(new SpeedObdCommand());
		final ObdCommandJob fuelConsumption = new ObdCommandJob(new FuelConsumptionObdCommand());
		final ObdCommandJob fuelEcon = new ObdCommandJob(
				new FuelEconomyObdCommand());
		//final FuelEconomyWithMAFObdCommand fuelEconCmdMAF = new FuelEconomyWithMAFObdCommand(FuelType.DIESEL, 1, 1, ltft, true);
		
		
		//add command names to validCommands
		//validCommands.add("MPG");
		validCommands.add(airTemp.getCommand().getName());
		validCommands.add(rpm.getCommand().getName());
		validCommands.add(maf.getCommand().getName());
		validCommands.add(fuelLevel.getCommand().getName());
		validCommands.add(ltft1.getCommand().getName());
		//validCommands.add(ltft2.getCommand().getName());
		//validCommands.add(stft1.getCommand().getName());
		//validCommands.add(stft2.getCommand().getName());
		validCommands.add(throt.getCommand().getName());
		validCommands.add(troub.getCommand().getName());
		validCommands.add(speed.getCommand().getName());
		validCommands.add(fuelConsumption.getCommand().getName());
		validCommands.add(fuelEcon.getCommand().getName());
		//validCommands.add(fuelEconCmdMAF.getName());
		//validCommands.add(fuelEcon.getCommand().getName());
		
		//focus here, trying to queue too many commands?
		///if we are able to get speed, then expand on that
		//try this:
		/*
		 * public enum ObdCommandJobState {
			    NEW,
			    RUNNING,
			    FINISHED,
			    EXECUTION_ERROR,
			    QUEUE_ERROR
			  }
		 */
		//added all commands!!!! can add more based on the preferences..hopefully!
		//next steps will involve visualization and actual profile building
		
		//TODO add TroubleCodes to this list
		//need a struct to keep track of the key names
		mServiceConnection.addJobToQueue(airTemp);
		mServiceConnection.addJobToQueue(rpm);
		mServiceConnection.addJobToQueue(maf);
		mServiceConnection.addJobToQueue(fuelLevel);
		mServiceConnection.addJobToQueue(equiv);
		mServiceConnection.addJobToQueue(ltft1);
		//mServiceConnection.addJobToQueue(ltft2);
		//mServiceConnection.addJobToQueue(stft1);
		//mServiceConnection.addJobToQueue(stft2);
		mServiceConnection.addJobToQueue(throt);
		mServiceConnection.addJobToQueue(troub);
		mServiceConnection.addJobToQueue(speed);
		mServiceConnection.addJobToQueue(fuelConsumption);
		mServiceConnection.addJobToQueue(fuelEcon);
		//mServiceConnection.addJobToQueue(fuelEconCmdMAF);
	}
}