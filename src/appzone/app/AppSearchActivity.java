package appzone.app;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
public class AppSearchActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appsearchresults);
		
		final Intent appview = new Intent(this, appzone.app.AppViewActivity.class);
		TableLayout resultsTable = (TableLayout)findViewById(R.id.search_results_table);
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction()) ) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			LinkedList<String> results = appSearch(query);
			ListIterator<String> searchResultsIterator = results.listIterator();
			while ( searchResultsIterator.hasNext() ) {
				String currentAppId = (String)searchResultsIterator.next();
				HashMap<String,Object> currentApp = AppStorage.apps.get(currentAppId);
				final String appid = currentAppId;
	    		TableRow approw = new TableRow(this);
	    		TextView appname = new TextView(this);
	    		ImageView appimg = new ImageView(this);
	    		appimg.setAdjustViewBounds(true);
	    		appimg.setImageResource((Integer)currentApp.get("appimg"));
	    		
	    		String appdesc = (String)currentApp.get("description");
	    		appdesc = appdesc.substring(0, 20)+"...";
	    		appname.setText((String)currentApp.get("appname")+": "+appdesc);
	    		
	    		if ( Common.screen_height >= AppZoneConstants.XLARGE_SCREEN_HEIGHT ) {
	    			appname.setTextAppearance(this, R.style.appzone_font_large);
	    			appname.setPadding(5, 5, 5, 5);
	    			appimg.setMaxHeight(60);
	    			appimg.setMaxWidth(60);
	    			appimg.setPadding(5, 5, 5, 5);
	    		}
	    		else if ( Common.screen_height <= AppZoneConstants.SMALL_SCREEN_HEIGHT ) {
	    			appname.setTextAppearance(this, R.style.appzone_font);	    			
	    			appname.setPadding(2, 2, 2, 2);
	    			appimg.setMaxWidth(30);
		    		appimg.setMaxHeight(30);
		    		appimg.setPadding(2, 2, 2, 2);
	    		}
	    		else {
	    			appname.setTextAppearance(this, R.style.appzone_font);
	    			appname.setPadding(5, 5, 5, 5);
	    			appimg.setMaxWidth(40);
		    		appimg.setMaxHeight(40);
		    		appimg.setPadding(3, 3, 3, 3);
	    		}
	    		appname.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						Common.currentAppId = appid;
						startActivity(appview);
						
					}
				});
	    		appimg.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						Common.currentAppId = appid;
						startActivity(appview);
						
					}
				});
	    		approw.addView(appimg);
	    		approw.addView(appname);
	    		resultsTable.addView(approw);
			}
			
		}
	}


	public LinkedList<String> appSearch(String query) {
		LinkedList<String> searchResults = new LinkedList<String>();
		HashMap<String,Object> currentApp = new HashMap<String, Object>();
		String currentAppId;
		Pattern space = Pattern.compile("[\\s]");
		Matcher spaceMatcher = space.matcher(query);
		
		
		Pattern searchQueryPattern = Pattern.compile(query);
		
		
		for ( String key : AppStorage.apps.keySet() ) {
			currentAppId = key;
			currentApp = AppStorage.apps.get(key);
			String currentAppName = currentApp.get("appname").toString();
			Matcher searchQueryMatcher = searchQueryPattern.matcher(currentAppName);
			if ( currentAppName.equalsIgnoreCase(query) ) {
				searchResults.addFirst(currentAppId);
			}
			else if ( spaceMatcher.find() ) {
				String[] keywords = space.split(query);
				
				for ( int i=0; i<keywords.length; i++ ) {
					if ( currentAppName.toLowerCase().contains(keywords[i].toLowerCase()) ) {
						searchResults.addLast(currentAppId);
					}
					
				}
			}
			if ( currentAppName.toLowerCase().contains(query.toLowerCase()) ) {
				searchResults.addLast(currentAppId);
			}
			
			
			
		}
		return searchResults;
	}

}