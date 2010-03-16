package pro.android.activity;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;

import pro.android.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CommonActivity extends Activity {

	public static final String PREFS_NAME = "thound_prefs";

	public static boolean isLogged = false;

	Intent nextIntent = null;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// call the base class to include system menus
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;

	}

	public HttpResponse login(String username, String password, String url) {
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

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
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
	protected void onDestroy() {

		super.onDestroy();
	}

}
