package pro.android.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.ThoundWrapper;

import pro.android.R;
import pro.android.activity.CommonActivity;
import pro.android.activity.TracksActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class ThoundsList implements OnBufferingUpdateListener{

	private ListView listView;
	private ThoundsAdapter adapter;
	private Activity activity;
	private ThoundWrapper[] thounds;
	private ThoundWrapper thound;
	private Drawable[] imgs;

	private String mix_url = null;
	private Player p;
	private ArrayList<HashMap<String,Object>> listThounds = new ArrayList<HashMap<String,Object>>();
	private HashMap<String, Object> item = new HashMap<String,Object>();
	private MediaController mediaController;
	private RelativeLayout anchorView;
	private LinearLayout retrieving_bar;
	private TextView showMedia;
	private ImageView imgAvatar;
	private ProgressBar pgrThound;

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
					mediaController.show(10000);
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
				mediaController.hide();
			}
		});
	}

	private Runnable returnRes = new Runnable() {
		public void run() {

			for(int i=0; thounds != null && i < thounds.length; i++){
				item = new HashMap<String,Object>();
				try {
					thound = thounds[i];
					int numTracks = thound.getTrackListLength()-1;
					item.put("image", imgs[i]);
					item.put("line1",thound.getTrack(0).getTitle());
					item.put("line2",thound.getTrack(0).getUserName());
					if(numTracks != 0)
						item.put("line3","+"+numTracks+(numTracks==1?" line":" lines"));
					else 
						item.put("line3","");
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
		//pgrThound = (ProgressBar) v.findViewById(R.id.pgrThound);
		//if(imageLed != null) 
		//	imageLed.setVisibility(View.INVISIBLE);
		//imageLed = (ImageView) v.findViewById(R.id.imgLed);


		int position = (Integer)v.getTag();
		
		Log.d("LISTITEM", "list item position: " + position);
//		Log.d("LISTITEM", "list item (v) position: " + v.getTag());
//		Log.d("LISTITEM", "v.getClass: " + v.getClass().toString());
//		Log.d("LISTITEM", "v.getParent.getClass: " + v.getParent().toString());

//		pgrThound =(ProgressBar) ((RelativeLayout)v.getParent()).getChildAt(0);
	
//		if ((Integer)((RelativeLayout)pgrThound.getParent()).getTag() == position) {
//			pgrThound.setVisibility(View.VISIBLE);
//		}

		pgrThound = (ProgressBar) v.findViewWithTag("progress"+position);
		pgrThound.setVisibility(View.VISIBLE);

		imgAvatar = (ImageView) v.findViewWithTag("avatar"+position);
		imgAvatar.setVisibility(View.INVISIBLE);
		try {
			if(p != null && p.isPlaying())
				p.pause();
			mix_url = thounds[position].getMixUrl();

			p = new Player(mix_url);
			p.getDefaulMediaPlayer().setOnBufferingUpdateListener(this);

			new Thread(p.getBufferedAudio()).start();
			if(p.getOffset() != 0)
				p.seekTo(p.getOffset());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mediaController.setMediaPlayer(p);
	}

	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		p.setBufferPercent(percent);
		if(percent > 50){
			p.start();
			pgrThound.setVisibility(View.INVISIBLE);
			imgAvatar.setVisibility(View.VISIBLE);
			mediaController.show(10000);
		}
	}


	public Runnable getReturnRes() {
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


	private class ThoundsAdapter extends ArrayAdapter<HashMap<String,Object>> {

		private ArrayList<HashMap<String,Object>> items;

		public ThoundsAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String,Object>> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.thound_item_list, null);
			}
			HashMap<String,Object> item = items.get(position);
			if (item != null) {
				TextView tt = (TextView) v.findViewById(R.id.text1);
				TextView bt = (TextView) v.findViewById(R.id.text2);
				TextView tl = (TextView) v.findViewById(R.id.text3);
				ImageButton at = (ImageButton) v.findViewById(R.id.arrow);
				ImageView img = (ImageView) v.findViewById(R.id.imgAvatar);
				ProgressBar pr = (ProgressBar) v.findViewById(R.id.pgrThound);
				if (img != null){
					img.setImageDrawable((Drawable)item.get("image"));
					img.setTag("avatar"+position);
				}
				if (tt != null) {
					tt.setText( (CharSequence) item.get("line1"));                       
					v.setTag(position);
				}
				if(bt != null){
					bt.setText((CharSequence) item.get("line2"));
				}
				if(tl != null){
					tl.setText((CharSequence) item.get("line3"));
				}
				if(at != null){
					at.setTag(position);
				}
				if(pr != null){
					pr.setTag("progress"+position);
				}

			}
			return v;
		}
	}


	public void setAvatars(Drawable[] imgs) {
		// TODO Auto-generated method stub
		this.imgs = imgs;
	}



}