package pro.android.activity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.thounds.thoundsapi.BandWrapper;
import org.thounds.thoundsapi.RequestWrapper;
import org.thounds.thoundsapi.ThoundWrapper;
import org.thounds.thoundsapi.UserWrapper;

import pro.android.R;
import pro.android.utils.ImageFromUrl;
import pro.android.utils.Player;
import pro.android.utils.ThoundsList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class ProfileActivity extends CommonActivity {

	ThoundsList list;
	int userId = -1;
	UserWrapper user;
	UserWrapper friend;
	BandWrapper band;
	ThoundWrapper thounds;
	Player p;
	ProgressBar seek;
	ListView contactsList;
	SimpleAdapter sAdapter;
	//private ArrayList<HashMap<String,String>> arrayList = new ArrayList<HashMap<String,String>>();
	//private HashMap<String,String> item = new HashMap<String,String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		currentActivity = R.id.profile;
		
		list = new ThoundsList(this);
		
		userId = getIntent().getExtras()!=null?getIntent().getExtras().getInt("userId"):-1;
	//	contactsList = (ListView) findViewById(R.id.contactsListView);
	
	//	sAdapter = new SimpleAdapter(this, arrayList, R.layout.contacts_item_list, new String[] {"line1", "line2"}, new int[]{R.id.ctext1, R.id.ctext2});
		//contactsList.setAdapter(sAdapter);


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
					//	retrievedContacts();
						
					}			
				};
				Thread thread =  new Thread(run, "retrievedContacts");
				thread.start();
				
			
			}
		});


		try {
			user = RequestWrapper.loadUserProfile();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ImageView avatar = (ImageView) findViewById(R.id.imgAvatar);
		TextView name = (TextView) findViewById(R.id.txtName);
		TextView country = (TextView) findViewById(R.id.txtCountry);
		TextView site = (TextView) findViewById(R.id.txtSite);
		//TextView blog = (TextView) findViewById(R.id.txtBolg);
		TextView tags = (TextView) findViewById(R.id.txtTags);
		TextView about = (TextView) findViewById(R.id.txtAbout);

		if(user.getAvatarUrl()!=null)
			avatar.setImageDrawable(new ImageFromUrl(this,user.getAvatarUrl()).getDrawable());
		name.setText(user.getName());
		country.setText(user.getCountry()+", "+user.getCity());
		site.setText(user.getSiteUrl()!=null?user.getSiteUrl():"");
		//blog.setText(user.getBlog());
		about.setText(user.getAbout()!=null?user.getAbout():"");
		if( user.getTagList() != null){
			String[] tagList = user.getTagList();
			String tag ="";
			for(int i=0; i<tagList.length - 1; i++){
				tag += tagList[i] + ", ";
			}
			tag += tagList[tagList.length-1];
			tags.setText(tag);
		}

		ThoundWrapper th = user.getDefaultThound();
	//	ImageButton playDef = (ImageButton) findViewById(R.id.btnPlayThound);
	//	playDef.setTag("default");
		if(th!=null){
			
			TextView line1 = (TextView) findViewById(R.id.text1);
			TextView line2 = (TextView) findViewById(R.id.text2);

			try {
				line1.setText(th.getTrack(0).getTitle());
				line2.setText("created at");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void retrievedLibrary() {
		try {
			
			if (userId == -1)
				list.setThound(RequestWrapper.loadUserLibrary().getThoundsList());
			else
				list.setThound(RequestWrapper.loadGenericUserLibrary(userId, 1, 20).getThoundsList());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		runOnUiThread(list.getReturnRes());

	}
	
//	private void retrievedContacts() {
//		try {
//			
//			if (userId == -1)
//				band = RequestWrapper.loadUserBand();
//			else
//				band = RequestWrapper.loadGenericUserBand(userId);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		runOnUiThread(returnRes);
//
//	}
	
//	private Runnable returnRes = new Runnable() {
//		public void run() {
//
//			try {
//				for(int i=0; i < band.getFriendListLength(); i++){
//					
//					try {
//						friend = band.getFriend(i);
//
//						item.put("line1",friend.getName());
//						item.put("line2",friend.getCity()+", "+friend.getCountry());
//
//						arrayList.add( item );
//						sAdapter.notifyDataSetChanged();
//
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					//retrieving_bar.setVisibility(View.INVISIBLE);
//
//					
//				}
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//	};
	public void onClickButton(View v){
		if(v.getTag().equals("default")){
			Log.d("Profile", "play default");
			try {
				if(p == null || p.getCurrentState() != Player.STATE_PLAYING){
				//	p = new Player(user.getDefaultThound().getMixUrl(), seek);
					((ImageButton) v).setImageResource(android.R.drawable.ic_media_pause);
				//	p.playAudio();
				}else{
					((ImageButton) v).setImageResource(android.R.drawable.ic_media_play);
				//	p.pauseAudio();
				}
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//else
			//list.onClickButton(v);
	}
	public void onClickItem(View v){
		//list.onClickItem(v);
	}
}
