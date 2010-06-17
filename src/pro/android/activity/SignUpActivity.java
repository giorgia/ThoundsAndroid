package pro.android.activity;

import org.json.JSONStringer;

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

	private static final int DIALOG_SIGN_UP = 0;
	private static final int DIALOG_RESULT_SIGNUP = 0;
	private String s="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("OKOKOKOKKO","ALLORA VAI");
		setContentView(R.layout.sign_up);
		Button signUp = (Button) findViewById(R.id.ButtonSignUp);
		final EditText userEdit =(EditText) findViewById(R.id.EditTextUser);



		userEdit.setOnClickListener(new OnClickListener() {
			public void onClick(View viewParam) {
				userEdit.setText(" ");
			}


		});

		//Click on SignUp Button
		signUp.setOnClickListener(new OnClickListener() {
			public void onClick(View viewParam) {
				try
				{
					EditText userEditText = (EditText) findViewById(R.id.EditTextUser);
					String user = userEditText.getText().toString();

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
					.value(user)
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

					//OLD::s= comm.signUp(myString);
					//RequestWrapper.registrateUserTest(user, email, country, city, tags);
					showDialog(DIALOG_RESULT_SIGNUP);
				}
				catch(Exception e)
				{

					Log.d("ATTENZIONE",e.toString());
				}
			}


		});
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {


		case DIALOG_RESULT_SIGNUP: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Alert").setIcon(
					android.R.drawable.ic_dialog_alert).setMessage(
							"ok vecchio!"+ s)
							.setCancelable(false).setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});

			return builder.create();
		}

		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}