package pro.android.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.ThoundWrapper;

import pro.android.R;
import pro.android.activity.CommonActivity;
import pro.android.activity.TracksActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class ThoundsList implements OnBufferingUpdateListener,OnItemClickListener, OnItemLongClickListener{

	private ListView listView;
	private SimpleAdapter adapter;
	private Activity activity;
	private ThoundWrapper[] thounds;
	private ThoundWrapper thound;

	private String mix_url = null;
	private Player p;
	private ArrayList<HashMap<String,String>> listThounds = new ArrayList<HashMap<String,String>>();
	private HashMap<String,String> item = new HashMap<String,String>();
	private MediaController mediaController;
	private RelativeLayout anchorView;
	private LinearLayout retrieving_bar;
	private TextView showMedia;
	private ImageView imageLed;

	public ThoundsList(final Activity activity) {
		this.activity = activity;
		showMedia = (TextView) activity.findViewById(R.id.txtMediaShow);

		retrieving_bar = (LinearLayout) activity.findViewById(R.id.retrieving_data);

		listView = (ListView) activity.findViewById(R.id.list);
		adapter = new SimpleAdapter(
				activity,listThounds,
				R.layout.thound_item_list,
				new String[] { "line1", "line2" },
				new int[] { R.id.text1, R.id.text2 }

		);

		listView.setAdapter(adapter);
		listView.setItemsCanFocus(true);
		
		retrieving_bar.setVisibility(View.VISIBLE);

		anchorView = (RelativeLayout) activity.findViewById(R.id.listLayout);
		mediaController = new MediaController(activity);
		mediaController.setAnchorView(anchorView);

		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		//listView.setOnItemSelectedListener(this);
		showMedia.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mediaController.show(p.getDuration());
			}
		});
		listView.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {

				if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL ){
					mediaController.hide();
				}	
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				//mediaController.show(p.getDuration());
			}
		});
	}

	private Runnable returnRes = new Runnable() {
		public void run() {

			for(int i=0; i < thounds.length; i++){
				item = new HashMap<String,String>();
				try {
					thound = thounds[i];

					item.put("line1",thound.getTrack(0).getTitle());
					item.put("line2",thound.getTrack(0).getUserName());
					listThounds.add( item );
					adapter.notifyDataSetChanged();
					
				} catch (IllegalThoundsObjectException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				retrieving_bar.setVisibility(View.INVISIBLE);

			}
			
		}
	};

	

	public void onItemClick(AdapterView<?> parent, View v, int position,
			long id) {
		
//		if(imageLed != null) 
//			imageLed.setVisibility(View.INVISIBLE);
//		adapter.notifyDataSetChanged();
//		imageLed = (ImageView) ((RelativeLayout)v).getChildAt(0);
//		Log.d("thounds","imageLed: "+imageLed);
//		Log.d("thounds","position: "+position);
//		imageLed.setVisibility(View.VISIBLE);
//		adapter.notifyDataSetChanged();

		//this.position = position;
		//View vParentPrev = v;
		//vParent = (RelativeLayout)v;
		listView.getChildAt(position).setBackgroundColor(Color.rgb(102, 194, 255));
		//if(vParentPrev!=null) vParentPrev.setBackgroundColor(android.R.color.background_light);
		try {
			if(p != null && p.isPlaying())
				p.pause();
			mix_url = thounds[position].getMixUrl();
			p = new Player(mix_url);
			p.getDefaulMediaPlayer().setOnBufferingUpdateListener(this);
			p.bufferedAudio();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		mediaController.setMediaPlayer(p);

		

	}


	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		p.setBufferPercent(percent);
		if(percent>50 && !p.isPlaying()){
			if(p.getOffset() != 0){
				p.seekTo(p.getOffset());
			}
			p.start();
			p.setCurrentState(Player.STATE_PLAYING);

		}
		//Log.d("thoundList" , ""+p.getBufferPercentage());
		mediaController.show(p.getDuration());
		//	if(seekBar != null)
		//	seekBar.setSecondaryProgress(percent);
		//activity.runOnUiThread(updateProgress);
	}


	public Runnable getReturnRes() {
		// TODO Auto-generated method stub
		return returnRes;
	}


	public ThoundWrapper[] getThounds() {
		return thounds;
	}

	public void setThound(ThoundWrapper[] thounds) {
		this.thounds = thounds;
	}


	public boolean onItemLongClick(AdapterView<?> arg0, View v, int position,
			long arg3) {
		CommonActivity.obj = thounds[position];

		try {
			if(p!=null && p.isPlaying()){
				Log.d("thounds","isplaying"+position );
				p.pause();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}


		CommonActivity.nextIntent = new Intent(v.getContext(), TracksActivity.class);
		//CommonActivity.nextIntent.putExtra("thound", obj);
		activity.startActivity(CommonActivity.nextIntent);



		return false;
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
				LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.tracks_item_list, null);
			}
			HashMap<String,String> item = items.get(position);
			if (item != null) {
				TextView tt = (TextView) v.findViewById(R.id.text1);
				TextView bt = (TextView) v.findViewById(R.id.text2);
				
				if (tt != null) {
					tt.setText( item.get("line1"));                            }
				if(bt != null){
					bt.setText(item.get("line2"));
				}
				
			}
			return v;
		}
	}



}