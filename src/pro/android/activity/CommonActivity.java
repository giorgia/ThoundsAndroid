package pro.android.activity;

import org.thounds.thoundsapi.RequestWrapper;
import org.thounds.thoundsapi.ThoundWrapper;

import pro.android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CommonActivity extends Activity {

	public static final String PREFS_NAME = "thound_prefs";
	public static ThoundWrapper obj;
	
	static final int DIALOG_LOADING = 0;
	static final int DIALOG_ALERT_CONNECTION = 1;
	static final int DIALOG_LOGIN = 2;
	static final int DIALOG_ALERT_LOGIN = 3;
	static final int DIALOG_RETRIEVING_THOUNDS = 4;
	static final int DIALOG_RETRIEVING_TRACKS = 5;
	static final int DIALOG_ADD_USER = 6;

	public static boolean isLogged = false;
	public static boolean connectionError = false;

	public static Intent nextIntent = null;
	public int currentActivity;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// call the base class to include system menus
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	
	public void logout() {
		RequestWrapper.logout();
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

}
