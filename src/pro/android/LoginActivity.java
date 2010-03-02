package pro.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends Activity {

	@Override
	public void onCreate(Bundle cicle) {
		super.onCreate(cicle);
		setContentView(R.layout.log_in);

		final EditText usernameEditText = (EditText) findViewById(R.id.txt_username);
		final EditText passwordEditText = (EditText) findViewById(R.id.txt_password);
		final TextView nameTextView = (TextView) findViewById(R.id.TextView01);
		final TextView countryTextView = (TextView) findViewById(R.id.TextView02);
		final ImageView avatarImageView = (ImageView) findViewById(R.id.ImageView01);

		//show soft keyboard
		final InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.showSoftInput(usernameEditText, InputMethodManager.SHOW_IMPLICIT);

		//clear the editTexts on click
		usernameEditText.setOnClickListener(new OnClickListener() {
			public void onClick(View viewParam) {
				usernameEditText.setText("");
			}
		}
		);
		passwordEditText.setOnClickListener(new OnClickListener() {
			public void onClick(View viewParam) {
				passwordEditText.setText("");
			}
		}
		);

		//OnClickListener for login button
		Button launch = (Button) findViewById(R.id.login_button);
		launch.setOnClickListener(new OnClickListener() {
			public void onClick(View viewParam) {
				//hide soft keyboard
				mgr.showSoftInput(usernameEditText, InputMethodManager.HIDE_IMPLICIT_ONLY);

				//Get the text from the editexts
				String username = usernameEditText.getText().toString();
				String password = passwordEditText.getText().toString();

				if (usernameEditText == null || passwordEditText == null) {
					//to do
					Log.d("result", "log in fail");
				} else {
					BufferedReader in = null; 
					try{
						DefaultHttpClient client = new DefaultHttpClient();

						HttpProtocolParams.setUseExpectContinue(client.getParams(), false); 

						client.getCredentialsProvider().setCredentials(new AuthScope(null, 80, "thounds"),
								new UsernamePasswordCredentials(username, password)); 


						HttpGet httpget = new HttpGet();
						httpget.setURI(new URI("http://thounds.com/profile"));

						//httpget.addHeader("Accept", "text/xml");
						httpget.addHeader("Accept", "application/json");

						Log.d("LOGIN","executing request" + httpget.getRequestLine());

						HttpResponse response = client.execute(httpget);
						in = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
						
				
			//======================== DEBUG =================================================
						Log.d("LOGIN","----------------------------------------");

						Log.d("LOGIN", ""+response.getStatusLine());

						StringBuffer sb = new StringBuffer(""); 
						String line = ""; 
						String NL = System.getProperty("line.separator"); 
						while ((line = in.readLine()) != null) {
							sb.append(line + NL); 
						}
						in.close();

						String result = sb.toString();
						Log.d("LOGIN", result);
			//====================================================================================
						
						// A Simple JSONObject Creation
						JSONObject json=new JSONObject(result);

						Log.d("thound user","name:"+json.getString("name"));
						Log.d("thound user","from:"+json.getString("country"));

						nameTextView.setText(json.getString("name"));
						countryTextView.setText(json.getString("country"));

						//Classe interna che genera un Drawable da un Uri
						class ImageFromUrl {
							private Drawable drawable = null;

							public Drawable getDrawable() {
								return drawable;
							}
							public ImageFromUrl(Context ctx, String url, String saveFilename) {
								try {
									InputStream is = (InputStream) this.fetch(url);
									drawable = Drawable.createFromStream(is, "src");
								} catch (MalformedURLException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}

							public Object fetch(String address) throws MalformedURLException,IOException {
								URL url = new URL(address);
								Object content = url.getContent();
								return content;
							}
						}
						//======================================================================

						avatarImageView.setImageDrawable((new ImageFromUrl(viewParam.getContext(), json.getString("avatar"), "user_"+json.getString("id")+"_avatar")).getDrawable());


						// A Simple JSONObject Parsing
						//JSONArray nameArray=json.names();
						//JSONArray valArray=json.toJSONArray(nameArray);
						//for(int i=0;i<valArray.length();i++)
						//{
						//    Log.i("Praeda","<jsonname"+i+">\n"+nameArray.getString(i)+"\n</jsonname"+i+">\n"
						//            +"<jsonvalue"+i+">\n"+valArray.getString(i)+"\n</jsonvalue"+i+">");
						//}

						// A Simple JSONObject Value Pushing
						//json.put("sample key", "sample value");
						//Log.i("Praeda","<jsonobject>\n"+json.toString()+"\n</jsonobject>");

					} catch (Exception e) {
						e.printStackTrace();

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
		}
		);
	}
}

