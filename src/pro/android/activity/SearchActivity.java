package pro.android.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.NotificationPair;
import org.thounds.thoundsapi.NotificationsWrapper;
import org.thounds.thoundsapi.Thounds;
import org.thounds.thoundsapi.ThoundsConnectionException;
import org.thounds.thoundsapi.ThoundsNotAuthenticatedexception;
import org.thounds.thoundsapi.UserWrapper;
import org.thounds.thoundsapi.UsersCollectionWrapper;

import pro.android.utils.ImageFromUrl;

import pro.android.R;
import pro.android.utils.Player;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;



public class SearchActivity extends CommonActivity {
	
//	ListView listView;
//	RequestSearchAdapter adapter;
//
//	UsersCollectionWrapper userCollection;
//
//	boolean isPlaying = false;
//	boolean isPaused = false;
//
//	String mix_url = null;
//	Player p;
//	NotificationsWrapper nw=null;
//	JSONObject json;
//	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
//	private HashMap<String,Object> item = new HashMap<String,Object>();
//	Button confirm = null;
//	Button ignore = null;
//	int position = -1;
//	int prevPosition= -1;
//    int totalPag=0;
//    int currentPage=0;
//    String [] searchQuery=null;
//	SeekBar seekBar;
//	ImageButton btn;
//	LinearLayout retrieving_bar;
//	LinearLayout vParent;
//	ProgressBar pBar;
//	TextView no_notifications;
//	private EditText searchEditText;
//	private TextView PagOf;
	//private TextView nextPage;
//	private TextView previusPage;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
//		listView = (ListView) findViewById(R.id.);
//		adapter = new RequestSearchAdapter(
//				this,
//				R.layout.newfriendslist,
//				list
//		);
//
//		listView.setAdapter(adapter);
//		listView.setItemsCanFocus(true);
//		searchEditText = (EditText) findViewById(R.id.EditTextSearch);
//	    PagOf= (TextView) findViewById(R.id.TextViewPagdi);
        //nextPage =(TextView) findViewById(R.id.TextView01);
        //previusPage =(TextView) findViewById(R.id.TextView02);
		//showDialog(DIALOG_LOADING);
	//	Runnable run = new Runnable(){
		//	public void run() {
			//	retrievedNotification();

