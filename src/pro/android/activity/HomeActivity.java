package pro.android.activity;

import org.thounds.thoundsapi.HomeWrapper;
import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.ThoundWrapper;
import org.thounds.thoundsapi.Thounds;
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.ThoundsNotAuthenticatedexception;

import pro.android.R;
import pro.android.utils.ImageFromUrl;
import pro.android.utils.ThoundsList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class HomeActivity extends CommonActivity{

	ThoundsList list;
	HomeWrapper home;
	ThoundWrapper thounds;

	Thread thread;
	Runnable run;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		currentActivity = R.id.home;

		
		ImageButton reload = (ImageButton) findViewById(R.id.btnReload);
		reload.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				reload();
			}
		});

		run = new Runnable(){
			public void run() {
				retrievedData();
			}			
		};	
		reload();
	}


	private synchronized void retrievedData() {

		try {
			home = Thounds.loadHome(1,20);
			ThoundWrapper[] ths = home.getThoundsCollection().getThoundsList();
			Drawable[] imgs = new Drawable[ths.length];
			for(int i = 0; i < ths.length; i++){
				imgs[i] = new ImageFromUrl(ths[i].getUserAvatarUrl()).getDrawable();
			}
			list.setThound(ths);
			list.setAvatars(imgs);
		} catch (ThoundsConnectionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			showDialog(DIALOG_ALERT_CONNECTION);

		} catch (IllegalThoundsObjectException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
		}catch (IllegalStateException e) {
			// TODO: handle exception
			reload();
		} catch (ThoundsNotAuthenticatedexception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		runOnUiThread(list.getReturnRes());
		StartNotification();
	}
	public void reload(){
		list = new ThoundsList(this);
		thread =  new Thread(run, "retrievedData");
		thread.start();
	}
	public void onClickArrow(View v){
		list.onClickArrow(v);
	}
	public void onItemClick(View v){
		list.onItemClick(v);
	}

	@Override
	public void onRestart(){
		super.onRestart();
		//reload();
		Log.d("HOME","RESTART");
	}

	@Override
	public void onResume(){
		super.onResume();
		list.resetListAdapter();
		Log.d("HOME","RESUME");
	}
	@Override
	public void onStart(){
		super.onStart();
		//reload();
		Log.d("HOME","START");
	}
	@Override
	public void onStop(){
		super.onStop();
		//reload();
		Log.d("HOME","STOP");
	}
}

