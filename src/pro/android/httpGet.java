package pro.android;

import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.net.URI;
import org.apache.http.HttpResponse; 
import org.apache.http.client.HttpClient; 
import org.apache.http.client.methods.HttpGet; 
import org.apache.http.impl.client.DefaultHttpClient;

/*
Here�s the general pattern for using the HttpClient: 
1.	Create an HttpClient (or get an existing reference).
2.	Instantiate a new HTTP method, such as PostMethod or GetMethod. 
3.	Set HTTP parameter names/values. 
4.	Execute the HTTP call using the HttpClient. 
5.	Process the HTTP response.
*/


public class httpGet {
	public void executeHttpGet() throws Exception { 
		BufferedReader in = null; 
		try {

			HttpClient client = new DefaultHttpClient(); 
			HttpGet request = new HttpGet(); 
			request.setURI(new URI("http://code.google.com/android/")); 
			HttpResponse response = client.execute(request);
			
			in = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));

			StringBuffer sb = new StringBuffer(""); 
			String line = ""; 
			String NL = System.getProperty("line.separator"); 
			
			while ((line = in.readLine()) != null) {
				sb.append(line + NL); 
				}
				in.close();
			
			String page = sb.toString();
			System.out.println(page); 
			} finally {
				if (in != null) {
					try {
					in.close(); 
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	}
}
