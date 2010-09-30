package pro.android.activity;


import org.thounds.thoundsapi.Thounds;
import org.thounds.thoundsapi.ThoundsConnectionException;

import pro.android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends CommonActivity {

	private String username;
	private String password;
	private EditText usernameEditText;
	private EditText passwordEditText;

	@Override
	public void onCreate(Bundle cicle) {
		super.onCreate(cicle);
		setContentView(R.layout.log_in);

		usernameEditText = (EditText) findViewById(R.id.txt_username);
		passwordEditText = (EditText) findViewById(R.id.txt_password);
		Button launch = (Button) findViewById(R.id.login_button);
		Button signup = (Button) findViewById(R.id.btn_sign_up);

		// Click on signup button
		signup.setOnClickListener(new OnClickListener() {
			public void onClick(final View viewParam) {
				nextIntent = new Intent(viewParam.getContext(), SignUpActivity.class);
				startActivity(nextIntent);
			}
		});

		// Click on login button
		launch.setOnClickListener(new OnClickListener() {
			public void onClick(final View viewParam) {

				// Get the text from the editexts
				username = usernameEditText.getText().toString();
				password = passwordEditText.getText().toString();
				Log.d("login", username+"  "+password);

				if (username == null || password == null) {
					showDialog(DIALOG_ALERT_LOGIN);
				} else {
					showDialog(DIALOG_LOGIN);
					Runnable run = new Runnable(){
						public void run() {
							try {
								if (login(username, password)) {
									nextIntent = new Intent(viewParam.getContext(), HomeActivity.class);
								}
							} catch (ThoundsConnectionException e) {
								connectionError = true;
								dismissDialog(DIALOG_LOGIN);
								showDialog(DIALOG_ALERT_CONNECTION);
								e.printStackTrace();
							}
							runOnUiThread(checkAutentication);
						}			
					};
					Thread thread =  new Thread(null, run, "Login");
					thread.start();

				}
			}
		});

	}
	private Runnable checkAutentication = new Runnable() {
		public void run() {
			if(connector.isAuthenticated()){
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
			
				editor.putString("silentUsername", username);
				editor.putString("silentPassword", password);
				
				editor.commit();
				dismissDialog(DIALOG_LOGIN);
				startActivity(nextIntent);
			} else {
				dismissDialog(DIALOG_LOGIN);
				showDialog(DIALOG_ALERT_LOGIN);

			}
		}

	};
	

	public void onClickUsername(View v){
		if((usernameEditText.getText().toString()).equals("e-mail"))
			usernameEditText.setText("");
	}
	public void onClickPassword(View v){
		if((passwordEditText.getText().toString()).equals("password"))
			passwordEditText.setText("");
	}

}
