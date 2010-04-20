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
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends CommonActivity  implements SeekBar.OnSeekBarChangeListener{

	ListView listView;
	SimpleAdapter adapter;

	JSONObject thound;
	JSONArray thounds = null;
	JSONObject thounds_collection;


	boolean isPlaying = false;
	boolean isPaused = false;
	Intent nextIntent;

	String mix_url = null;
	Player p;
	JSONObject json;
	ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	SeekBar seekBar;
	ProgressBar buf ;
	ImageButton btn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		Button logout = (Button) findViewById(R.id.Button01);
		logout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				logout();
			}
		});


		listView = (ListView) findViewById(R.id.list);
		adapter = new SimpleAdapter(
				this,
				list,
				R.layout.thounds_item_list,
				new String[] { "line1","line2" },
				new int[] { R.id.text1, R.id.text2 }
		);
		listView.setAdapter(adapter);


		Runnable run = new Runnable(){
			public void run() {
				retrievedData();     	
			}			
		};
		Thread thread =  new Thread(null, run, "MagentoBackground");
		thread.start();
		showDialog(DIALOG_RETRIEVING_DATA);

	}

	private Runnable returnRes = new Runnable() {
		public void run() {
			for(int i=0; i<thounds.length(); i++){
				HashMap<String,String> item = new HashMap<String,String>();
				try {
					thound = thounds.getJSONObject(i);
					item.put("line1",thound.getJSONArray("tracks").getJSONObject(0).getString("title"));
					item.put("line2",thound.getJSONArray("tracks").getJSONObject(0).getJSONObject("user").getString("name"));

					list.add( item );

					dismissDialog(DIALOG_RETRIEVING_DATA);
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
			json = getJson("http://thounds.com/home");

			thounds_collection = json.getJSONObject("thounds-collection");
			Log.d("Home", "Collection "+thounds_collection);
			thounds = thounds_collection.getJSONArray("thounds");
			Log.d("Home", "Thounds "+thounds);
			Log.d("Home", "Thounds lenght "+thounds.length());
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

		LinearLayout vwParentRow = (LinearLayout)((LinearLayout)v.getParent()).getChildAt(1);
		String title = ((TextView)vwParentRow.getChildAt(0)).getText().toString();
		String user = ((TextView)vwParentRow.getChildAt(1)).getText().toString();

		HashMap<String,String> item = new HashMap<String,String>();
		item.put("line1",title);
		item.put("line2",user);

		//buf = (ProgressBar) v.findViewById(R.id.ProgressBuffering);
		//buf.setVisibility(View.VISIBLE);

		seekBar = (SeekBar) findViewById(R.id.SeekBar01);
		seekBar.setOnSeekBarChangeListener(this);

		try {

			if((!isPlaying && !isPaused) || !mix_url.equals(thounds.getJSONObject(list.indexOf(item)).getString("mix_url"))){
				if(isPlaying) {
					btn.setImageResource(android.R.drawable.ic_media_play);
					p.stopAudio();
				}
				btn = (ImageButton) v.findViewById(R.id.play_home);
				mix_url = thounds.getJSONObject(list.indexOf(item)).getString("mix_url");

				p = new Player(seekBar, mix_url);
				p.playAudio();
				isPlaying = true;
				btn.setImageResource(android.R.drawable.ic_media_pause);

			}else if(isPaused){
				isPlaying = true;
				isPaused = false;
				p.playAudio();
				btn.setImageResource(android.R.drawable.ic_media_pause);

			}else if(isPlaying){
				p.pauseAudio();
				isPlaying = false;
				isPaused = true;
				btn.setImageResource(android.R.drawable.ic_media_play);
			}
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}
	public void onClickItem(View v){

		try {
			if(isPlaying)
				p.stopAudio();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LinearLayout vwParentRow = (LinearLayout)((LinearLayout)v.getParent()).getChildAt(1);
		String title = ((TextView)vwParentRow.getChildAt(0)).getText().toString();
		String user = ((TextView)vwParentRow.getChildAt(1)).getText().toString();

		HashMap<String,String> item = new HashMap<String,String>();
		item.put("line1",title);
		item.put("line2",user);

		try {
			JSONObject obj = thounds.getJSONObject(list.indexOf(item));
			nextIntent = new Intent(v.getContext(), TracksActivity.class);
			nextIntent.putExtra("thound", obj.toString());
			startActivity(nextIntent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		Log.d("HOME", "onProgressChanged"+progress); 
		p.setProgress(progress);
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		Log.d("HOME", "onStart"); 
		p.pauseAudio();
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		try {
			Log.d("HOME", "onStop"); 
			p.playAudio();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}

