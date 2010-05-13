package ThoundsAPIWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;

public class ThoundWrapper {
	private JSONObject thound;
	private JSONArray tracks;

	public ThoundWrapper(JSONObject thound) throws JSONException {
		this.thound = thound;
		tracks = thound.getJSONArray("tracks");
	}

	public int getBmp() throws JSONException {
		return thound.getInt("bmp");
	}

	public boolean isBanned() throws JSONException {
		return thound.getBoolean("banned");
	}

	public boolean hasNewTracks() throws JSONException {
		return thound.getBoolean("has_new_tracks");
	}

	public int getId() throws JSONException {
		return thound.getInt("id");
	}

	public int getLeadTrackId() throws JSONException {
		return thound.getInt("lead_track_id");
	}

	public int getMixDuration() throws JSONException {
		return thound.getInt("mix_duration");
	}

	public String getMixUrl() throws JSONException {
		return thound.getString("mix_url");
	}

	public String getPrivacy() throws JSONException {
		return thound.getString("privacy");
	}

	public String getPublicId() throws JSONException {
		return thound.getString("public_id");
	}

	public String getPublicUrl() throws JSONException {
		return thound.getString("public_url");
	}

	public String getTags() throws JSONException {
		return thound.getString("tags");
	}

	public int getUserId() throws JSONException {
		return thound.getInt("user_id");
	}

	public String getUserAvatarUrl() throws JSONException {
		return thound.getString("user_avatar");
	}

	public Drawable getUserAvatar() throws JSONException {
		String avatarUrl = thound.getString("user_avatar");
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
	
	public int getTrackListLength(){
		return tracks.length();
	}
	
	public TrackWrapper getTrack(int index) throws JSONException{
		return new TrackWrapper(tracks.getJSONObject(index));
	}
}
