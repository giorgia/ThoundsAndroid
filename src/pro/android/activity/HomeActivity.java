package pro.android.activity;

import pro.android.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends CommonActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		Button logout = (Button) findViewById(R.id.Button01);
		logout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				logout();
			}
		});
	}

}
