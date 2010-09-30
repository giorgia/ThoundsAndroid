package pro.android.activity;

import org.json.JSONStringer;
import org.thounds.thoundsapi.Thounds;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import pro.android.R;

public class SignUpActivity extends CommonActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up);
		Button signUp = (Button) findViewById(R.id.ButtonSignUp);
		
		//Click on SignUp Button
		signUp.setOnClickListener(new OnClickListener() {
			public void onClick(View viewParam) {
				try
				{
					EditText userEditText = (EditText) findViewById(R.id.EditTextName);
					String name = userEditText.getText().toString();

					EditText emailEditText = (EditText) findViewById(R.id.EditTextEmail);
					String email = emailEditText.getText().toString();

					EditText countryEditText = (EditText) findViewById(R.id.EditTextCountry);
					String country = countryEditText.getText().toString();

					EditText cityEditText = (EditText) findViewById(R.id.EditTextCity);
					String city = cityEditText.getText().toString();

					EditText tagsEditText = (EditText) findViewById(R.id.EditTextTags);
					String tags = tagsEditText.getText().toString();

					String myString = new JSONStringer()
					.object()
					.key("user")
					.object()
					.key("name")
					.value(name)
					.key("email")
					.value(email)
					.key("country")
					.value(country)
					.key("city")
					.value(city)
					.key("tags")
					.value(tags)

					.endObject()
					.endObject()
					.toString();

					Log.e("json-------->",myString);
					Thounds.registrateUser(name, email, country, city, tags);
					showDialog(DIALOG_RESULT_SIGNUP);
				}
				catch(Exception e)
				{ 
					showDialog(DIALOG_FORMAT_JSON);
				}
			}


		});
	}

	public void onClickEditText(View v){
		((EditText)v).setText("");
	}
	
}