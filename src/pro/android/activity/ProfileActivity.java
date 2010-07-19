package pro.android.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.thounds.thoundsapi.BandWrapper;
import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.RequestWrapper;
import org.thounds.thoundsapi.ThoundWrapper;
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.UserWrapper;

import pro.android.R;
import pro.android.utils.ImageFromUrl;
import pro.android.utils.Player;
import pro.android.utils.ThoundsList;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;


public class ProfileActivity extends CommonActivity{

	ThoundsList list;
	static int userId;
	UserWrapper user;
	UserWrapper friend;
	BandWrapper band;
	ThoundWrapper thounds;
	Player p;
	ProgressBar seek;
	protected int progress;
	ListView contactsList;
	ContactsAdapter sAdapter;
	private ArrayList<HashMap<String,Object>> arrayList = new ArrayList<HashMap<String,Object>>();
	private HashMap<String,Object> item = new HashMap<String,Object>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		currentActivity = R.id.profile;

		list = new ThoundsList(this);

		userId = getIntent().getExtras()!=null?getIntent().getExtras().getInt("userId"):-1;

		contactsList = (ListView) findViewById(R.id.contactsListView);

		sAdapter = new ContactsAdapter(this, R.layout.contacts_item_list, arrayList);
		contactsList.setAdapter(sAdapter);

