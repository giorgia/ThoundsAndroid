package pro.android.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.thounds.thoundsapi.BandWrapper;
import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.Thounds;
import org.thounds.thoundsapi.ThoundWrapper;
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.ThoundsNotAuthenticatedexception;
import org.thounds.thoundsapi.UserWrapper;

import pro.android.R;
import pro.android.utils.ImageFromUrl;
import pro.android.utils.Player;
import pro.android.utils.ThoundsList;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;


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
		
		ImageView avatar = (ImageView) findViewById(R.id.imgAvatar);
		TextView name = (TextView) findViewById(R.id.txtName);
		TextView country = (TextView) findViewById(R.id.txtCountry);
		TextView site = (TextView) findViewById(R.id.txtSite);
		TextView blog = (TextView) findViewById(R.id.txtBolg);
		TextView tags = (TextView) findViewById(R.id.txtTags);
		TextView about = (TextView) findViewById(R.id.txtAbout);
		
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
		//=========== SETUP THE TABS ======================================
		TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
		tabHost.setup();

		TabSpec spec1=tabHost.newTabSpec("info");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator("info", getResources().getDrawable(R.drawable.tab_info));

		TabSpec spec2=tabHost.newTabSpec("library");
		spec2.setIndicator("library", getResources().getDrawable(R.drawable.tab_library));
		spec2.setContent(R.id.tab2);

		TabSpec spec3=tabHost.newTabSpec("contacts");
		spec3.setIndicator("contacts", getResources().getDrawable(R.drawable.tab_contacts));
		spec3.setContent(R.id.tab3);

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
		
		
		//-----------------TAB LISTNER ----------------------------------------
		tabHost.setOnTabChangedListener(new OnTabChangeListener(){

			public void onTabChanged(String tagId) {
				if(tagId.equals("info")){

				}else if (tagId.equals("library")){
					Runnable run = new Runnable(){
						public void run() {
							retrievedLibrary();
						}			
					};
					Thread thread =  new Thread(run, "retrievedLibrary");
					thread.start();
				}else{
					Runnable run = new Runnable(){
						public void run() {
							retrievedContacts();
						}			
					};
					Thread thread =  new Thread(run, "retrievedContacts");
					thread.start();

					showDialog(DIALOG_RETRIEVING_THOUNDS);
				}
			}
		});

		//---------------- Request User Info ----------------------------
		try {
			if(userId == -1)
				user = Thounds.loadUserProfile();
			else{
				Log.d("ID",""+userId);
				user = Thounds.loadGenericUserProfile(userId);
			}
		} catch (ThoundsConnectionException e1) {
			// TODO Auto-generated catch block
			//	e1.printStackTrace();
			showDialog(DIALOG_ALERT_CONNECTION);

		} catch (IllegalThoundsObjectException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
		} catch (ThoundsNotAuthenticatedexception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//------- Setting Header ------------------------
		Button logout = (Button) findViewById(R.id.btn_logout);
		logout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				StopNotification();
				logout();
			}
		});
		
		TextView userName = (TextView) findViewById(R.id.UserName);
		userName.setText(user.getName());

		//--------------- Setting User Info -----------------------
		if(user.getAvatarUrl()!=null)
			avatar.setImageDrawable(new ImageFromUrl(user.getAvatarUrl()).getDrawable());
		name.setText(user.getName());
		country.setText(user.getCountry()+", "+user.getCity());
		site.setText(user.getSiteUrl()!=null?user.getSiteUrl():"--");
		//blog.setText(user.!=null?user.getBlog():"--");
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
			Log.d("Profile Activity - getDefaultThound", "IllegalThoundsObjectException");
			showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
			//e.printStackTrace();
		}


	}

	private void retrievedLibrary() {
		try {
			ThoundWrapper[] ths;
			if (userId == -1){
				ths = Thounds.loadUserLibrary().getThoundsList();	
			}else{
				ths = Thounds.loadGenericUserLibrary(userId, 1, 20).getThoundsList();
			}
			Drawable[] imgs = new Drawable[ths.length];
			for(int i = 0; i < ths.length; i++){
				imgs[i] = new ImageFromUrl(ths[i].getTrack(0).getCover().equals("null")?DEFAULT_COVER_URL:ths[i].getTrack(0).getCover()).getDrawable();
			}
			list.setThound(ths);
			list.setAvatars(imgs);
		} catch (IllegalThoundsObjectException e) {
			showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ThoundsConnectionException e) {
			// TODO Auto-generated catch block
			showDialog(DIALOG_ALERT_CONNECTION);
			e.printStackTrace();
		} catch (ThoundsNotAuthenticatedexception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		runOnUiThread(list.getReturnRes());
	}

	private void retrievedContacts() {
		try {

			if (userId == -1)
				band = Thounds.loadUserBand();
			else
				band = Thounds.loadGenericUserBand(userId);
		} catch (ThoundsConnectionException e) {
			// TODO Auto-generated catch block
			showDialog(DIALOG_ALERT_CONNECTION);
			e.printStackTrace();
		} catch (IllegalThoundsObjectException e) {
			// TODO Auto-generated catch block
			showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
			e.printStackTrace();
		} catch (ThoundsNotAuthenticatedexception e) {
			// TODO Auto-generated catch block
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

	public void onClickButton(final View v){

		Log.d("Profile", "play default");
		try {
			if(p == null || !p.isPlaying()){
				p = new Player(user.getDefaultThound().getMixUrl());
				new Thread(p.getBufferedAudio()).start();
				p.getDefaulMediaPlayer().setOnBufferingUpdateListener(new OnBufferingUpdateListener(){

					public void onBufferingUpdate(MediaPlayer mp, int percent) {
						seek.setSecondaryProgress(percent);
						if(percent > 50){
							((ImageButton) v).setImageResource(android.R.drawable.ic_media_pause);
							p.start();
							progressUpdater();
						}
					}});
				
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
	//==================================================================================================
	//===============================================ADAPTER===================================================

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
