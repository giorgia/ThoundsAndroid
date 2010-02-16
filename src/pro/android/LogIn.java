package pro.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class LogIn extends Activity {

	@Override
	public void onCreate(Bundle cicle) {
		super.onCreate(cicle);
		setContentView(R.layout.log_in);

		final EditText usernameEditText = (EditText) findViewById(R.id.txt_username);
		final EditText passwordEditText = (EditText) findViewById(R.id.txt_password);
		
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
				//Get the text from the editexts
				String sUserName = usernameEditText.getText().toString();
				String sPassword = passwordEditText.getText().toString();

				
				if (usernameEditText == null || passwordEditText == null) {
					
					Log.d("result", "log in fail");
				} else {
					Log.d("result", "log in ok  " + sUserName + "  "
							+ sPassword);

				}

			}
		}

		);
	}
	 
}