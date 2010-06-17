package pro.android.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.json.JSONException;
import org.thounds.thoundsapi.IllegalThoundsObjectException;
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

	private ListView listView;
	private TracksAdapter mAdapter;
	private ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	private ThoundWrapper thound;
	private TrackWrapper track;
	private TrackWrapper[] tracks;

	private Player p;

	private SeekBar seek;
	private int progress;
	private int buffered = 0;
	private boolean isAllDownload = false;
	private View itemView;
	private ToggleButton tSolo = null;
	private ToggleButton tMute = null;
	private AudioManager am;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tracks);
		currentActivity = -1;

		am =(AudioManager) this.getSystemService(AUDIO_SERVICE);
		TextView thoundTitle = (TextView)findViewById(R.id.ThoundTitle);
		ImageView cover = (ImageView)findViewById(R.id.cover);
		seek = (SeekBar) findViewById(R.id.SeekBarTracks);

		try {
			if(obj != null)
				thound = obj;
			tracks = thound.getTracksList();

			thoundTitle.setText(tracks[0].getTitle());

			if(tracks[0].getCover() != null)
				cover.setImageDrawable(new ImageFromUrl(tracks[0].getCover()).getDrawable());

		} catch (IllegalThoundsObjectException e) {
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

		p = new Player(tracks.length);

		for(int i=0; i< tracks.length; i++){

			final HashMap<String,String> item = new HashMap<String,String>();
			try {
				track = tracks[i];

				p.setData(track.getUri(), track.getOffset(), i);

				item.put("line1",track.getUserName());
				item.put("line2", track.getCreatedAt());

				list.add( item );

				mAdapter.notifyDataSetChanged();

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

		final Button play = (Button) findViewById(R.id.PlayTracks);
		play.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(!p.isPlaying()){
					play.setText("PAUSE");
					playTracks();
					progressUpdater();
				}else{
					play.setText("PLAY");
					pauseTracks();
				}		
			}
		});
	}

	@Override
	public void onStart(){
		super.onStart();
		showDialog(DIALOG_RETRIEVING_TRACKS);
		new Thread(downlodingTracks).start();
	}

	@Override
	public void onPause(){
		super.onPause();
		if(p.isPlaying())
			pauseTracks();
	}
	private Runnable downlodingTracks  = new Runnable(){

		public void run() {

			for(int i =0; i < p.size(); i++ ){

				try {
					p.getMediaPlayer(i).setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

						public void onBufferingUpdate(MediaPlayer mp, int percent) {

							if(percent == 100){
								buffered++;		
								if(buffered == p.size()){
									isAllDownload = true;
									dismissDialog(DIALOG_RETRIEVING_TRACKS);

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
