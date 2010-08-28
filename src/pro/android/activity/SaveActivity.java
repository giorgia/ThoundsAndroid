package pro.android.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import org.thounds.thoundsapi.RequestWrapper;
import org.thounds.thoundsapi.ThoundsConnectionException;

import pro.android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SaveActivity extends CommonActivity{

	static int SELECT_IMAGE = 0;
	static int TAKE_IMAGE = 1;
  
	private TextView vTitle;
	private TextView vTags;
	private RadioGroup vGroup;
	private RadioButton vPublic;
	private RadioButton vContacts;
	private RadioButton vPrivate;
	private CheckBox vLocation;
	private ImageView vCamera;
	private ImageButton vCancel;
	private ImageButton vSave;

	final CharSequence[] items = {"Choose from album", "Take a new picture"};
	private LocationManager lm;

	private String title = null;
	private String tags = null;
	private double lng;
	private double lat;
	private String privacy;
	private int delay = 0, offset =0;
	private int duration = 0;
	private String thoundPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/thounds/test.3gp";
	private String coverPath = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.save);

		vTitle = (TextView) findViewById(R.id.txt_thound_title);
		vTags = (TextView) findViewById(R.id.txt_thound_tags);
		vGroup = (RadioGroup) findViewById(R.id.RadioGroup01);
		vPublic = (RadioButton) findViewById(R.id.rdb_public);
		vContacts = (RadioButton) findViewById(R.id.rdb_contacts);
		vPrivate = (RadioButton) findViewById(R.id.rdb_private);
		vLocation = (CheckBox) findViewById(R.id.chk_location);
		vCamera = (ImageView) findViewById(R.id.btn_camera);
		vCancel = (ImageButton) findViewById(R.id.btn_cancel);
		vSave = (ImageButton) findViewById(R.id.btn_save);
		
		Bundle extras = getIntent().getExtras();
		duration = extras.getInt("duration");

		vTitle.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				if(vTitle.getText().toString().equals("Thound title")){
					vTitle.setText("");
					vTitle.setTextColor(R.color.black);
				}

			}
		});
		vTags.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				if(vTags.getText().toString().equals("Tags separated by spaces")){
					vTags.setText("");
					vTags.setTextColor(android.R.color.primary_text_dark);
				}

			}
		});
		vGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == vPublic.getId()){
					privacy = RequestWrapper.PUBLIC;
				}else if (checkedId == vContacts.getId()){
					privacy = RequestWrapper.CONTACTS;
				}else if(checkedId == vPrivate.getId()){
					privacy = RequestWrapper.PRIVATE;
				}else
					privacy = null;
			}
		});


		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a color");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if(items[item].equals("Choose from album")){
					Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
					startActivityForResult(intent , SELECT_IMAGE);
				}else if(items[item].equals("Take a new picture") ){
					Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
					startActivityForResult(intent,TAKE_IMAGE);
				}
			}
		});
		final AlertDialog alert = builder.create();
		vCamera.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				alert.show();
			}
		});

		vSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				title = vTitle.getText().toString();
				tags = vTags.getText().toString();
				if(vLocation.isChecked()){
					locating();
				}
				if(privacy == null){
					//dialog campo privacy obbligatorio
				}else if(!title.equals("")){
					try {
						Log.d("thound upload", title);
						boolean ris = RequestWrapper.createThound(title, tags, delay, offset, duration, privacy, 100, lat, lng, thoundPath, null);
						if(ris)
							Log.d("thound upload", "OK");
						else Log.d("thound upload", "BAD");
					} catch (ThoundsConnectionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
				}
			}
		});



	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
			Uri selectedImage = data.getData();
			vCamera.setScaleType(ScaleType.FIT_XY);
			vCamera.setImageURI(selectedImage);
			coverPath = selectedImage.getPath();
		} 
		if (requestCode== TAKE_IMAGE && resultCode == Activity.RESULT_OK){
			Bitmap x = (Bitmap) data.getExtras().get("data");
			BitmapDrawable d = new BitmapDrawable(x);
			vCamera.setScaleType(ScaleType.FIT_XY);
			vCamera.setImageDrawable(d);

			ContentValues values = new ContentValues();
			values.put(Images.Media.TITLE, "coverThound");
			values.put(Images.Media.BUCKET_ID, "test");
			values.put(Images.Media.DESCRIPTION, "test Image taken");
			values.put(Images.Media.MIME_TYPE, "image/jpeg");
			Uri selectedImage = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);

			OutputStream outstream;
			try {
				outstream = getContentResolver().openOutputStream(selectedImage);

				x.compress(Bitmap.CompressFormat.JPEG, 70, outstream);

				outstream.close();
			} catch (FileNotFoundException e) {
				//
			}catch (IOException e){
				//
			}
			coverPath = selectedImage.getPath();	
		}
		Log.d("PHOTO", coverPath);
	}

	private void locating(){
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location loc;
		loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(loc != null){
			Log.e("GPS", ""+loc.getLatitude()+"   "+loc.getLongitude());
		}else{
			loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			Log.e("Network", ""+loc.getLatitude()+"   "+loc.getLongitude());
		}
		lng = loc.getLongitude();
		lat = loc.getLatitude();
	}




}
