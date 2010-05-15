package pro.android.activity;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pro.android.R;
import pro.android.utils.ImageFromUrl;
import pro.android.utils.Player;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TracksActivity extends CommonActivity{

	ListView listView;
	//SimpleAdapter adapter;
	TracksAdapter mAdapter;
	ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	JSONObject thound;
	JSONArray jsTracks;
	JSONObject track;
	Vector<Player> tracks;
	SeekBar seek;
	int buffered = 0;
	boolean isAllDownload = false;
	View itemView;
	LinearLayout lDowloading;
	LinearLayout lProgress;
	ToggleButton tSolo = null;
	ToggleButton tMute = null;
	LinearLayout lTrack = null;
	AudioManager am;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tracks);
		currentActivity = -1;
		
		am =(AudioManager) this.getSystemService(AUDIO_SERVICE);
		TextView thoundTitle = (TextView)findViewById(R.id.ThoundTitle);
		ImageView cover = (ImageView)findViewById(R.id.cover);
		seek = (SeekBar) findViewById(R.id.SeekBarTracks);
		lDowloading = (LinearLayout) findViewById(R.id.TracksDownloading);
		lProgress = (LinearLayout) findViewById(R.id.TracksProgress);

		Bundle extras = getIntent().getExtras();
		//=============DA SOSTITUIRE CON METODI API THOUNDS==============
		try {
			if(extras !=null)
				thound = new JSONObject(extras.getString("thound"));

			jsTracks = thound.getJSONArray("tracks");

			thoundTitle.setText(thound.getJSONArray("tracks").getJSONObject(0).getString("title"));

			if(!jsTracks.getJSONObject(0).get("cover").equals(null))
				cover.setImageDrawable(new ImageFromUrl(this,jsTracks.getJSONObject(0).getString("cover"), ""+jsTracks.getJSONObject(0).getInt("id")).getDrawable());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//================================================================

		listView = (ListView) findViewById(R.id.list_tracks);
		mAdapter = new TracksAdapter(
				this,
				R.layout.tracks_item_list,
				list
		);
		listView.setAdapter(mAdapter);

		tracks = new Vector<Player>(jsTracks.length());

		for(int i=0; i< jsTracks.length(); i++){

			final HashMap<String,String> item = new HashMap<String,String>();
			try {
				track = jsTracks.getJSONObject(i);

				//=============DA SOSTITUIRE CON METODI API THOUNDS==============
				tracks.add(new Player(track.getString("uri"), track.getInt("offset")));
				item.put("line1",track.getJSONObject("user").getString("name"));
				item.put("line2",track.getString("created_at"));
				//===============================================================

				list.add( item );

				mAdapter.notifyDataSetChanged();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}
	
		
		tracks.elementAt(0).setSeekBar(seek);

		Button play = (Button) findViewById(R.id.PlayTracks);
		play.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				new Thread(downlodingTracks).start();
				lDowloading.setVisibility(View.VISIBLE);
				lProgress.setVisibility(View.INVISIBLE);
			}
		});
	}


	private Runnable downlodingTracks  = new Runnable(){

		public void run() {

			for(int i =0; i < tracks.size(); i++){
				
				try {
					
					tracks.elementAt(i).bufferedAudio();
					tracks.elementAt(i).getMediaPlayer().setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

						public void onBufferingUpdate(MediaPlayer mp, int percent) {
							if(percent == 100){
								buffered++;
								for(int j=0;  j < tracks.size(); j++)
									if(tracks.elementAt(j).equals(mp)){
										
										lTrack = (LinearLayout) listView.findViewWithTag("layout"+j);
										lTrack.setBackgroundColor(Color.LTGRAY);
									}
								Log.d("Tracks"," -----> Buffered =" +buffered);
								if(buffered == tracks.size()){
									isAllDownload = true;
									Log.d("Tracks"," -------->isAllDownload =" +isAllDownload);

									playTracks();
								}
							}
						}

					});
					
				

				}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	};

	private void playTracks() {
		if(isAllDownload){
			lDowloading.setVisibility(View.INVISIBLE);
			lProgress.setVisibility(View.VISIBLE);
			Log.d("Tracks"," playTracks CALL");
			for(int i =0; i < tracks.size(); i++){
				try {
					tracks.elementAt(i).playAudio();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void pauseTracks() {
		for(int i =0; i < tracks.size(); i++){
			try {
				tracks.elementAt(i).pauseAudio();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	public void onClickSolo(View v){
		if(tSolo!=null)
			tSolo.setChecked(false);
		tSolo = (ToggleButton)v;
		ToggleButton tsMute;
		int tag = Integer.parseInt(((String) tSolo.getTag()).substring(4));
		if(tSolo.isChecked()){
			for(int i = 0; i< tracks.size(); i++){
				tsMute = (ToggleButton) listView.findViewWithTag("mute"+i);
				if(i != tag){
					tracks.elementAt(i).muteAudio();
					tsMute.setChecked(true);
				}else if(tsMute.isChecked()){
					tracks.elementAt(tag).unmuteAudio(am.getStreamVolume(AudioManager.STREAM_MUSIC));
					tsMute.setChecked(false);
				}
			}
		}else{
			for(int i = 0; i< tracks.size(); i++){
				tsMute = (ToggleButton) listView.findViewWithTag("mute"+i);
				tracks.elementAt(i).unmuteAudio(am.getStreamVolume(AudioManager.STREAM_MUSIC));
				tsMute.setChecked(false);
			}
		}
	}
	
	public void onClickMute(View v){
		tMute = (ToggleButton)v;
		int tag = Integer.parseInt(((String) tMute.getTag()).substring(4));
		if(tMute.isChecked()){
			tracks.elementAt(tag).muteAudio();

		}else{
			tracks.elementAt(tag).unmuteAudio(am.getStreamVolume(AudioManager.STREAM_MUSIC));
		}
	}


	@Override
	public void onPause(){
		super.onPause();
		pauseTracks();
	}

	private class TracksAdapter extends ArrayAdapter<HashMap<String,String>> {

		private ArrayList<HashMap<String,String>> items;

		public TracksAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String,String>> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.tracks_item_list, null);
			}
			HashMap<String,String> item = items.get(position);
			if (item != null) {
				TextView tt = (TextView) v.findViewById(R.id.text1);
				TextView bt = (TextView) v.findViewById(R.id.text2);
				ToggleButton tSolo = (ToggleButton) v.findViewById(R.id.ToggleSolo);
				ToggleButton tMute = (ToggleButton) v.findViewById(R.id.ToggleMute);
				LinearLayout lTrack = (LinearLayout) v.findViewById(R.id.trackItemLayout);
				
				if (tt != null) {
					tt.setText( item.get("line1"));                            }
				if(bt != null){
					bt.setText(item.get("line2"));
				}
				tSolo.setTag("solo"+position);
				tMute.setTag("mute"+position);
				lTrack.setTag("layout"+position);
				
			}
			return v;
		}
	}
}
