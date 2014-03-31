package com.unim.networkbenchmark;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
	private Button button;
	private TextView results, status, ratio, conn, latency, speed, duration;
	private ProgressBar progress;
	private Handler handler;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		handler = new Handler();
	}
	
	@Override
	protected void onPostCreate(Bundle b) {
		super.onPostCreate(b);
		
		button = (Button) findViewById(R.id.button1);
		results = (TextView) findViewById(R.id.out_results);
		status = (TextView) findViewById(R.id.out_status);
		ratio = (TextView) findViewById(R.id.out_progress);
		conn = (TextView) findViewById(R.id.out_conn);
		latency = (TextView) findViewById(R.id.out_latency);
		speed = (TextView) findViewById(R.id.out_speed);
		duration = (TextView) findViewById(R.id.out_time);
		progress = (ProgressBar) findViewById(R.id.progressBar1);
		
		progress.setProgress(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public void pressButton(View view) {		
		// clear/set fields
		button.setEnabled(false);
		status.setText("Busy");
		ratio.setText("");
		latency.setText("");
		conn.setText("");
		speed.setText("");
		duration.setText("");
		results.setText("");
		progress.setProgress(0);
		
		Thread dl = new Thread(){
			public void run(){
				downloadFile();
			}
		};
		dl.start();
	}
	
	public void downloadFile() {
		// connection type
		
		
		// latency
		
		
		
		
		
		// update UI
		handler.post(new Runnable() {
			public void run() {
				status.setText("Hello");
			}
		});
	}
	
	

}
