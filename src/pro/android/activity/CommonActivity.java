package pro.android.activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;
import pro.android.R;

import pro.android.utils.Communication;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CommonActivity extends Activity {

	Intent nextIntent = null;
	public static final String PREFS_NAME = "thound_prefs";
	static final int DIALOG_LOADING = 0;
	static final int DIALOG_ALERT_CONNECTION = 1;
	static final int DIALOG_LOGIN = 2;
	static final int DIALOG_ALERT_LOGIN = 3;
	Communication comm=null;
	String username, password;

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// call the base class to include system menus
		MenuInflater inflater = getMenuInflater();
	
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.home:
			item.setIntent(new Intent(this, HomeActivity.class));
			break;
		case R.id.notifications:
			item.setIntent(new Intent(this, NotificationsActivity.class));
			break;
		case R.id.record:
			item.setIntent(new Intent(this, RecordActivity.class));
			break;
		case R.id.profile:
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
			mDialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					startActivity(nextIntent);
				}
			});
			return mDialog1;
		}
		case DIALOG_ALERT_CONNECTION: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Alert").setMessage("No network connection!")
			.setCancelable(false).setIcon(
					android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
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
	protected void onDestroy() {

		super.onDestroy();
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 if(comm ==null)
		 {
			 Log.i("creo Comunication", "Comunication");
		comm=new Communication();
		 }
		
	}
	
	public boolean logout() {
		
        
        comm.logout();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.clear();
		editor.commit();
		Intent  nextIntent = new Intent(this, LoginActivity.class);
		startActivity(nextIntent);
		this.finish();
		
		return true;
	}
	
	public JSONObject request(String url)
	{
		
		SharedPreferences settings = getSharedPreferences( PREFS_NAME, 0);
		  username = settings.getString("silentUsername", username);
		  password = settings.getString("silentPassword", password);
	    
		  Log.e("REQUEST", "Entra...REQUEST");
		if (Communication.isLogged || comm.login(username, password)) {

			  Log.e("lOGGATO", "CHIAMO GET reQUEST");
			//nextIntent = new Intent(v.getContext(), HomeActivity.class);
             return comm.getRequest( url);

		} else {
			 Log.e("Non lOGGATO", "Ritorno null");
			//setContentView(R.layout.log_in);
			return null;
		}
		
	}
	
	public void setViewLogin()
	{
		
		setContentView(R.layout.log_in);
	}

	
	
	
	

}
