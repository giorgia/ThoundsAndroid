package ThoundsAPIWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrackWrapper {

	private JSONObject track;

	public TrackWrapper(JSONObject track){
		this.track = track;
	}
	
	public String getCover() throws JSONException{
		return track.getString("cover");
	}
}
