package pro.android.activity;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.thounds.thoundsapi.HomeWrapper;
import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.NotificationsWrapper;
import org.thounds.thoundsapi.RequestWrapper;
import org.thounds.thoundsapi.ThoundWrapper;
import org.thounds.thoundsapi.ThoundsConnectionException;

import pro.android.R;
import pro.android.utils.ThoundsList;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class NewThoundsNotificationActivity extends CommonActivity{
	ThoundsList list;
	//HomeWrapper home;
	ThoundWrapper [] thounds;

	JSONObject thound;
	JSONObject thounds_collection;
	JSONObject json;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_thounds_list);
		//currentActivity = R.id.home;
		//Button logout = (Button) findViewById(R.id.btnLogout);
		/*logout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				logout();
			}
		});

		ImageButton reload = (ImageButton) findViewById(R.id.btnReload);
		reload.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});
*/

		list = new ThoundsList(this);

		Runnable run = new Runnable(){
			public void run() {
				retrievedData();     	
			}			
		};
		Thread thread =  new Thread(run, "retrievedData");
		thread.start();


	}



	private void retrievedData() {
		
		NotificationsWrapper nw;
		try {
			nw = RequestWrapper.loadNotifications();
			thounds=nw.getNewThoundsList();
			list.setThound(thounds);
			
		} catch (ThoundsConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalThoundsObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
			//home = RequestWrapper.loadHome(1, 20);
			//list.setThound(home.getThoundsCollection().getThoundsList());

	
	

		runOnUiThread(list.getReturnRes());

	}
//	public void onClickButton(View v){
//		list.onClickButton(v);
//	}
//	public void onClickItem(View v){
//		list.onClickItem(v);
//	}

	@Override
	public void onPause(){
		super.onPause();
		this.finish();
	}
}