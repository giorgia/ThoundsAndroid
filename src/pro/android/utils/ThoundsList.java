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
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class ThoundsList implements OnBufferingUpdateListener{

	private ListView listView;
	private ThoundsAdapter adapter;
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
		adapter = new ThoundsAdapter(
				activity, 
				R.layout.thound_item_list,
				listThounds
		);

		listView.setAdapter(adapter);
		retrieving_bar.setVisibility(View.VISIBLE);

		anchorView = (RelativeLayout) activity.findViewById(R.id.listLayout);
		mediaController = new MediaController(activity);
		mediaController.setAnchorView(anchorView);

		showMedia.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (p != null)
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
				mediaController.hide();
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
					int numTracks = thound.getTrackListLength();
					item.put("line2",thound.getTrack(0).getUserName() +"    +"+numTracks+(numTracks==1?" track":" tracks"));
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



	public void onItemClick(View v) {
		if(imageLed != null) 
			imageLed.setVisibility(View.INVISIBLE);
		imageLed = (ImageView) ((RelativeLayout)v).getChildAt(0);
		imageLed.setVisibility(View.VISIBLE);

		int position = (Integer) ((RelativeLayout)v.getParent()).getTag();

		try {
			if(p != null && p.isPlaying())
				p.pause();
			mix_url = thounds[position].getMixUrl();
			p = new Player(mix_url);
			p.bufferedAudio();
			if(p.getOffset() != 0)
				p.seekTo(p.getOffset());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		mediaController.setMediaPlayer(p);
		mediaController.show(p.getDuration());
		p.getDefaulMediaPlayer().setOnBufferingUpdateListener(this);
		

	}


	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		p.setBufferPercent(percent);
		
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


	public void onClickArrow(View v){
		int position = (Integer)v.getTag();
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
				v = vi.inflate(R.layout.thound_item_list, null);
			}
			HashMap<String,String> item = items.get(position);
			if (item != null) {
				TextView tt = (TextView) v.findViewById(R.id.text1);
				TextView bt = (TextView) v.findViewById(R.id.text2);
				ImageButton at = (ImageButton) v.findViewById(R.id.arrow);
								

				if (tt != null) {
					tt.setText( item.get("line1"));                       
					v.setTag(position);
				}
				if(bt != null){
					bt.setText(item.get("line2"));
				}
				if(at != null){
					at.setTag(position);
				}

			}
			return v;
		}
	}



}