package pro.android.activity;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pro.android.R;
import pro.android.utils.ImageFromUrl;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TracksActivity extends CommonActivity  implements SeekBar.OnSeekBarChangeListener{

	ListView listView;
	SimpleAdapter adapter;
	ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	JSONObject thound;
	JSONArray tracks;
	JSONObject track;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tracks);
		
		TextView thounder = (TextView)findViewById(R.id.Thounder);
		TextView thoundTitle = (TextView)findViewById(R.id.ThoundTitle);
		ImageView cover = (ImageView)findViewById(R.id.cover);
		
		Bundle extras = getIntent().getExtras();

		try {
			if(extras !=null)
				thound = new JSONObject(extras.getString("thound"));
		
			tracks = thound.getJSONArray("tracks");

			thoundTitle.setText(thound.getJSONArray("tracks").getJSONObject(0).getString("title"));
			thounder.setText(thound.getJSONArray("tracks").getJSONObject(0).getJSONObject("user").getString("name"));
			if(tracks.getJSONObject(0).get("cover") != null)
				cover.setImageDrawable(new ImageFromUrl(this,tracks.getJSONObject(0).getString("cover"), ""+tracks.getJSONObject(0).getInt("id")).getDrawable());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




		listView = (ListView) findViewById(R.id.list_tracks);
		adapter = new SimpleAdapter(
				this,
				list,
				R.layout.tracks_item_list,
				new String[] { "line1","line2" },
				new int[] { R.id.text1, R.id.text2 }
		);
		listView.setAdapter(adapter);


		for(int i=0; i< tracks.length(); i++){
		
			HashMap<String,String> item = new HashMap<String,String>();
			try {
				track = tracks.getJSONObject(i);
				item.put("line1",track.getJSONObject("user").getString("name"));
				item.put("line2",track.getString("created_at"));
				
				list.add( item );

				adapter.notifyDataSetChanged();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	} 

}
