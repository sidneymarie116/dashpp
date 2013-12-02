package dashpp.obd.reader.activity;

import dashpp.obd.reader.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UsageActivity extends Activity {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
			super.onCreate (savedInstanceState);
			setContentView (R.layout.usage);
	}
}
