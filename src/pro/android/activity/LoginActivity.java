package pro.android.activity;


import org.thounds.thoundsapi.RequestWrapper;
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

					Runnable run = new Runnable(){
						public void run() {


							try {
								if (RequestWrapper.login(username, password)) 

									nextIntent = new Intent(viewParam.getContext(), HomeActivity.class);
							} catch (ThoundsConnectionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							

							runOnUiThread(returnRes);
						}			
					};
					Thread thread =  new Thread(null, run, "Login");
					thread.start();
					showDialog(DIALOG_LOGIN);
				}
			}
		});

	}
	private Runnable returnRes = new Runnable() {
		public void run() {
			if(RequestWrapper.isLogged()){
				dismissDialog(DIALOG_LOGIN);

			} else {
				dismissDialog(DIALOG_LOGIN);
				showDialog(DIALOG_ALERT_LOGIN);

			}
		}

	};
	@Override
	protected void onStop() {
		super.onStop();

		// Save user preferences. We need an Editor object to
		// make changes. All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		if (RequestWrapper.isLogged()) {
			editor.putString("silentUsername", username);
			editor.putString("silentPassword", password);
		}

		// Don't forget to commit your edits!!!
		editor.commit();

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_LOGIN: {
			ProgressDialog mDialog1 = new ProgressDialog(this);
			mDialog1.setTitle("Login");
			mDialog1.setMessage("Please wait...");
			mDialog1.setIndeterminate(true);
			mDialog1.setCancelable(true);
			mDialog1
			.setOnDismissListener(new DialogInterface.OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					if (nextIntent != null)
						startActivity(nextIntent);
				}
			});
			return mDialog1;
		}
		case DIALOG_ALERT_LOGIN: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Alert").setIcon(
					android.R.drawable.ic_dialog_alert).setMessage(
					"Username or Password incorrect. Try again!")
					.setCancelable(false).setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							dialog.cancel();
						}
					});

			return builder.create();
		}
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	public void onClickUsername(View v){
		if((usernameEditText.getText().toString()).equals("e-mail"))
			usernameEditText.setText("");
	}
	public void onClickPassword(View v){
		if((passwordEditText.getText().toString()).equals("password"))
			passwordEditText.setText("");
	}

}
