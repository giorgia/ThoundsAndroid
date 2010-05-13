package ThoundsAPIWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeWrapper {
	private JSONObject home;
	private JSONArray thoundsList;
	
	public HomeWrapper(JSONObject home) throws JSONException {
		this.home = home;
		thoundsList = home.getJSONObject("thounds-collection").getJSONArray("thounds");
	}
	
	public UserWrapper getUser() throws JSONException{
		return new UserWrapper(home.getJSONObject("user"));
	}
	
	public int getCurrentPage()throws JSONException{
		return home.getJSONObject("thounds-collection").getInt("page");
	}
	
	public int getPageNumber()throws JSONException{
		return home.getJSONObject("thounds-collection").getInt("pages");
	}
	
	public int getThoundsNumber()throws JSONException{
		return home.getJSONObject("thounds-collection").getInt("total");
	}
	
	public int getThoundsListLength() throws JSONException{
		return thoundsList.length();
	}
	
	public ThoundWrapper getThounds(int index) throws JSONException{
		return new ThoundWrapper(thoundsList.getJSONObject(index));
	}
}
