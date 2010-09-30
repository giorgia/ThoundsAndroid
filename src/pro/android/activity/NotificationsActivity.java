package pro.android.activity;


import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.NotificationPair;
import org.thounds.thoundsapi.NotificationsWrapper;
import org.thounds.thoundsapi.Thounds;
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.ThoundsNotAuthenticatedexception;
import org.thounds.thoundsapi.UserWrapper;

import pro.android.R;
import pro.android.utils.ImageFromUrl;
import pro.android.utils.Player;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class NotificationsActivity extends CommonActivity {

	ListView listView;
	RequestFriendshipAdapter adapter;

	NotificationPair<UserWrapper>[] userWrapper;

	boolean isPlaying = false;
	boolean isPaused = false;

	String mix_url = null;
	Player p;
	NotificationsWrapper nw=null;
	JSONObject json;
	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	private HashMap<String,Object> item = new HashMap<String,Object>();
	Button confirm = null;
	Button ignore = null;
	int position = -1;
	int prevPosition= -1;

	SeekBar seekBar;
	ImageButton btn;
	LinearLayout retrieving_bar;
	LinearLayout vParent;
	ProgressBar pBar;
	TextView no_notifications;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notifications);


		listView = (ListView) findViewById(R.id.friendshipList);
		adapter = new RequestFriendshipAdapter(
				this,
				R.layout.friendship_item,
				list
		);

		listView.setAdapter(adapter);
		listView.setItemsCanFocus(true);

		showDialog(DIALOG_LOADING);
		Runnable run = new Runnable(){
			public void run() {
				retrievedNotification();

			}			
		};
		Thread thread =  new Thread(run, "retrievedNotification");
		thread.start();



	}

	private Runnable returnRes = new Runnable() {
		public void run() {

			for(int i=0; i<userWrapper.length; i++){
				item = new HashMap<String,Object>();
				try {
					if(userWrapper[i].getNotificationObject().getAvatarUrl()!=null)
						item.put("image", new ImageFromUrl(userWrapper[i].getNotificationObject().getAvatarUrl()).getDrawable());

					item.put("line1",userWrapper[i].getNotificationObject().getName());
					item.put("line2",userWrapper[i].getNotificationObject().getCity()+", "+userWrapper[i].getNotificationObject().getCountry());
					list.add( item );


					adapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			dismissDialog(DIALOG_LOADING);
		}
	};

	private void retrievedNotification() {
		try {
			nw = Thounds.loadNotifications();
			userWrapper= nw.getBandRequestList();
		} catch (ThoundsConnectionException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			showDialog(DIALOG_ALERT_CONNECTION);
		} catch (IllegalThoundsObjectException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
		} catch (ThoundsNotAuthenticatedexception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		runOnUiThread(returnRes);

	}

	public void onClickItemNotification(View v){

		int position = (Integer) v.getTag();

		nextIntent = new Intent(v.getContext(), ProfileActivity.class);
		nextIntent.putExtra("userId", userWrapper[position].getNotificationObject().getId());
		startActivity(nextIntent);
	}


	public void onClickConfirm(View v){

		confirm = (Button)v;
		int tag = (Integer)confirm.getTag();

		try {
			int idNotif= userWrapper[tag].getNotificationId(); 
			boolean ris = Thounds.acceptFriendship(idNotif);
			if(ris)	{
				showDialog(DIALOG_ADD_USER);
			} else {
				showDialog(DIALOG_IGNORE_USER);
			}
		} catch (ThoundsConnectionException e) {
			// TODO Auto-generated catch block
			showDialog(DIALOG_ALERT_CONNECTION);
			e.printStackTrace();

		} catch (ThoundsNotAuthenticatedexception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void onClickIgnore(View v){
		ignore = (Button)v;
		int tag = (Integer)ignore.getTag();
		int idNotif= userWrapper[tag].getNotificationId();
		try {
			Thounds.refuseFriendship(idNotif);
		} catch (ThoundsConnectionException e1) {
			// TODO Auto-generated catch block
			showDialog(DIALOG_ALERT_CONNECTION);
			e1.printStackTrace();
		} catch (ThoundsNotAuthenticatedexception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private class RequestFriendshipAdapter extends ArrayAdapter<HashMap<String,Object>> {

		private ArrayList<HashMap<String,Object>> items;

		public RequestFriendshipAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String,Object>> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.friendship_item, null);
			}
			HashMap<String,Object> item = items.get(position);
			if (item != null) {

				TextView tt = (TextView) v.findViewById(R.id.text1);
				TextView bt = (TextView) v.findViewById(R.id.text2);
				ImageView avatar = (ImageView) v.findViewById(R.id.ImageViewAvatar);
				Button confirm = (Button) v.findViewById(R.id.ButtonConfirmFriends);
				Button ignore = (Button) v.findViewById(R.id.ButtonIgnoreFriend);
				LinearLayout ln = (LinearLayout) v.findViewById(R.id.lNewFriend);

				avatar.setImageDrawable(new ImageFromUrl(userWrapper[position].getNotificationObject().getAvatarUrl()).getDrawable());
				if (tt != null) {
					tt.setText( (String)item.get("line1")); }
				if(bt != null){
					bt.setText((String)item.get("line2"));
				}
				if (avatar != null)
					avatar.setImageDrawable((Drawable)item.get("image"));

				ln.setTag(position);
				confirm.setTag(position);
				ignore.setTag(position);

			}
			return v;
		}

	}

	@Override
	public void onPause(){
		super.onPause();
	}

}



