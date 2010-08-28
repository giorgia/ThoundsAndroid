package pro.android.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ImageFromUrl {
	private Drawable drawable = null;

	public Drawable getDrawable() {
		return drawable;
	}
	
	public ImageFromUrl(String url) {
		Log.d("URL",""+url);
		HttpGet httpRequest = null;
        httpRequest = new HttpGet(url);
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = null;
        
		try {
			response = (HttpResponse) httpclient.execute(httpRequest);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
        HttpEntity entity = response.getEntity();
        BufferedHttpEntity bufHttpEntity;
        InputStream instream = null;
        
		try {
			bufHttpEntity = new BufferedHttpEntity(entity);
			instream = bufHttpEntity.getContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        drawable = new BitmapDrawable(BitmapFactory.decodeStream(instream));
	}
	
	/*
	 * NOTE: this method doesn't work sometimes
	 */
//	public ImageFromUrl(String url) {
//		try {
//			InputStream is = (InputStream) this.fetch(url);
//			drawable = Drawable.createFromStream(is, "src");
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
}
