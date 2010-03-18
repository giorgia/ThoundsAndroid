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

	static final int DIALOG_LOADING = 0;
	static final int DIALOG_ALERT_CONNECTION = 1;
	static final int DIALOG_LOGIN = 2;
	static final int DIALOG_ALERT_LOGIN = 3;
	
	
	public static boolean isLogged = false;

	Intent nextIntent = null;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// call the base class to include system menus
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean login(String username, String password, String url) {
		DefaultHttpClient client = new DefaultHttpClient();

		HttpProtocolParams.setUseExpectContinue(client.getParams(), false);

		client.getCredentialsProvider().setCredentials(
				new AuthScope(null, 80, "thounds"),
				new UsernamePasswordCredentials(username, password));
		HttpResponse response = null;
		try {
			HttpGet httpget = new HttpGet();
			httpget.setURI(new URI(url));

			httpget.addHeader("Accept", "application/json");

			Log.d("LOGIN", "executing request" + httpget.getRequestLine());

			response = client.execute(httpget);

			BufferedReader in = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));

			Log.d("LOGIN","----------------------------------------");

			StringBuffer sb = new StringBuffer(""); 
			String line = ""; 
			String NL = System.getProperty("line.separator"); 
			while ((line = in.readLine()) != null) {
				sb.append(line + NL); 
			}
			in.close();

			String result = sb.toString();
			Log.d("LOGIN", result);

			JSONObject json = new JSONObject(result);

			if(json.getString("email").equals(username))
				return true;

		}catch (JSONException e) {
			//Access denied
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public void logout() {
		isLogged = false;

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
			mDialog1
			.setOnDismissListener(new DialogInterface.OnDismissListener() {
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
					.setPositiveButton("Ok",
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
	protected void onDestroy() {

		super.onDestroy();
	}

}
