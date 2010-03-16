package pro.android.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;

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

	public static boolean isLogged = false;

	private static final int DIALOG1_KEY = 0;
	private static final int DIALOG2_KEY = 1;

	String username;
	String password;

	@Override
	public void onCreate(Bundle cicle) {
		super.onCreate(cicle);
		setContentView(R.layout.log_in);

		final EditText usernameEditText = (EditText) findViewById(R.id.txt_username);
		final EditText passwordEditText = (EditText) findViewById(R.id.txt_password);
		Button launch = (Button) findViewById(R.id.login_button);

		// Click on login button
		launch.setOnClickListener(new OnClickListener() {
			public void onClick(View viewParam) {

				// Get the text from the editexts
				username = usernameEditText.getText().toString();
				password = passwordEditText.getText().toString();

				if (usernameEditText == null || passwordEditText == null) {
					showDialog(DIALOG2_KEY);
				} else {
					BufferedReader in = null;
					try {
						showDialog(DIALOG1_KEY);

						HttpResponse response = login(username, password,
								"http://thounds.com/home");

						new Handler().postDelayed(new Runnable() {

							public void run() {
								dismissDialog(DIALOG1_KEY);
							}
						}, 4000);

						if (response != null
								&& response.getStatusLine().getReasonPhrase()
										.equals("OK")) {
							isLogged = true;
							nextIntent = new Intent(viewParam.getContext(),
									HomeActivity.class);

						} else {
							dismissDialog(DIALOG1_KEY);

							showDialog(DIALOG2_KEY);

						}
						in = new BufferedReader(new InputStreamReader(response
								.getEntity().getContent()));

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
		case DIALOG1_KEY: {
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
		case DIALOG2_KEY: {
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
