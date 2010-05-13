package ThoundsAPIWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BandWrapper {
	private JSONObject band;
	private JSONArray friendList;

	public BandWrapper(JSONObject band) throws JSONException {
		this.band = band;
		friendList = band.getJSONObject("friends-collection").getJSONArray("friends");
	}
	
	public int getCurrentPage()throws JSONException{
		return band.getInt("page");
	}
	
	public int getPageTotalNumber()throws JSONException{
		return band.getInt("pages");
	}
	
	public int getFriendTotalNumber()throws JSONException{
		return band.getInt("total");
	}
	
	public int getFriendListLength()throws JSONException{
		return friendList.length();
	}
	
	public UserWrapper getFriend(int index)throws JSONException{
		JSONObject friend = friendList.getJSONObject(index);
		if(friend != null)
			return new UserWrapper(friend);
		return null;
	}
}