		//	}			
	//	};
	//	Thread thread =  new Thread(run, "retrievedNotification");
	//	thread.start();
		
	}

	/*private Runnable returnRes = new Runnable() {
		public void run() {
			
			for(int i=0; i<userWrapper.length; i++){
				item = new HashMap<String,Object>();
				try {
					if(userWrapper[i].getNotificationObject().getAvatarUrl()!=null)
						item.put("image", new ImageFromUrl(userWrapper[i].getNotificationObject().getAvatarUrl()).getDrawable());

					item.put("line1",userWrapper[i].getNotificationObject().getName());
					item.put("line2",userWrapper[i].getNotificationObject().getCity()+", "+userWrapper[i].getNotificationObject().getCountry());
					list.add( item );


					adapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			dismissDialog(DIALOG_LOADING);
		}
	};
	

	private void retrievedNotification() {
		try {
			nw = RequestWrapper.loadNotifications();
			userWrapper= nw.getBandRequestList();
		} catch (ThoundsConnectionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalThoundsObjectException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		runOnUiThread(returnRes);

	}
*/
	/*
	public void retrievalResult()
	{
		list.clear();
		if(list.isEmpty())
		{
			Log.e("search","la lista è vuota");	
			
		}
		Log.e("search","cancello gli elementi nella lista");
		adapter.clear();
		Log.e("search","prendo i risultati");
		UserWrapper[] userWrapper;
		try {
			userWrapper = userCollection.getUsersList();
		
		for(int i=0; i<userWrapper.length; i++){
			item = new HashMap<String,Object>();
			
				if(userWrapper[i].getAvatarUrl()!=null)
					item.put("image", new ImageFromUrl(userWrapper[i].getAvatarUrl()).getDrawable());

				
				Log.e("search nome",userWrapper[i].getName().toString());
				item.put("line1",userWrapper[i].getName());
				item.put("line2",userWrapper[i].getCity()+", "+userWrapper[i].getCountry());
				list.add( item );

				Log.e("search","aggiunto 1");
			
				adapter.notifyDataSetChanged();
			}
			
		
		}
		catch (IllegalThoundsObjectException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			showDialog(DIALOG_ALERT_CONNECTION);
			e.printStackTrace();
		}
//		dismissDialog(DIALOG_LOADING);
		
	}
	
	 public void onClickSearch(View v){
		 Log.e("Search","Ok ho cliccato serach");
		
		 
	 String requestSearch = searchEditText.getText().toString();
	 if(requestSearch!="")
	 {
	 searchQuery = requestSearch.split(" ");
	
	 currentPage =1;
	 retrievalSearch( searchQuery ,currentPage);
	
	 //dismissDialog(DIALOG_LOADING);
	 try {
		Log.e("Search","I risultati trovati sono:"+ String.valueOf(userCollection.getUsersList().length));
	} catch (IllegalThoundsObjectException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
	}
	catch (Exception ex) {
		// TODO Auto-generated catch block
		ex.printStackTrace();
		showDialog(DIALOG_ALERT_CONNECTION);
	}
	 }
	
	 }
	 
	 private void 	 retrievalSearch(String [] splitSearch,int pag)
	 {
		try {
			
			userCollection = Thounds.search(splitSearch, pag, 10);
			totalPag = userCollection.getPageTotalNumber();

			if(userCollection.getusersTotalNumber()!=0)
			{
			Log.e("Search result","Bellaaaaaaaaa il numero di pagine sono:"+String.valueOf(userCollection.getUsersListLength()));
			retrievalResult();
			PagOf.setText("Page "+String.valueOf(pag)+" of "+String.valueOf(totalPag));
			}
			else
			{
				showDialog(RESULT_SEARCH_NULL);
				
			}
		} catch (ThoundsConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showDialog(DIALOG_ALERT_CONNECTION);
		} catch (IllegalThoundsObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
		} catch (ThoundsNotAuthenticatedexception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 }
	 
	public void onClickItemNotification(View v){

		int position = (Integer) v.getTag();

		nextIntent = new Intent(v.getContext(), ProfileActivity.class);
		try {
			nextIntent.putExtra("userId", userCollection.getUsers(position).getId());
		} catch (IllegalThoundsObjectException e) {
			// TODO Auto-generated catch block
			showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
			e.printStackTrace();
		}
		startActivity(nextIntent);
		
	}

	public void onClickNextPage(View v)
	{
		if(searchQuery!=null)
		{
			if(currentPage+1 <= totalPag)
			{
			currentPage ++;
			 retrievalSearch( searchQuery ,currentPage);
			}
		}
		
	}
	
	public void onClickPreviousPage(View v)
	{
		if(searchQuery!=null)
		{
			if(currentPage-1 > 0)
			{
			 currentPage --;
			 retrievalSearch( searchQuery ,currentPage);
			}
		}
		
	}
	
	
	public void onClickConfirm(View v){
		
	}

	public void onClickIgnore(View v){ //E' associato a bottone Add
		confirm = (Button)v;

		int tag = (Integer)confirm.getTag();
		int position = (Integer) v.getTag();

		Log.d("il numero cliccato �",String.valueOf(tag));
		try {

			int idUser=-1;
			try {
				
				Log.e("Search error ", String.valueOf(tag));
				idUser= userCollection.getUsers(tag).getId();
			} catch (IllegalThoundsObjectException e) {
				// TODO Auto-generated catch block
				showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
				e.printStackTrace();
			}
			//UserWrapper us= RequestWrapper.acceptFriendship(tag);
		//	int idNotif= userWrapper[tag].getNotificationId(); 
			//mettere Id che c'� nelle librerie nuove
			boolean ris = Thounds.friendshipRequest(idUser);
			if(ris)
			{
				Log.d("Evviva ha funzionato.....",String.valueOf(idUser));
				showDialog(DIALOG_ADD_USER);
				//nextIntent = new Intent(v.getContext(), HomeActivity.class);
				//startActivity(nextIntent);
			}
			else
			{
				Log.d("NOoooo non ha funzionato.....",String.valueOf(idUser));
				//nextIntent = new Intent(v.getContext(), HomeActivity.class);
				//startActivity(nextIntent);
			}

		} catch (ThoundsConnectionException e) {
			showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (ThoundsNotAuthenticatedexception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	


	private class RequestSearchAdapter extends ArrayAdapter<HashMap<String,Object>> {

		private ArrayList<HashMap<String,Object>> items;

		public RequestSearchAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String,Object>> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.newfriendslist, null);
			}
			HashMap<String,Object> item = items.get(position);
			if (item != null) {
				
				TextView tt = (TextView) v.findViewById(R.id.text1);
				TextView bt = (TextView) v.findViewById(R.id.text2);
				ImageView avatar = (ImageView) v.findViewById(R.id.ImageViewAvatar);
				Button invisible = (Button) v.findViewById(R.id.ButtonConfirmFriends);
				Button Add = (Button) v.findViewById(R.id.ButtonIgnoreFriend);
				LinearLayout ln = (LinearLayout) v.findViewById(R.id.lNewFriend);
                 UserWrapper user=null;
				try {
					user = userCollection.getUsers(position);
				} catch (IllegalThoundsObjectException e) {
					// TODO Auto-generated catch block
					showDialog(DIALOG_ILLEGAL_THOUNDS_OBJECT);
					e.printStackTrace();
				}
				//avatar.setImageDrawable(new ImageFromUrl(userWrapper[position].getNotificationObject().getAvatarUrl()).getDrawable());
                 avatar.setImageDrawable(new ImageFromUrl(user.getAvatarUrl()).getDrawable());
				if (tt != null) {
					tt.setText( (String)item.get("line1")); }
				if(bt != null){
					bt.setText((String)item.get("line2"));
				}
				if (avatar != null)
					avatar.setImageDrawable((Drawable)item.get("image"));
				
				if(invisible!= null)
				{
					//confirm.setText("Add");
					invisible.setVisibility(View.INVISIBLE);
					
				}
				if(Add !=null)
				{
					Add.setText("Add");
					//ignore.setVisibility(View.INVISIBLE);
				}
				
				ln.setTag(position);
				Add.setTag(position);
				invisible.setTag(position);

			}
			return v;
		}
		
		public  void clearItems()
		{
			items.clear();
			
		}

	}

	@Override
	public void onPause(){
		super.onPause();
	}
*/
}




