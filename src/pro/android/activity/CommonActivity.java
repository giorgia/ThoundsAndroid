package pro.android.activity;

import pro.android.R;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CommonActivity extends Activity{

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//call the base class to include system menus
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
}
