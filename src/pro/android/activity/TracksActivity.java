package pro.android.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.json.JSONException;
import org.thounds.thoundsapi.ThoundWrapper;
import org.thounds.thoundsapi.TrackWrapper;

import pro.android.R;
import pro.android.utils.ImageFromUrl;
import pro.android.utils.Player;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TracksActivity extends CommonActivity{

	ListView listView;
	TracksAdapter mAdapter;
	ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	ThoundWrapper thound;
	TrackWrapper track;
	TrackWrapper[] tracks;

	//Vector<Player> tracksPlayers;
	Player p;

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

		//Bundle extras = getIntent().getExtras();
		try {
			//if(extras !=null)
			//thound = (ThoundWrapper)extras.getSerializable("thound");
			if(obj != null)
				thound = obj;
			tracks = thound.getTracksList();

			thoundTitle.setText(tracks[0].getTitle());

			//if(tracks[0].getUserAvatarUrl() != null)
			//	cover.setImageDrawable(new ImageFromUrl(this,tracks[0].getCover()).getDrawable());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		listView = (ListView) findViewById(R.id.list_tracks);
		mAdapter = new TracksAdapter(
				this,
				R.layout.tracks_item_list,
				list
		);
		listView.setAdapter(mAdapter);

		//tracksPlayers = new Vector<Player>(tracks.length);
		p = new Player(tracks.length);

		for(int i=0; i< tracks.length; i++){

			final HashMap<String,String> item = new HashMap<String,String>();
			try {
				track = tracks[i];

				p.setData(track.getUri(), track.getOffset(), i);

				item.put("line1",track.getUserName());
				item.put("line2", "data creazione");//track.getCreatedAt();

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

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}
		
	
		//tracksPlayers.elementAt(0).setSeekBar(seek);

		Button play = (Button) findViewById(R.id.PlayTracks);
		play.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				playTracks();

				//new Thread(downlodingTracks).start();
				//lDowloading.setVisibility(View.VISIBLE);
				//lProgress.setVisibility(View.INVISIBLE);
			}
		});
	}

	@Override
	public void onStart(){
		super.onStart();
		new Thread(downlodingTracks).start();
	}

	@Override
	public void onPause(){
		super.onPause();
		pauseTracks();
	}
	private Runnable downlodingTracks  = new Runnable(){

		public void run() {

			for(int i =0; i < p.size(); i++ ){

				try {

					//p.bufferedAudio();
					p.getMediaPlayer(i).setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

						public void onBufferingUpdate(MediaPlayer mp, int percent) {
							showDialog(DIALOG_RETRIEVING_TRACKS);
							if(percent == 100){
								buffered++;		
								
								//lTrack = (LinearLayout) listView.findViewWithTag("layout"+Math.random()%p.size());
								//lTrack.setBackgroundColor(Color.LTGRAY);

								Log.d("Tracks"," -----> Buffered =" +buffered);
								if(buffered == p.size()){
									isAllDownload = true;
									Log.d("Tracks"," -------->isAllDownload =" +isAllDownload);
									dismissDialog(DIALOG_RETRIEVING_TRACKS);
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
			for(int i =0; i < p.size(); i++){
				try {
					p.start(i);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void pauseTracks() {
		for(int i =0; i < p.size(); i++){
			try {
				p.pause(i);
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
			for(int i = 0; i< p.size(); i++){
				tsMute = (ToggleButton) listView.findViewWithTag("mute"+i);
				if(i != tag){
					p.muteAudio(i);
					tsMute.setChecked(true);
				}else if(tsMute.isChecked()){
					p.unmuteAudio(am.getStreamVolume(AudioManager.STREAM_MUSIC) , tag);
					tsMute.setChecked(false);
				}
			}
		}else{
			for(int i = 0; i< p.size(); i++){
				tsMute = (ToggleButton) listView.findViewWithTag("mute"+i);
				p.unmuteAudio(am.getStreamVolume(AudioManager.STREAM_MUSIC), i);
				tsMute.setChecked(false);
			}
		}
	}

	public void onClickMute(View v){
		tMute = (ToggleButton)v;
		int tag = Integer.parseInt(((String) tMute.getTag()).substring(4));
		if(tMute.isChecked()){
			p.muteAudio(tag);

		}else{
			p.unmuteAudio(am.getStreamVolume(AudioManager.STREAM_MUSIC), tag);
		}
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
