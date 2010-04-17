package pro.android.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

import pro.android.R;
import android.app.Activity;
import pro.android.*;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;

public class Communication {
	
	public static boolean isLogged = false;
	private String username;
	private String password;
	
    public DefaultHttpClient client;
      public Communication()
    {
    	client = new DefaultHttpClient();
    	HttpProtocolParams.setUseExpectContinue(client.getParams(), false);
    	
    }
      
      public JSONObject getRequest(String url)
      {
    	  if(username=="" || password=="")
				return null;
    	  
  		client.getCredentialsProvider().setCredentials(
  				new AuthScope(null, 80, "thounds"),
  				new UsernamePasswordCredentials(username, password));
  		Log.d("Credenziali", "username:" + username+" psw:"+password);
  		HttpResponse response = null;
  		try {
  			HttpGet httpget = new HttpGet();
  			httpget.setURI(new URI(url));

  			//httpget.addHeader("Accept", "application/json");

  			Log.d("getRequest", "executing request" + httpget.getRequestLine());

  			response = client.execute(httpget);

  			BufferedReader in = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));

  		
  			StringBuffer sb = new StringBuffer(""); 
  			String line = ""; 
  			String NL = System.getProperty("line.separator"); 
  			while ((line = in.readLine()) != null) {
  				sb.append(line + NL); 
  			}
  			in.close();

  			String result = sb.toString();
  			Log.d("request", result);

  			return new JSONObject(result);
  		
      }catch (JSONException e) {
			//Access denied
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		//return null;
    	  
      }
	
	public boolean login(String user, String psw) {
		try {		
			if(user=="" || psw=="")
				return false;
			username=user;
			password =psw;
        String url="http://thounds.com/profile"	;
		client.getCredentialsProvider().setCredentials(
				new AuthScope(null, 80, "thounds"),
				new UsernamePasswordCredentials(username, password));
		HttpResponse response = null;
		Log.d("Credenziali", "username:" + username+" psw:"+password);
			HttpGet httpget = new HttpGet();
			httpget.setURI(new URI(url));

			httpget.addHeader("Accept", "application/json");

			Log.d("LOGIN", "executing request" + httpget.getRequestLine());

			response = client.execute(httpget);

			BufferedReader in = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));

			Log.d("LOGIN","----------------------------------------");

			StringBuffer sb = new StringBuffer(""); 
			String line = ""; 
			String NL = System.getProperty("line.separator"); 
			while ((line = in.readLine()) != null) {
				sb.append(line + NL); 
			}
			in.close();

			String result = sb.toString();
			Log.d("LOGIN", result);

			JSONObject json = new JSONObject(result);

			if(json.getString("email").equals(username))
				return true;

		}catch (JSONException e) {
			//Access denied
			return false;
		} catch (Exception e) {
			Log.d("LOGIN", "eccezione:"+e.toString());
			e.printStackTrace();
			return false;
		}
		return false;
	}
	


	public boolean logout() {
		username="";
		password="";
		isLogged = false;
		/*
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.clear();
		editor.commit();
		Intent  nextIntent = new Intent(this, LoginActivity.class);
		startActivity(nextIntent);
		this.fin.finish();
		*/
		return true;
	}


}