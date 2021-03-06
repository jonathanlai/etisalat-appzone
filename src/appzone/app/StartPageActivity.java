package appzone.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.Bundle;

import java.lang.reflect.*;
import java.util.*;

public class StartPageActivity extends Activity {
	public final int DIALOG_HELP_ID = 1;
	public final int DIALOG_EXIT_ID = 2;
	public final int DIALOG_INVALIDNUMBER_ID = 3;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.activity = this;
        
        AppStorage.initAppStorageFromXML();

        /*Check the display size and set the variable
         * Note Galaxy 10.1(xlarge): 160dpi density, 800px width, 1280px height
         * HTC Wildfire(small): 120dpi density, 240 width, 320 height*/
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Common.screen_height = dm.heightPixels;
        /*End screen-res checking section*/
        setContentView(R.layout.startpage);
        loadCategories();
        /* Check the sim's MNC to determine whether the service provider is Etisalat*/
        TelephonyManager tMgr =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tMgr.getSimOperator();
        if ( !operator.equals(AppZoneConstants.ETISALAT_MNC) ) {
        	showDialog(DIALOG_INVALIDNUMBER_ID);
        }
    }
    @Override
    public void onResume() {
    	if ( Common.currentCategory != "" && Common.currentCategory != null ) {
    		loadApps(Common.currentCategory);
    	}
    	else {
    		loadCategories();
    	}
    	super.onResume();
    }
    public void loadCategories() {
    	Spinner catSpinner = (Spinner)findViewById(R.id.categorySpinner);
    	ArrayAdapter<String> spinnerArray = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
    	spinnerArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinnerArray.add("All");
    	for ( int i=0; i<AppStorage.categories.size(); i++ ) {
    		spinnerArray.add(AppStorage.categories.get(i));
    	}
    	catSpinner.setAdapter(spinnerArray);
    	catSpinner.setOnItemSelectedListener(new catOnItemSelectedListener());
    	
    }
    /**
     * Function to load a category of apps
     * @param category The Category of apps to be loaded, if this is <strong>"all"</strong>, all the apps get loaded
     */
    public void loadApps(String category) {
    	HashMap<String, Object> thisApp = new HashMap<String, Object>();
    	TableLayout appTable = (TableLayout)findViewById(R.id.tableLayout1);
    	appTable.removeAllViews();
    	final Intent appview = new Intent(this,appzone.app.AppViewActivity.class);
    	for ( String key : AppStorage.apps.keySet() ) {
    		if ( category.equals("All") || AppStorage.apps.get(key).get("category").equals(category) 
    				|| (category.equals("Entertainment") && AppStorage.apps.get(key).get("category").equals("Fun")) ) {
	    		thisApp = AppStorage.apps.get(key);
	    		final String appid = key;
	    		TableRow approw = new TableRow(this);
	    		TextView appname = new TextView(this);
	    		TextView appcategory = new TextView(this);
	    		ImageView appimg = new ImageView(this);
	    		appimg.setAdjustViewBounds(true);
	    		
	    		appimg.setImageResource((Integer)thisApp.get("appimg"));
	    		appname.setText((String)thisApp.get("appname"));
	    		final String appcat = (String)thisApp.get("category");
	    		appcategory.setText(appcat);
	    		
	    		if ( Common.screen_height >= AppZoneConstants.XLARGE_SCREEN_HEIGHT ) {
	    			appname.setTextAppearance(this, R.style.appzone_font_large);
	    			appcategory.setTextAppearance(this, R.style.appzone_font_large);
	    			appname.setPadding(5, 5, 5, 5);
	    			appcategory.setPadding(20, 5, 5, 5);
	    			appimg.setMaxHeight(60);
	    			appimg.setMaxWidth(60);
	    			appimg.setPadding(5, 5, 5, 5);
	    		}
	    		else if ( Common.screen_height <= AppZoneConstants.SMALL_SCREEN_HEIGHT ) {
	    			appname.setTextAppearance(this, R.style.appzone_font);
	    			appcategory.setTextAppearance(this, R.style.appzone_font);
	    			if ( category.equals("All") || category.equals("Information") || category.equals("Entertainment") ) {
	    				appname.setTextAppearance(this, R.style.appzone_font_small);
	    				appcategory.setTextAppearance(this, R.style.appzone_font_small);
	    			}
	    			appname.setPadding(2, 2, 2, 2);
	    			appcategory.setPadding(2, 2, 2, 2);
	    			appimg.setMaxWidth(30);
		    		appimg.setMaxHeight(30);
		    		appimg.setPadding(2, 2, 2, 2);
	    		}
	    		else {
	    			appname.setTextAppearance(this, R.style.appzone_font);
	    			appcategory.setTextAppearance(this, R.style.appzone_font);
	    			appname.setPadding(5, 5, 5, 5);
	    			appcategory.setPadding(5, 5, 5, 5);
	    			appimg.setMaxWidth(40);
		    		appimg.setMaxHeight(40);
		    		appimg.setPadding(3, 3, 3, 3);
	    		}
	    		
	    		
	    		
	    		/*appcategory.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						loadApps(appcat);
						
					}
				});*/
	    		appname.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						//viewSingleApp(appid);
						Common.currentAppId = appid;
						startActivity(appview);
						
					}
				});
	    		appimg.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						//viewSingleApp(appid);
						Common.currentAppId = appid;
						startActivity(appview);
						
					}
				});
	    		approw.addView(appimg);
	    		approw.addView(appname);
	    		approw.addView(appcategory);
	    		appTable.addView(approw);
	    	}
    	}
    }
    
    
    public class catOnItemSelectedListener implements OnItemSelectedListener {

		
		public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
				long id) {
			String category = parent.getItemAtPosition(pos).toString();
			Common.currentCategory = category;
			loadApps(category);
			
		}

		
		public void onNothingSelected(AdapterView<?> parent) {
			loadApps("All");
			
		}
    	
    }
    /**
     * Method for creating the options menu (to show help)
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.startpage_menu, menu);
        return true;
    }
    /**
     * Method that gets invoked when an item in the options menu is selected
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.help:
            showDialog(DIALOG_HELP_ID);
            return true;
        case R.id.search:
        	onSearchRequested();
        	return true;
        case R.id.exit:
        	showDialog(DIALOG_EXIT_ID);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    protected Dialog onCreateDialog(int id, Bundle bundle) {
    	AlertDialog dialog = null;
    	String helpMessage = "Choose the category of the app you want to view. " +
    			"Once you enter the app's page, you can send the relevant messages for " +
    			"each app.";
    	switch( id ) {
    	case DIALOG_HELP_ID:
    		AlertDialog.Builder helpDialogBuilder = new AlertDialog.Builder(this);
    		helpDialogBuilder.setMessage(helpMessage)
    		       .setCancelable(true)
    		       .setTitle("Info")
    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                dialog.cancel();
    		           }
    		       });
    		dialog = helpDialogBuilder.create();
    		return dialog;
    	case DIALOG_EXIT_ID:
    		AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(this);
    		exitDialogBuilder.setMessage("Are you sure you want to exit?")
    		       .setCancelable(false)
    		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                StartPageActivity.this.finish();
    		           }
    		       })
    		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						
					}
				});
    		dialog = exitDialogBuilder.create();
    		return dialog;
    	case DIALOG_INVALIDNUMBER_ID:
    		AlertDialog.Builder invalidNumberDialogBuilder = new AlertDialog.Builder(this);
    		invalidNumberDialogBuilder.setMessage("This app works only with an Etisalat SIM!")
    		       .setCancelable(false)
    		       .setTitle("ERROR")
    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                StartPageActivity.this.finish();
    		           }
    		       });
    		       
    		dialog = invalidNumberDialogBuilder.create();
    		return dialog;
    	default:
    		return null;
    	}
    }
    @Override
    /**
     * Overriding the back button function
     */
    public void onBackPressed() {
    	showDialog(DIALOG_EXIT_ID);
    }
}
