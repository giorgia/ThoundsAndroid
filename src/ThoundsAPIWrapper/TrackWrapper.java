package ThoundsAPIWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;

public class TrackWrapper {

	private JSONObject track;

	public TrackWrapper(JSONObject track){
		this.track = track;
	}
	
	public String getCover() throws JSONException{
		return track.getString("cover");
	}
	
	public int getDelay() throws JSONException{
		return track.getInt("delay");
	}
	
	public int getDuration() throws JSONException{
		return track.getInt("duration");
	}
	
	public String getHost()throws JSONException{
		return track.getString("host");
	}
	
	public int getId() throws JSONException {
		return track.getInt("id");
	}
	
	public int getOffset() throws JSONException{
		return track.getInt("offset");
	}
	
	public String getPath()throws JSONException{
		return track.getString("path");
	}
	
	public String getPrivacy() throws JSONException {
		return track.getString("privacy");
	}
	
	public String getTags() throws JSONException {
		return track.getString("tags");
	}
	
	public int getThoundId() throws JSONException {
		return track.getInt("thound_id");
	}
	
	public String getTitle() throws JSONException {
		return track.getString("title");
	}
	
	public String getUri() throws JSONException {
		return track.getString("uri");
	}
	
	public int getUserId() throws JSONException {
		return track.getInt("user_id");
	}
	
	public String getUserAvatarUrl()throws JSONException {
		return track.getJSONObject("user").getString("avatar");
	}
	
	public Drawable getUserAvatar() throws JSONException {
		String avatarUrl = track.getJSONObject("user").getString("avatar");
		Drawable image = null;
		try {
			URL url = new URL(avatarUrl);
			InputStream is = (InputStream) url.getContent();
			image = Drawable.createFromStream(is, "src");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public String getUserCity()throws JSONException {
		return track.getJSONObject("user").getString("city");
	}
	
	public String getUserCountry()throws JSONException {
		return track.getJSONObject("user").getString("country");
	}
	
	public String getUserName()throws JSONException {
		return track.getJSONObject("user").getString("name");
	}
	
	/*------------------da fare------------------
	 * getLat
	 * getLng
	 * ------------------------------------------*/
}