		contactsList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				nextIntent = new Intent(arg1.getContext(), ProfileActivity.class);
				try {
					nextIntent.putExtra("userId", band.getFriend(arg2).getId());
				} catch (IllegalThoundsObjectException e) {
					// TODO Auto-generated catch block
					showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
					e.printStackTrace();
				}
				startActivity(nextIntent);
		};
				
		});

		final ScrollView lInfo = (ScrollView) findViewById(R.id.scrollInfo);
		final LinearLayout lLibrary = (LinearLayout) findViewById(R.id.lLibrary);
		final LinearLayout lContacts = (LinearLayout) findViewById(R.id.lContacts);

		final Button info = (Button) findViewById(R.id.btnInfo);
		final Button library = (Button) findViewById(R.id.btnLibrary);
		final Button contacts = (Button) findViewById(R.id.btnContacts);
		seek = (ProgressBar) findViewById(R.id.ProgressDefault);

		info.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				lInfo.setVisibility(View.VISIBLE);
				lLibrary.setVisibility(View.INVISIBLE);
				lContacts.setVisibility(View.INVISIBLE);
				info.setBackgroundResource(R.layout.shape_darkgray_left);
				info.setTextColor(Color.WHITE);
				library.setBackgroundResource(R.layout.shape_gray_center);
				library.setTextColor(Color.BLACK);
				contacts.setBackgroundResource(R.layout.shape_gray_right);
				contacts.setTextColor(Color.BLACK);
			}
		});


		library.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				lInfo.setVisibility(View.INVISIBLE);
				lLibrary.setVisibility(View.VISIBLE);
				lContacts.setVisibility(View.INVISIBLE);
				library.setBackgroundResource(R.layout.shape_darkgray_center);
				library.setTextColor(Color.WHITE);
				info.setBackgroundResource(R.layout.shape_gray_left);
				info.setTextColor(Color.BLACK);
				contacts.setBackgroundResource(R.layout.shape_gray_right);
				contacts.setTextColor(Color.BLACK);

				Runnable run = new Runnable(){
					public void run() {
						retrievedLibrary();
						Log.d("Profile", "dopo retireved");
					}			
				};
				Thread thread =  new Thread(run, "retrievedLibrary");
				thread.start();

			}


		});

		contacts.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				lContacts.setVisibility(View.VISIBLE);
				lInfo.setVisibility(View.INVISIBLE);
				lLibrary.setVisibility(View.INVISIBLE);
				contacts.setBackgroundResource(R.layout.shape_darkgray_right);
				contacts.setTextColor(Color.WHITE);
				info.setBackgroundResource(R.layout.shape_gray_left);
				info.setTextColor(Color.BLACK);
				library.setBackgroundResource(R.layout.shape_gray_center);
				library.setTextColor(Color.BLACK);


				Runnable run = new Runnable(){
					public void run() {
						retrievedContacts();
					}			
				};
				Thread thread =  new Thread(run, "retrievedContacts");
				thread.start();

				showDialog(DIALOG_RETRIEVING_THOUNDS);
			}
		});


		
		try {
			if(userId == -1)
				user = RequestWrapper.loadUserProfile();
			else{
				Log.d("ID",""+userId);
				user = RequestWrapper.loadGenericUserProfile(userId);
			}
		} catch (ThoundsConnectionException e1) {
			// TODO Auto-generated catch block
		//	e1.printStackTrace();
			showDialog(DIALOG_ALERT_CONNECTION);
			
		} catch (IllegalThoundsObjectException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
		}


		ImageView avatar = (ImageView) findViewById(R.id.imgAvatar);
		TextView name = (TextView) findViewById(R.id.txtName);
		TextView country = (TextView) findViewById(R.id.txtCountry);
		TextView site = (TextView) findViewById(R.id.txtSite);
		TextView blog = (TextView) findViewById(R.id.txtBolg);
		TextView tags = (TextView) findViewById(R.id.txtTags);
		TextView about = (TextView) findViewById(R.id.txtAbout);

		if(user.getAvatarUrl()!=null)
			avatar.setImageDrawable(new ImageFromUrl(user.getAvatarUrl()).getDrawable());
		name.setText(user.getName());
		country.setText(user.getCountry()+", "+user.getCity());
		site.setText(user.getSiteUrl()!=null?user.getSiteUrl():"--");
		//blog.setText(user.getBlog()!=null?user.getBlog():"--");
		about.setText(user.getAbout()!=null?user.getAbout():"--");
		if( user.getTagList() != null){
			String[] tagList = user.getTagList();
			String tag ="";
			for(int i=0; i<tagList.length - 1; i++){
				tag += tagList[i] + ", ";
			}
			tag += tagList[tagList.length-1];
			tags.setText(tag);
		}

		try {
			obj = user.getDefaultThound();
			ImageButton playDef = (ImageButton) findViewById(R.id.playDefault);

			if(obj!=null){

				TextView line1 = (TextView) findViewById(R.id.txt_def_title);
				TextView line2 = (TextView) findViewById(R.id.txt_def_date);
				
				line1.setText(obj.getTrack(0).getTitle());
				line2.setText(obj.getTrack(0).getCreatedAt().subSequence(0, 10)+" at "+obj.getTrack(0).getCreatedAt().substring(11, 16));
			}
		} catch (IllegalThoundsObjectException e) {
			// TODO Auto-generated catch block
			showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
			//e.printStackTrace();
		}

	}

	private void retrievedLibrary() {
		try {

			if (userId == -1)
				list.setThound(RequestWrapper.loadUserLibrary().getThoundsList());
			else
				list.setThound(RequestWrapper.loadGenericUserLibrary(userId, 1, 20).getThoundsList());

		} catch (IllegalThoundsObjectException e) {
			showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ThoundsConnectionException e) {
			// TODO Auto-generated catch block
			showDialog(DIALOG_ALERT_CONNECTION);
			e.printStackTrace();
		}

		runOnUiThread(list.getReturnRes());

	}

	private void retrievedContacts() {
		try {

			if (userId == -1)
				band = RequestWrapper.loadUserBand();
			else
				band = RequestWrapper.loadGenericUserBand(userId);
		} catch (ThoundsConnectionException e) {
			// TODO Auto-generated catch block
			showDialog(DIALOG_ALERT_CONNECTION);
			e.printStackTrace();
		} catch (IllegalThoundsObjectException e) {
			// TODO Auto-generated catch block
			showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
			e.printStackTrace();
		}

		runOnUiThread(returnRes);

	}

	private Runnable returnRes = new Runnable() {
		public void run() {

			for(int i=0; i < band.getFriendListLength(); i++){
				item = new HashMap<String,Object>();
				try {
					friend = band.getFriend(i);
					if(friend.getAvatarUrl()!=null)
						item.put("image", new ImageFromUrl(friend.getAvatarUrl()).getDrawable());
					item.put("line1",friend.getName());
					item.put("line2",friend.getCity()+", "+friend.getCountry());

					arrayList.add( item );
					sAdapter.notifyDataSetChanged();


				}catch (IllegalThoundsObjectException e) {
					// TODO Auto-generated catch block
					showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
					e.printStackTrace();
				}


			}
			dismissDialog(DIALOG_RETRIEVING_THOUNDS);

		}
	};
	public void onClickButton(View v){

		Log.d("Profile", "play default");
		try {
			if(p == null || !p.isPlaying()){
				p = new Player(user.getDefaultThound().getMixUrl());
				p.bufferedAudio();
				((ImageButton) v).setImageResource(android.R.drawable.ic_media_pause);
				p.start();
				progressUpdater();
			}else{
				((ImageButton) v).setImageResource(android.R.drawable.ic_media_play);
				p.pause();
			}

		}
		 catch(IllegalThoundsObjectException ite)
		 {
			 showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
			 
		 }
		catch (Exception e) {
			showDialog(DIALOG_EXCEPTION_BUFFER_PALYER);
		}
	
	}

	public void progressUpdater() {
		new Thread(new Runnable() {
			public void run() {
				seek.setMax((int)p.getDuration()/1000);

				while (p.isPlaying() && progress < seek.getMax()) {
					progress = p.getCurrentPosition()/1000;
					seek.setProgress(progress);

				}
			}

		}).start();
	}
	
	public void onClickDefaultArrow(View v){
		CommonActivity.nextIntent = new Intent(v.getContext(), TracksActivity.class);
		startActivity(CommonActivity.nextIntent);
	}
	public void onClickArrow(View v){
		list.onClickArrow(v);
	}
	public void onItemClick(View v){
		list.onItemClick(v);
	}

	private class ContactsAdapter extends ArrayAdapter<HashMap<String,Object>> {

		private ArrayList<HashMap<String,Object>> items;

		public ContactsAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String,Object>> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.contacts_item_list, null);
			}
			HashMap<String,Object> item = items.get(position);
			if (item != null) {
				ImageView img = (ImageView) v.findViewById(R.id.imgItemAvatar);
				TextView tt = (TextView) v.findViewById(R.id.text1);
				TextView bt = (TextView) v.findViewById(R.id.text2);
				if (img != null)
					img.setImageDrawable((Drawable)item.get("image"));
				if (tt != null) {
					tt.setText( (String)item.get("line1"));                            }
				if(bt != null){
					bt.setText((String)item.get("line2"));
				}

			}

			return v;
		}
	}

}
