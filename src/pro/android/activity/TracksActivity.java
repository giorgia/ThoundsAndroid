package pro.android.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.ThoundWrapper;
import org.thounds.thoundsapi.TrackWrapper;

import pro.android.R;
import pro.android.utils.ImageFromUrl;
import pro.android.utils.Player;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TracksActivity extends CommonActivity{

	private ListView listView;
	private TracksAdapter mAdapter;
	private ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	private ThoundWrapper thound;
	private TrackWrapper track;
	public TrackWrapper[] tracks;
	private Drawable[] imgs;

	private Player p;

	private SeekBar seek;
	private int progress;
	private int buffered = 0;
	private boolean isAllDownload = false;
	private ToggleButton tSolo = null;
	private ToggleButton tMute = null;
	private ImageButton play;
	private ImageButton rec;
	private AudioManager am;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tracks);
		currentActivity = -1;

		am =(AudioManager) this.getSystemService(AUDIO_SERVICE);
		TextView thoundTitle = (TextView)findViewById(R.id.ThoundTitle);
		TextView name = (TextView)findViewById(R.id.text1);
		TextView day = (TextView)findViewById(R.id.text2);
		TextView at = (TextView)findViewById(R.id.text3);
		ImageView cover = (ImageView)findViewById(R.id.cover);
		seek = (SeekBar) findViewById(R.id.SeekBarTracks);

		//=========Settings TITLE and DEFAULT THOUND===============
		try {
			if(obj != null)
				thound = obj;
			tracks = thound.getTracksList();

			thoundTitle.setText(tracks[0].getTitle());
			name.setText(tracks[0].getUserName());
			day.setText(tracks[0].getCreatedAt().substring(0, 10));
			at.setText(tracks[0].getCreatedAt().substring(11, 16));
			cover.setImageDrawable(new ImageFromUrl(tracks[0].getCover().equals("null")?DEFAULT_COVER_URL:tracks[0].getCover()).getDrawable());

		} catch (IllegalThoundsObjectException e) {
			// TODO Auto-generated catch block
			showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
			e.printStackTrace();
		}
		//==================================================
		listView = (ListView) findViewById(R.id.list_tracks);

		mAdapter = new TracksAdapter(
				this,
				R.layout.tracks_item_list,
				list
		);
		listView.setAdapter(mAdapter);

		p = new Player(tracks.length);
		imgs = new Drawable[tracks.length];

		//=========Downloading tracks==============
		showDialog(DIALOG_RETRIEVING_TRACKS);
		new Thread(dowloadingTracks).start();
		//=========================================

		//Button PLAY
		play = (ImageButton) findViewById(R.id.PlayTracks);
		play.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(!isAllDownload)
					runOnUiThread(dowloadingTracks);  
				else{
					if(!p.isPlaying()){
						play.setImageResource(R.drawable.ic_pause);
						playTracks();
						progressUpdater();
					}else{
						play.setImageResource(R.drawable.ic_play);
						pauseTracks();
					}		
				}

			}
		});
		//Button RECORD
		rec = (ImageButton) findViewById(R.id.RecTrack);
		rec.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				nextIntent = new Intent(v.getContext(), RecordActivity.class);
				nextIntent.putExtra("thoundId", thound.getId());
				nextIntent.putExtra("tracks", savedInstanceState);
				startActivity(nextIntent);

			}
		});

	}

	private Runnable dowloadingTracks = new Runnable(){
		public void run() {

			for(int i=0; i< tracks.length; i++){
				try {
					track = tracks[i];
					imgs[i] = new ImageFromUrl(tracks[i].getUserAvatarUrl()).getDrawable();
				
					p.setData(track.getUri(), track.getOffset(), i);
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

				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					showDialog(DIALOG_ILLEGAL_ARGUMENT_EXCEPTION);
					e.printStackTrace();
				} catch (IllegalStateException e) {
					showDialog(DIALOG_ILLEGAL_STATE_EXCEPTION);
					// TODO Auto-generated catch block
					e.printStackTrace();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					showDialog(DIALOG_GENERIC_EXCEPTION);
					e.printStackTrace();
				}
			}
			runOnUiThread(returnRes);

		}
	};

	private Runnable returnRes = new Runnable() {
		public void run() {

			for(int i=1; i< tracks.length; i++){
				final HashMap<String,Object> item = new HashMap<String,Object>();

				track = tracks[i];

				item.put("line1",track.getUserName());
				item.put("line2",track.getCreatedAt().substring(0, 10));
				item.put("line3", track.getCreatedAt().substring(11, 16));
				item.put("image", imgs[i]);
				list.add( item );

				mAdapter.notifyDataSetChanged();

			}
		}
	};

	@Override
	public void onStart(){
		super.onStart();
		Log.e("tracks", "OnStart");
	}

	@Override
	public void onPause(){
		super.onPause();
		if(p.isPlaying())
			pauseTracks();
	}


	public void playTracks() {
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

	public void pauseTracks() {
		for(int i =0; i < p.size(); i++){
			try {
				p.pause(i);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				showDialog(DIALOG_GENERIC_EXCEPTION);
				e.printStackTrace();
			}
		}

	}

	public void onClickSolo(View v){
		tSolo = (ToggleButton)v;
		int tag = Integer.parseInt(((String) tSolo.getTag()).substring(4));
		tMute = (ToggleButton)((RelativeLayout)v.getParent()).findViewWithTag("mute"+tag);
		tMute.setChecked(false);
		
		if(tSolo.isChecked()){
			p.soloAudio(am.getStreamVolume(AudioManager.STREAM_MUSIC), tag);
		}else{
			p.unsoloAudio(am.getStreamVolume(AudioManager.STREAM_MUSIC), tag);
		}
	}

	public void onClickMute(View v){

		tMute = (ToggleButton)v;
		int tag = Integer.parseInt(((String) tMute.getTag()).substring(4));
		tSolo = (ToggleButton)((RelativeLayout)v.getParent()).findViewWithTag("solo"+tag);
		tSolo.setChecked(false);
		
		if(tMute.isChecked()){
			p.muteBtnAudio(am.getStreamVolume(AudioManager.STREAM_MUSIC), tag);
		}else{
			p.unmuteBtnAudio(am.getStreamVolume(AudioManager.STREAM_MUSIC), tag);
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




	private class TracksAdapter extends ArrayAdapter<HashMap<String,Object>> {

		private ArrayList<HashMap<String,Object>> items;

		public TracksAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String,Object>> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.tracks_item_list, null);
			}
			HashMap<String,Object> item = items.get(position);
			if (item != null) {
				TextView tt = (TextView) v.findViewById(R.id.text1);
				TextView bt = (TextView) v.findViewById(R.id.text2);
				TextView td = (TextView) v.findViewById(R.id.text3);
				ImageView avatar = (ImageView) v.findViewById(R.id.ImageAvatarTrack);
				ToggleButton tSolo = (ToggleButton) v.findViewById(R.id.ToggleSolo);
				ToggleButton tMute = (ToggleButton) v.findViewById(R.id.ToggleMute);
				RelativeLayout lTrack = (RelativeLayout) v.findViewById(R.id.trackItemLayout);

				if (tt != null) {
					tt.setText( (String)item.get("line1"));                            }
				if(bt != null){
					bt.setText((String)item.get("line2"));
				}
				if(td != null){
					td.setText((String)item.get("line3"));
				}
				if(avatar != null){
					avatar.setImageDrawable((Drawable)item.get("image"));
				}
				tSolo.setTag("solo"+(position+1));
				tMute.setTag("mute"+(position+1));
				lTrack.setTag("layout"+position);

			}
			return v;
		}
	}
}
