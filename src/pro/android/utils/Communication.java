package pro.android.utils;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.*;




import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
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
	
public String signUp(String s)
{
	try
	{
	String url="http://thounds.com/users";//422
	
	Map<String,String> paramz = new HashMap<String,String>();  
	
	        paramz.put("-d", s);  
	        
	        String retString = doPOSTRequest(url, paramz); //makeRequest(url,paramz);//doPOSTRequest(url, paramz);  
	       
	           Log.i("ECCO","Return : "+retString);  
	       
	                return retString;
	       
	//client.getCredentialsProvider().setCredentials(
		//	new AuthScope(null, 80, "thounds"),
			//new UsernamePasswordCredentials(username, password));
	/*HttpResponse response = null;
	Log.d("Credenziali", "username:" + username+" psw:"+password);
		HttpPost http = new HttpPost();
		http.setURI(new URI(url));

		http.addHeader("Accept", "application/json");
		http.addHeader("Content-type","application/json");
		//HttpClientParams hcp=(HttpClientParams) DefaultedHttpParams.getDefaults();
		//client.setParams(new BasicHttpParams().setParameter("-d", s));//.setParameter("-d", "@"+s);
		//client.setEntity(new UrlEncodedFormEntity());
		Log.d("SIGNUP", "executing request" + http.getRequestLine());

		response = client.execute(http);

		BufferedReader in = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));

		Log.d("SIGNUP","----------------------------------------");

		StringBuffer sb = new StringBuffer(""); 
		String line = ""; 
		String NL = System.getProperty("line.separator"); 
		while ((line = in.readLine()
		) != null) {
			sb.append(line + NL); 
		}
		in.close();

		String result = sb.toString();
		Log.d("SIGNUP", result);
		



		//JSONObject json = new JSONObject(result);

		//Log.d("SIGNUP",json.toString());
		//if(json.getString("email").equals(username))
			//return true;
		//return true;
		return result;
		*/
	}
	catch(Exception e)
	{
	 Log.e("SIGN UP COMUNICATION",e.toString());	
	 System.out.print(e.toString());
		
	}
	//return false;
	return "";
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


	
	public static String doPOSTRequest(String url,Map<String, String> parameters)   
	
	 {  
	
	    
	
	  String returnString = null;  
	
	     // Creo un nuovo HttpClient e l'Header del post  
	
	     HttpClient httpclient = new DefaultHttpClient();  
	
	     HttpPost httppost = new HttpPost(url);  
	
	 	httppost.addHeader("Accept", "application/json");
		httppost.addHeader("Content-type","application/json");
	
	  
	
	     try {  
	
	         // aggiungo i dati alla richiesta  
	
	         List<NameValuePair> nameValuePairs = composeParametersForPostRequest(parameters);  
	
	         httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
	
	  
	
	         // Eseguo la richiesta HTTP  
	
	         HttpResponse response = httpclient.execute(httppost);  
	         int i= response.getStatusLine().getStatusCode();
	         Log.e("ECCOLO IL NUMERO---------->", Integer.toString(i));
	         if (response.getStatusLine().getStatusCode() == 200) {   
	
	          returnString = EntityUtils.toString(response.getEntity());   
	
	                response = null;   
	
	                   
	
	         }  
	
	     }/* catch (ClientProtocolException e) {  
	
	      throw new PostRequestException(e.getMessage());  
	
	     } catch (IOException e) {  
	
	      throw new PostRequestException(e.getMessage());  
	
	     }  
	     */
	 catch(Exception e)
	 {
		 System.out.println(e.toString());
		 
	 }
	       
	
	     return returnString;  
	
	   
	
	 }  
	
	
	/*** 
	#
	  * Metodo per comporre la parte di URL relativa ai parametri 
	#
	  * @param parameters : mappa di parametri 
	#
	  * @return    : Stringa contenente i parametri concatenati 
	#
	  */  
	
	 public static String composeParametersForGetRequest(Map<String, String> parameters){  
	
	  String paramStr = "";  
	
	  List<String> chiavi = new ArrayList<String>(parameters.keySet());  
	
	    
	
	  for (int i = 0 ; i < chiavi.size(); i++){  
	
	   String chiave = chiavi.get(i);  
	
	   paramStr += chiave+"=";    
	
	   paramStr += URLEncoder.encode(parameters.get(chiave));    
	
	   paramStr += "&";    
	
	  }  
	
	  return paramStr;  
	
	 }  
	
	 
	 
	 public static List<NameValuePair> composeParametersForPostRequest(Map<String,String> parameters){  
	
	    
	
	  List<String> chiavi = new ArrayList<String>(parameters.keySet());  
	
	  List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();  
	
	    
	
	  for (int i = 0 ; i < chiavi.size(); i++){  
	
	   String chiave = chiavi.get(i);  
	
	   nameValuePairs.add(new BasicNameValuePair(chiave, parameters.get(chiave)));  
	
	  }  
	
	    
	
	  return nameValuePairs;  
	
	 }  
	 
	 public static String makeRequest(String path, Map<String, String> params) 
	 {
     try
     {
	 DefaultHttpClient httpclient = new DefaultHttpClient();
	 HttpPost httpost = new HttpPost(path);
	 Iterator<?> iter = params.entrySet().iterator();

	 JSONObject holder = new JSONObject();

	 while(iter.hasNext()) {
	 Map.Entry pairs = (Map.Entry)iter.next();
	 String key = (String)pairs.getKey();
	 Map<?, ?> m = (Map<?, ?>)pairs.getValue();

	 JSONObject data = new JSONObject();
	 Iterator<?> iter2 = m.entrySet().iterator();
	 while(iter2.hasNext()) {
	 Map.Entry pairs2 = (Map.Entry)iter2.next();
	 data.put((String)pairs2.getKey(), (String)pairs2.getValue());
	 }
	 holder.put(key, data);
	 }

	 StringEntity se = new StringEntity(holder.toString());
	 httpost.setEntity(se);
	 httpost.setHeader("Accept", "application/json");
	 httpost.setHeader("Content-type", "application/json");

	 ResponseHandler responseHandler = new BasicResponseHandler();
	 HttpResponse response = httpclient.execute(httpost,responseHandler);
    // int i= response.getStatusLine().getStatusCode();
     //Log.e("ECCOLO IL NUMERO---------->", Integer.toString(i));
     }
     catch(Exception e)
     {
         Log.e("Errore:", e.toString()); 
    	 return e.toString();
    	 
     }
	 return "cioss";// response.getStatusLine().getStatusCode();
	 } 
	 
	
}