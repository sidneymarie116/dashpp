package dashpp.obd.reader.activity;

import dashpp.obd.reader.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends Activity {
		@Override
		protected void onCreate (Bundle savedInstanceState) {
				super.onCreate (savedInstanceState);
				setContentView (R.layout.main_menu);
				
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
						Intent act = new Intent (v.getContext(), CollisionActivity.class);
						((Button)v).setBackgroundColor(Color.WHITE);
						startActivity (act);
					}
				});
				
				final Button switchActFive = (Button) findViewById (R.id.settingsButton);
				switchActFive.setOnClickListener (new View.OnClickListener() {
					
					@Override
					public void onClick (View v) {
						// TODO Auto-generated method stub
						Intent act = new Intent (v.getContext(), SettingsActivity.class);
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
