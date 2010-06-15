package pro.android.activity;

import java.io.BufferedReader;

import java.io.IOException;

import org.thounds.thoundsapi.RequestWrapper;
import org.thounds.thoundsapi.UserWrapper;

import pro.android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends CommonActivity {

	private static final int DIALOG_SIGN_UP = 0;
	public static boolean isLogged = false;
    private Intent	nextIntent;
	String username;
	String password;

	@Override
	public void onCreate(Bundle cicle) {
		super.onCreate(cicle);
		setContentView(R.layout.log_in);

		final EditText usernameEditText = (EditText) findViewById(R.id.txt_username);
		final EditText passwordEditText = (EditText) findViewById(R.id.txt_password);
		Button launch = (Button) findViewById(R.id.login_button);
		Button signUp = (Button) findViewById(R.id.ButtonSignUpLogin);
		
		//Click on SignUp Button
		signUp.setOnClickListener(new OnClickListener() {
			public void onClick(View viewParam) {
				try
				{
					Log.e("MA ALLORA PASSIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIs","siiiiiiiiiii");
			     nextIntent = new Intent(viewParam.getContext(), SignUpActivity.class);
			     //showDialog(DIALOG_SIGN_UP);
			     startActivity(nextIntent);
				}
				catch(Exception e)
				{
					
					Log.d("ATTENZIONE",e.toString());
				}
			}
			
			
		});
		
	
	
		// Click on login button
		launch.setOnClickListener(new OnClickListener() {
			public void onClick(View viewParam) {

				// Get the text from the editexts
				username = usernameEditText.getText().toString();
				password = passwordEditText.getText().toString();

				if (usernameEditText == null || passwordEditText == null || usernameEditText.equals("")) {
					showDialog(DIALOG_ALERT_LOGIN);
				} else {
					BufferedReader in = null;
					try {
						showDialog(DIALOG_LOGIN);

						if (RequestWrapper.login(username, password))//(//comm.login(username, password))
						{
							new Handler().postDelayed(new Runnable() {

								public void run() {
									dismissDialog(DIALOG_LOGIN);
								}
							}, 2000);
							isLogged = true;
							nextIntent = new Intent(viewParam.getContext(),HomeActivity.class);
							

						} else {
							dismissDialog(DIALOG_LOGIN);

							showDialog(DIALOG_ALERT_LOGIN);

						}


					} catch (Exception e) {
						e.printStackTrace();

					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}

				}
			}
		});

	}
	
	
	@Override
	protected void onStop() {
		super.onStop();

		// Save user preferences. We need an Editor object to
		// make changes. All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		if (isLogged) {
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
	
	
}
