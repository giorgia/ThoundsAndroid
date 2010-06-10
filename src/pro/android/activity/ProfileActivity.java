package pro.android.activity;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;


public class ProfileActivity extends CommonActivity {

	ThoundsList list;
	UserWrapper user;
	ThoundWrapper thounds;
	Player p;
	ProgressBar seek;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		currentActivity = R.id.profile;

		list = new ThoundsList(this);
		
		

		final ScrollView lInfo = (ScrollView) findViewById(R.id.scrollInfo);
		final LinearLayout lLibrary = (LinearLayout) findViewById(R.id.lLibrary);
		//final LinearLayout lContacts = (LinearLayout) findViewById(R.id.);

		final Button info = (Button) findViewById(R.id.btnInfo);
		final Button library = (Button) findViewById(R.id.btnLibrary);
		final Button contacts = (Button) findViewById(R.id.btnContacts);
		seek = (ProgressBar) findViewById(R.id.ProgressDefault);

		info.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				lInfo.setVisibility(View.VISIBLE);
				lLibrary.setVisibility(View.INVISIBLE);
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
				library.setBackgroundResource(R.layout.shape_darkgray_center);
				library.setTextColor(Color.WHITE);
				info.setBackgroundResource(R.layout.shape_gray_left);
				info.setTextColor(Color.BLACK);
				contacts.setBackgroundResource(R.layout.shape_gray_right);
				contacts.setTextColor(Color.BLACK);
				
				Runnable run = new Runnable(){
					public void run() {
						retrievedData();
						Log.d("Profile", "dopo retireved");
					}			
				};
				Thread thread =  new Thread(run, "retrievedData");
				thread.start();
				
			}


		});

		contacts.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				lInfo.setVisibility(View.INVISIBLE);
				lLibrary.setVisibility(View.INVISIBLE);
				contacts.setBackgroundResource(R.layout.shape_darkgray_right);
				contacts.setTextColor(Color.WHITE);
				info.setBackgroundResource(R.layout.shape_gray_left);
				info.setTextColor(Color.BLACK);
				library.setBackgroundResource(R.layout.shape_gray_center);
				library.setTextColor(Color.BLACK);
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

	private void retrievedData() {
		try {
			//			
			//			ThoundsCollection collection = RequestWrapper.loadUserLibrary();
			list.setThound(RequestWrapper.loadUserLibrary().getThoundsList());

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
