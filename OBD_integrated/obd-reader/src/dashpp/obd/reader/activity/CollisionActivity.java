package dashpp.obd.reader.activity;

import dashpp.obd.reader.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CollisionActivity extends Activity {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
			super.onCreate (savedInstanceState);
			setContentView (R.layout.collision);
			
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
