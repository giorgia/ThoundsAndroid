package pro.android.activity;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pro.android.R;
import pro.android.utils.Player;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;

public class HomeActivity extends CommonActivity  implements OnBufferingUpdateListener{

	ListView listView;
	ThoundsAdapter adapter;

	JSONObject thound;
	JSONArray thounds = null;
	JSONObject thounds_collection;


	boolean isPlaying = false;
	boolean isPaused = false;

	String mix_url = null;
	Player p;
	JSONObject json;
	ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

	int position = -1;
	int prevPosition= -1;

	SeekBar seekBar;
	ImageButton btn;
	LinearLayout retrieving_bar;
	LinearLayout vParent;
	ProgressBar pBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		currentActivity = R.id.home;
		
		Button logout = (Button) findViewById(R.id.btnLogout);
		logout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				logout();
			}
		});

		ImageButton reload = (ImageButton) findViewById(R.id.btnReload);
		reload.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});
		retrieving_bar = (LinearLayout) findViewById(R.id.retrieving_data);


		listView = (ListView) findViewById(R.id.list);
		adapter = new ThoundsAdapter(
				this,
				R.layout.thounds_item_list,
				list
		);

		listView.setAdapter(adapter);
		listView.setItemsCanFocus(true);


		Runnable run = new Runnable(){
			public void run() {
				retrievedData();     	
			}			
		};
		Thread thread =  new Thread(run, "retrievedData");
		thread.start();
		retrieving_bar.setVisibility(View.VISIBLE);


	}

	private Runnable returnRes = new Runnable() {
		public void run() {
			for(int i=0; i<thounds.length(); i++){
				HashMap<String,String> item = new HashMap<String,String>();
				try {
					//=============DA SOSTITUIRE CON METODI API THOUNDS==============
					thound = thounds.getJSONObject(i);
					item.put("line1",thound.getJSONArray("tracks").getJSONObject(0).getString("title"));
					item.put("line2",thound.getJSONArray("tracks").getJSONObject(0).getJSONObject("user").getString("name"));
					//===============================================================

					list.add( item );

					retrieving_bar.setVisibility(View.INVISIBLE);

					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	};

	private void retrievedData() {
		try {
			//=============DA SOSTITUIRE CON METODI API THOUNDS==============
			json = getJson("http://thounds.com/home");

			thounds_collection = json.getJSONObject("thounds-collection");

			thounds = thounds_collection.getJSONArray("thounds");

			//===============================================================
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			//showDialog(DIALOG_ALERT_CONNECTION);
			e.printStackTrace();
		}

		runOnUiThread(returnRes);

	}
	public void onClickButton(View v){
		Log.d("Home", "CLICK PLAY");
		LinearLayout vParentPrev = vParent;
		vParent = (LinearLayout)v.getParent().getParent();
		prevPosition = position;
		position = (Integer) v.getTag();
		pBar = (ProgressBar) ((RelativeLayout)v.getParent()).findViewById(R.id.pBarThound);
		
		seekBar = (SeekBar) findViewById(R.id.SeekBarHome);

		try {
			//Play di un nuovo Thounds
			if(prevPosition == -1 || position != prevPosition){
			
				pBar.setVisibility(View.VISIBLE);
				//Se il player e' in stato di PLAY metto in stop il player
				if(p != null && p.getCurrentState() == Player.STATE_PLAYING) {
					btn.setImageResource(android.R.drawable.ic_media_play);
					p.stopAudio();
				}

				//aspetti grafici
				btn = (ImageButton) v;
				vParent.setBackgroundColor( Color.rgb(102, 194, 255));
				if(vParentPrev!=null && !vParentPrev.equals(vParent)) 
					vParentPrev.setBackgroundColor(android.R.color.background_light);
				//Recupero l'url del thound
				mix_url = thounds.getJSONObject(position).getString("mix_url");
				//creo un nuovo player
				p = new Player(mix_url, seekBar);
				p.getMediaPlayer().setOnBufferingUpdateListener(this);
				p.getMediaPlayer().setOnCompletionListener(p);
				p.getSeekBar().setOnSeekBarChangeListener(p);
				p.playAudio();
				
				Log.d("Home", "----PLAY nuovo player");

				//isPlaying = true;
				
				btn.setImageResource(android.R.drawable.ic_media_pause);

			}else if(position == prevPosition){

				if(p.getCurrentState() == Player.STATE_PAUSED){
					p.playAudio();
					btn.setImageResource(android.R.drawable.ic_media_pause);

				}else if(p.getCurrentState() == Player.STATE_STOPPED){
					p.playAudio();
					btn.setImageResource(android.R.drawable.ic_media_pause);

				}else if(p.getCurrentState() == Player.STATE_PLAYING){
					p.pauseAudio();
					btn.setImageResource(android.R.drawable.ic_media_play);
				}
			}
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}

public void onBufferingUpdate(MediaPlayer mp, int percent) {
		
		if(percent>50 && p.getCurrentState() != Player.STATE_PLAYING){
			if(p.getOffset() != 0){
				Log.d("Player","offset  "+p.getOffset());
				mp.seekTo(p.getOffset());
			}
			pBar.setVisibility(View.INVISIBLE);
			mp.start();
			p.setCurrentState(Player.STATE_PLAYING);
			p.progressUpdater();
		}
		if(seekBar != null)
			seekBar.setSecondaryProgress(percent);


	}
	public void onClickItem(View v){

		int position = (Integer) v.getTag();

		LinearLayout vParentPrev = vParent;
		vParent = (LinearLayout)v.getParent();
		vParent.setBackgroundColor(Color.rgb(102, 194, 255));
		if(vParentPrev!=null) vParentPrev.setBackgroundColor(android.R.color.background_light);

		try {
			if(isPlaying)
				p.stopAudio();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			JSONObject obj = thounds.getJSONObject(position);
			nextIntent = new Intent(v.getContext(), TracksActivity.class);
			nextIntent.putExtra("thound", obj.toString());
			startActivity(nextIntent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private class ThoundsAdapter extends ArrayAdapter<HashMap<String,String>> {

		private ArrayList<HashMap<String,String>> items;

		public ThoundsAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String,String>> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.thounds_item_list, null);
			}
			HashMap<String,String> item = items.get(position);
			if (item != null) {
				TextView tt = (TextView) v.findViewById(R.id.text1);
				TextView bt = (TextView) v.findViewById(R.id.text2);
				ImageButton bPlay = (ImageButton) v.findViewById(R.id.btnPlayThound);
				LinearLayout lItem = (LinearLayout) v.findViewById(R.id.lThound);

				if (tt != null) {
					tt.setText( item.get("line1"));                            }
				if(bt != null){
					bt.setText(item.get("line2"));
				}
				bPlay.setTag(position);
				lItem.setTag(position);

			}
			return v;
		}
	
	}

	@Override
	public void onPause(){
		super.onPause();
	}
}

