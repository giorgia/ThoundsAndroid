package ThoundsAPIWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RequestWrapper {
	private static String HOST = "http://thounds.com/";
	private static String PROFILE_PATH = "profile";
	private static String HOME_PATH = "home";
	private static String USERS_PATH = "users";
	private static String BAND_PATH = "band";
	private static String FRIENDSHIPS_PATH = "friendships";
	private static String THOUNDS_PATH = "thounds";

	protected static String USERNAME = "";
	protected static String PASSWORD = "";
	private static boolean isLogged = false;

	private static DefaultHttpClient getHttpClient(boolean useAuthentication) {
		DefaultHttpClient client = new DefaultHttpClient();

		HttpProtocolParams.setUseExpectContinue(client.getParams(), false);
		if (useAuthentication)
			client.getCredentialsProvider().setCredentials(
					new AuthScope(null, 80, "thounds"),
					new UsernamePasswordCredentials(USERNAME, PASSWORD));
		return client;
	}

	private static JSONObject httpResponseToJSONObject(HttpResponse response)
			throws IllegalStateException, IOException, JSONException {
		BufferedReader in = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer sb = new StringBuffer("");
		String line = "";
		String NL = System.getProperty("line.separator");
		while ((line = in.readLine()) != null) {
			sb.append(line + NL);
		}
		in.close();

		String result = sb.toString();

		return new JSONObject(result);
	}

	public static boolean login(String username, String password) {
		USERNAME = username;
		PASSWORD = password;
		JSONObject json;
		try {
			DefaultHttpClient client = getHttpClient(true);
			HttpGet httpget = new HttpGet();
			httpget.addHeader("Accept", "application/json");
			httpget.setURI(new URI(HOST + PROFILE_PATH));
			HttpResponse response = client.execute(httpget);
			json = httpResponseToJSONObject(response);

			if ((json == null) || (!json.getString("email").equals(username))) {
				USERNAME = "";
				PASSWORD = "";
				return false;
			} else {
				isLogged = true;
				return true;
			}
		} catch (JSONException e) {
			Log.d("LOGIN", "Catch JSONException");
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("LOGIN", "Catch Exception");
		}
		return false;
	}

	public static boolean isLogged() {
		return isLogged;
	}

	public static UserWrapper loadUserProfile() throws ClientProtocolException,
			URISyntaxException, IOException, JSONException {
		DefaultHttpClient client = getHttpClient(true);
		HttpGet httpget = new HttpGet();
		httpget.addHeader("Accept", "application/json");
		httpget.setURI(new URI(HOST + PROFILE_PATH));
		HttpResponse response = client.execute(httpget);
		return new UserWrapper(httpResponseToJSONObject(response));
	}

	public static UserWrapper loadGenericUserProfile(int userId)
			throws ClientProtocolException, URISyntaxException, IOException,
			JSONException {
		DefaultHttpClient client = getHttpClient(true);
		HttpGet httpget = new HttpGet();
		httpget.addHeader("Accept", "application/json");
		httpget.setURI(new URI(HOST + USERS_PATH + "/"
				+ Integer.toString(userId)));
		HttpResponse response = client.execute(httpget);
		return new UserWrapper(httpResponseToJSONObject(response));
	}

	public static BandWrapper loadUserBand() throws ClientProtocolException,
			URISyntaxException, IOException, JSONException {
		return loadUserBand(1, 10);
	}

	public static BandWrapper loadUserBand(int page, int perPage)
			throws ClientProtocolException, URISyntaxException, IOException,
			JSONException {
		DefaultHttpClient client = getHttpClient(true);

		StringBuilder uriBuilder = new StringBuilder(HOST + PROFILE_PATH + "/"
				+ BAND_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));

		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = client.execute(httpget);
		return new BandWrapper(httpResponseToJSONObject(response));
	}

	public static BandWrapper loadGenericUserBand(int userId)
			throws ClientProtocolException, URISyntaxException, IOException,
			JSONException {
		return loadGenericUserBand(userId, 1, 10);
	}

	public static BandWrapper loadGenericUserBand(int userId, int page,
			int perPage) throws ClientProtocolException, URISyntaxException,
			IOException, JSONException {
		DefaultHttpClient client = getHttpClient(true);

		StringBuilder uriBuilder = new StringBuilder(HOST + USERS_PATH + "/"
				+ Integer.toString(userId) + BAND_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));

		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");
		HttpResponse response = client.execute(httpget);
		return new BandWrapper(httpResponseToJSONObject(response));
	}

	public static boolean registrateUser(String name, String mail,
			String country, String city, String tags)
			throws ClientProtocolException, URISyntaxException, IOException,
			JSONException {

		JSONObject userJSON = new JSONObject();
		JSONObject userFieldJSON = new JSONObject();
		userFieldJSON.put("name", name);
		userFieldJSON.put("email", mail);
		userFieldJSON.put("country", country);
		userFieldJSON.put("city", city);
		userFieldJSON.put("tags", tags);
		userJSON.put("user", userFieldJSON);

		// -------------------------------------------

		DefaultHttpClient client = getHttpClient(false);

		HttpPost httppost = new HttpPost();
		httppost.setURI(new URI(HOST + USERS_PATH));
		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("Content-type", "application/json");
		StringEntity se = new StringEntity(userJSON.toString());
		httppost.setEntity(se);

		HttpResponse response = client.execute(httppost);

		Log.e("PROVA", response.getStatusLine().toString());
		return (response.getStatusLine().getStatusCode() == 201);
	}

	public static HomeWrapper loadHome() throws ClientProtocolException,
			URISyntaxException, IOException, JSONException {
		return loadHome(1, 10);
	}

	public static HomeWrapper loadHome(int page, int perPage)
			throws ClientProtocolException, URISyntaxException, IOException,
			JSONException {
		DefaultHttpClient client = getHttpClient(true);

		StringBuilder uriBuilder = new StringBuilder(HOST + HOME_PATH);
		uriBuilder.append("?page=" + Integer.toString(page));
		uriBuilder.append("&per_page=" + Integer.toString(perPage));

		HttpGet httpget = new HttpGet(uriBuilder.toString());
		httpget.addHeader("Accept", "application/json");

		HttpResponse response = client.execute(httpget);
		return new HomeWrapper(httpResponseToJSONObject(response));
	}

	public static boolean friendshipRequest(int userId)
			throws ClientProtocolException, URISyntaxException, IOException,
			JSONException {

		DefaultHttpClient client = getHttpClient(true);

		HttpPost httppost = new HttpPost();
		httppost.setURI(new URI(HOST + USERS_PATH + "/"
				+ Integer.toString(userId) + "/" + FRIENDSHIPS_PATH));
		httppost.addHeader("Accept", "application/json");

		HttpResponse response = client.execute(httppost);

		Log.e("PROVA", response.getStatusLine().toString());
		return (response.getStatusLine().getStatusCode() == 201);
	}

	public static void acceptFriendship(int friendshipId)
			throws ClientProtocolException, URISyntaxException, IOException,
			JSONException {

		DefaultHttpClient client = getHttpClient(true);

		HttpPut httpput = new HttpPut();
		httpput.setURI(new URI(HOST + PROFILE_PATH + "/" + FRIENDSHIPS_PATH
				+ "/" + Integer.toString(friendshipId) + "?accept=true"));
		httpput.addHeader("Accept", "application/json");
		httpput.addHeader("Content-type", "application/json");

		HttpResponse response = client.execute(httpput);
	}

	public static void refuseFriendship(int friendshipId)
			throws ClientProtocolException, URISyntaxException, IOException,
			JSONException {

		DefaultHttpClient client = getHttpClient(true);

		HttpPut httpput = new HttpPut();
		httpput.setURI(new URI(HOST + PROFILE_PATH + "/" + FRIENDSHIPS_PATH
				+ "/" + Integer.toString(friendshipId)));
		httpput.addHeader("Accept", "application/json");
		httpput.addHeader("Content-type", "application/json");

		HttpResponse response = client.execute(httpput);
	}

	public static boolean removeUserFromBand(int userId)
			throws ClientProtocolException, URISyntaxException, IOException,
			JSONException {

		DefaultHttpClient client = getHttpClient(true);

		HttpDelete httpdelete = new HttpDelete();
		httpdelete.setURI(new URI(HOST + PROFILE_PATH + "/" + FRIENDSHIPS_PATH
				+ "/" + Integer.toString(userId)));
		httpdelete.addHeader("Accept", "application/json");
		httpdelete.addHeader("Content-type", "application/json");

		HttpResponse response = client.execute(httpdelete);
		return (response.getStatusLine().getStatusCode() == 200);
	}

	public static ThoundWrapper loadThounds(int thoundId)
			throws ClientProtocolException, URISyntaxException, IOException,
			JSONException {
		DefaultHttpClient client = getHttpClient(isLogged);
		HttpGet httpget = new HttpGet();
		httpget.addHeader("Accept", "application/json");
		httpget.setURI(new URI(HOST + THOUNDS_PATH + "/"
				+ Integer.toString(thoundId)));
		HttpResponse response = client.execute(httpget);
		return new ThoundWrapper(httpResponseToJSONObject(response));
	}

	public static ThoundWrapper loadThounds(String thoundHash)
			throws ClientProtocolException, URISyntaxException, IOException,
			JSONException {
		DefaultHttpClient client = getHttpClient(isLogged);
		HttpGet httpget = new HttpGet();
		httpget.addHeader("Accept", "application/json");
		httpget.setURI(new URI(HOST + THOUNDS_PATH + "/" + thoundHash));
		HttpResponse response = client.execute(httpget);
		return new ThoundWrapper(httpResponseToJSONObject(response));
	}

	public static boolean removeThound(int thoundId)
			throws ClientProtocolException, URISyntaxException, IOException,
			JSONException {

		DefaultHttpClient client = getHttpClient(true);

		HttpDelete httpdelete = new HttpDelete();
		httpdelete.setURI(new URI(HOST + THOUNDS_PATH
				+ "/" + Integer.toString(thoundId)));
		httpdelete.addHeader("Accept", "application/json");
		httpdelete.addHeader("Content-type", "application/json");

		HttpResponse response = client.execute(httpdelete);
		return (response.getStatusLine().getStatusCode() == 200);
	}
}
