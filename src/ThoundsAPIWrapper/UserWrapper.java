package ThoundsAPIWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.graphics.drawable.Drawable;
import android.util.Log;

public class UserWrapper{
	
	private JSONObject profile;
	
	public UserWrapper(JSONObject profile){
		this.profile = profile;
	}
	
	public String getName() throws JSONException{
		return profile.getString("name");
	}
	
	public String getSiteUrl() throws JSONException{
		return profile.getString("site_url");
	}
	
	public String getMail()throws JSONException{
		return profile.getString("email");
	}
	
	public String getCity()throws JSONException{
		return profile.getString("city");
	}
	
	public String getCountry()throws JSONException{
		return profile.getString("country");
	}
	
	public String getAbout()throws JSONException{
		return profile.getString("about");
	}
	
	public String getAvatarUrl() throws JSONException{
		return profile.getString("avatar");
	}
	
	public Drawable getAvatar() throws JSONException{
		String avatarUrl = profile.getString("avatar");
		Drawable image = null;
		try {
			URL url = new URL(avatarUrl);
			InputStream is = (InputStream)url.getContent();
			image = Drawable.createFromStream(is, "src");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public String[] getTagList()throws JSONException{
		JSONArray tags = profile.getJSONArray("tags");
		String tagList[] = new String[tags.length()];
		for(int i=0; i<tags.length(); i++){
			tagList[i] = tags.getJSONObject(i).getString("name");
		}
		return tagList;
	}
	
	public ThoundWrapper getDefaultThound()throws JSONException{
		JSONObject thound = profile.getJSONObject("default_thound");
		if(thound != null)
			return new ThoundWrapper(thound);
		return null;
	}
}
