package pro.android.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.thounds.thoundsapi.IllegalThoundsObjectException;
import org.thounds.thoundsapi.NotificationPair;
import org.thounds.thoundsapi.NotificationsWrapper;
import org.thounds.thoundsapi.RequestWrapper;
import org.thounds.thoundsapi.ThoundsConnectionException;
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
	
	ListView listView;
	RequestSearchAdapter adapter;

	UsersCollectionWrapper userCollection;

	boolean isPlaying = false;
	boolean isPaused = false;

	String mix_url = null;
	Player p;
	NotificationsWrapper nw=null;
	JSONObject json;
	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	private HashMap<String,Object> item = new HashMap<String,Object>();
	Button confirm = null;
	Button ignore = null;
	int position = -1;
	int prevPosition= -1;
    int totalPag=0;
    int currentPage=0;
    String [] searchQuery=null;
	SeekBar seekBar;
	ImageButton btn;
	LinearLayout retrieving_bar;
	LinearLayout vParent;
	ProgressBar pBar;
	TextView no_notifications;
	private EditText searchEditText;
	private TextView PagOf;
	//private TextView nextPage;
//	private TextView previusPage;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		listView = (ListView) findViewById(R.id.listRequest);
		adapter = new RequestSearchAdapter(
				this,
				R.layout.newfriendslist,
				list
		);

		listView.setAdapter(adapter);
		listView.setItemsCanFocus(true);
		searchEditText = (EditText) findViewById(R.id.EditTextSearch);
	    PagOf= (TextView) findViewById(R.id.TextViewPagdi);
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
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
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
	}
	catch (Exception ex) {
		// TODO Auto-generated catch block
		ex.printStackTrace();
	}
	 }
	
	 }
	 
	 private void 	 retrievalSearch(String [] splitSearch,int pag)
	 {
		try {
			
			userCollection = RequestWrapper.search(splitSearch, pag, 10);
			totalPag = userCollection.getusersTotalNumber();
		
			Log.e("Search result","Bellaaaaaaaaa il numero di pagine sono:"+String.valueOf(userCollection.getUsersListLength()));
			retrievalResult();
			PagOf.setText("Page "+String.valueOf(pag)+" of "+String.valueOf(totalPag));
		} catch (ThoundsConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalThoundsObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 }
	 
	public void onClickItemNotification(View v){

		int position = (Integer) v.getTag();

		nextIntent = new Intent(v.getContext(), ProfileActivity.class);
		//nextIntent.putExtra("userId", userWrapper[position].getNotificationObject().getId());
		try {
			nextIntent.putExtra("userId", userCollection.getUsers(position).getId());
		} catch (IllegalThoundsObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startActivity(nextIntent);
		
	}

	public void onClickNextPage(View v)
	{
		if(searchQuery!=null)
		{
			
			currentPage ++;
			 retrievalSearch( searchQuery ,currentPage);
		}
		
	}
	
	public void onClickPreviousPage(View v)
	{
		if(searchQuery!=null)
		{
			
			 currentPage --;
			 retrievalSearch( searchQuery ,currentPage);
		}
		
	}
	
	
	public void onClickConfirm(View v){

		confirm = (Button)v;

		int tag = (Integer)confirm.getTag();

		Log.d("il numero cliccato �",String.valueOf(tag));
		try {

			int idNotif=-1;
			try {
				idNotif = userCollection.getUsers(position).getId();
			} catch (IllegalThoundsObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//UserWrapper us= RequestWrapper.acceptFriendship(tag);
		//	int idNotif= userWrapper[tag].getNotificationId(); 
			//mettere Id che c'� nelle librerie nuove
			boolean ris = RequestWrapper.friendshipRequest(idNotif);
			if(ris)
			{
				Log.d("Evviva ha funzionato.....",String.valueOf(idNotif));
				showDialog(DIALOG_ADD_USER);
				//nextIntent = new Intent(v.getContext(), HomeActivity.class);
				//startActivity(nextIntent);
			}
			else
			{
				Log.d("NOoooo non ha funzionato.....",String.valueOf(idNotif));
				//nextIntent = new Intent(v.getContext(), HomeActivity.class);
				//startActivity(nextIntent);
			}

		} catch (ThoundsConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	public void onClickIgnore(View v){
		/*ignore = (Button)v;
		//Log.e("tag",(String) confirm.getTag());
		int tag = (Integer)ignore.getTag();
		int idNotif= userWrapper[tag].getNotificationId();
		try {
			RequestWrapper.refuseFriendship(idNotif);
		} catch (ThoundsConnectionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/

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
				Button confirm = (Button) v.findViewById(R.id.ButtonConfirmFriends);
				Button ignore = (Button) v.findViewById(R.id.ButtonIgnoreFriend);
				LinearLayout ln = (LinearLayout) v.findViewById(R.id.lNewFriend);
                 UserWrapper user=null;
				try {
					user = userCollection.getUsers(position);
				} catch (IllegalThoundsObjectException e) {
					// TODO Auto-generated catch block
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
				
				if(confirm != null)
				{
					confirm.setText("Add");
					
				}
				if(ignore !=null)
				{
					
					ignore.setVisibility(0);
				}
				
				ln.setTag(position);
				confirm.setTag(position);
				ignore.setTag(position);

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

}




