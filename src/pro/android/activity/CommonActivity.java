package pro.android.activity;

import org.thounds.thoundsapi.Thounds;
import org.thounds.thoundsapi.ThoundWrapper;
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.connector.ThoundsDigestConnector;

import pro.android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CommonActivity extends Activity {

	public static final String PREFS_NAME = "thound_prefs";
	public static ThoundWrapper obj;

	private boolean runningNotificationService=false; 

	static final int DIALOG_LOADING = 0;
	static final int DIALOG_ALERT_CONNECTION = 1;
	static final int DIALOG_LOGIN = 2;
	static final int DIALOG_ALERT_LOGIN = 3;
	static final int DIALOG_RETRIEVING_THOUNDS = 4;
	static final int DIALOG_RETRIEVING_TRACKS = 5;
	static final int DIALOG_ADD_USER = 6;
	static final int RESULT_SEARCH_NULL = 7;
	static final int DIALOG_IGNORE_USER = 8;
	static final int DIALOG_ILLEGAL_THOUNDS_OBJECT = 9;
	static final int DIALOG_EXCEPTION_BUFFER_PALYER = 10;
	static final int DIALOG_FORMAT_JSON=11;
	static final int DIALOG_GENERIC_EXCEPTION=12;
	static final int DIALOG_ILLEGAL_ARGUMENT_EXCEPTION=13;
	static final int DIALOG_ILLEGAL_STATE_EXCEPTION=14;
	static final int DIALOG_RESULT_SIGNUP = 15;

	public static boolean connectionError = false;
	static String DEFAULT_COVER_URL = "http://thounds.com/images/speaker.gif";
	public ThoundsDigestConnector connector;
	public static Intent nextIntent = null;
	public int currentActivity;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// call the base class to include system menus
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean login(String username, String password) throws ThoundsConnectionException{	
		connector = new ThoundsDigestConnector(username, password);
		Thounds.setConnector(connector);
		return connector.login(username, password);
	}


	public void logout() {
		((ThoundsDigestConnector)Thounds.getConnector()).logout();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.clear();
		editor.commit();
		nextIntent = new Intent(this, LoginActivity.class);
		startActivity(nextIntent);
		this.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.home:
			if(currentActivity != R.id.home)
				item.setIntent(new Intent(this, HomeActivity.class));
			break;
		case R.id.notifications:
			item.setIntent(new Intent(this, NotificationsActivity.class));
			break;
		case R.id.record:
			if(currentActivity != R.id.record)
				item.setIntent(new Intent(this, RecordActivity.class));
			break;
		case R.id.profile:
			ProfileActivity.userId = -1;
			item.setIntent(new Intent(this, ProfileActivity.class));
			break;
		case R.id.search:
			item.setIntent(new Intent(this, SearchActivity.class));
			break;
		}

		return false;
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_LOADING: {
			ProgressDialog mDialog1 = new ProgressDialog(this);
			mDialog1.setMessage("Loading. Please wait...");
			mDialog1.setIndeterminate(true);
			mDialog1.setCancelable(true);

			return mDialog1;
		}
		case DIALOG_ALERT_CONNECTION: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Alert").setMessage("No network connection!")
			.setCancelable(false).setIcon(
					android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							finish();
							dialog.cancel();
						}
					});

			return builder.create();
		}
		case DIALOG_ADD_USER: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Information").setMessage("User added successfully")
			.setCancelable(false).setIcon(
					android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							finish();
							dialog.cancel();
						}
					});

			return builder.create();
		}

		case DIALOG_FORMAT_JSON: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Information").setMessage("Format Json Exception")
			.setCancelable(false).setIcon(
					android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							finish();
							dialog.cancel();
						}
					});

			return builder.create();
		}

		case DIALOG_GENERIC_EXCEPTION: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Information").setMessage("Generic exception")
			.setCancelable(false).setIcon(
					android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							finish();
							dialog.cancel();
						}
					});

			return builder.create();
		}

		case DIALOG_ILLEGAL_THOUNDS_OBJECT: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Information").setMessage("Illegal thounds object")
			.setCancelable(false).setIcon(
					android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							finish();
							dialog.cancel();
						}
					});

			return builder.create();
		}

		case DIALOG_ILLEGAL_ARGUMENT_EXCEPTION: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Information").setMessage("Illegal Argument")
			.setCancelable(false).setIcon(
					android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							finish();
							dialog.cancel();
						}
					});

			return builder.create();
		}

		case DIALOG_ILLEGAL_STATE_EXCEPTION: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Information").setMessage("Illegal state")
			.setCancelable(false).setIcon(
					android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							finish();
							dialog.cancel();
						}
					});

			return builder.create();
		}

		case DIALOG_EXCEPTION_BUFFER_PALYER: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Information").setMessage("Exception Buffer Player")
			.setCancelable(false).setIcon(
					android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							finish();
							dialog.cancel();
						}
					});

			return builder.create();
		}
		case DIALOG_IGNORE_USER: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Information").setMessage("User ignore")
			.setCancelable(false).setIcon(
					android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							finish();
							dialog.cancel();
						}
					});

			return builder.create();
		}

		case RESULT_SEARCH_NULL: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Information").setMessage("No user foundsk")
			.setCancelable(false).setIcon(
					android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							finish();
							dialog.cancel();
						}
					});

			return builder.create();
		}

		case DIALOG_RESULT_SIGNUP: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Alert").setIcon(
					android.R.drawable.ic_dialog_alert).setMessage(
					"Registration Successful.")
					.setCancelable(false).setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							dialog.cancel();
						}
					});

			return builder.create();
		}
		case DIALOG_LOGIN: {
			ProgressDialog mDialog1 = new ProgressDialog(this);
			mDialog1.setTitle("Login");
			mDialog1.setMessage("Please wait...");
			mDialog1.setIndeterminate(true);
			mDialog1.setCancelable(true);

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
		case DIALOG_RETRIEVING_THOUNDS: {
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("Please wait...");
			progressDialog.setMessage("Retrieving data ...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);   
			return progressDialog;
		}
		case DIALOG_RETRIEVING_TRACKS: {
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Downloading tracks...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);   
			return progressDialog;
		}
		}
		return null;
	}
	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	//Start Notification Service
	public synchronized void StartNotification()
	{
		if(runningNotificationService==false)
		{
			Log.e("notification", "start service su Thounds ACtivity");
			runningNotificationService=true;
			startService(new Intent(CommonActivity.this, NotificationService.class));

		}
	}

	public synchronized void StopNotification()
	{
		if(runningNotificationService==true)
		{
			Log.e("notification", "start service su Thounds ACtivity");
			runningNotificationService=false;
			NotificationService.stop();
			//startService(new Intent(HomeActivity.this, NotificationService.class));

		}
	}

}
