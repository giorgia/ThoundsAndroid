package pro.android.activity;

import org.thounds.thoundsapi.UserWrapper;

import pro.android.R;
import pro.android.utils.ImageFromUrl;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends CommonActivity {
	
	
	private UserWrapper user;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		ImageView imageProfile = (ImageView)findViewById(R.id.ImageViewPhoto);
		imageProfile.setImageDrawable(new ImageFromUrl(this,user.getAvatarUrl(), "").getDrawable());
		TextView TextName = (TextView) findViewById(R.id.TextViewNome);
		TextName.setText(user.getName());
		TextName.postInvalidate();

	}

}
