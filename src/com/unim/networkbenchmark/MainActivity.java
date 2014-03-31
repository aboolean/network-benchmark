package com.unim.networkbenchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

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
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MainActivity extends ActionBarActivity {
	private Button button;
	private TextView results, status, ratio, conn, latency, speed, duration;
	private ProgressBar progress;
	private Handler handler;
	private Thread currentThread;
	private boolean isDownloading;
		
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
		
		isDownloading = false;
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
		if (!isDownloading){
			ratio.setText("");
			latency.setText("");
			conn.setText("");
			speed.setText("");
			duration.setText("");
			results.setText("");
			progress.setProgress(0);
			
			button.setText("Cancel");
			isDownloading = true;
			status.setText("Busy");
			currentThread = new Thread(){
				public void run(){
					downloadFile();
				}
			};
			currentThread.start();
		} else{
			button.setText("Begin Test");
			isDownloading = false;
			if (currentThread != null){
				currentThread.interrupt();
			}
			ratio.setText("");
			latency.setText("");
			conn.setText("");
			speed.setText("");
			duration.setText("");
			results.setText("");
			status.setText("Ready");
			progress.setProgress(0);
		}
	}
	
	public void downloadFile() {
		// connection type
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		
		String ptype = "None";
		if (ni == null || !ni.isAvailable() || !ni.isConnected()){
			button.setEnabled(true);
			currentThread.interrupt();
		} else {
			ptype = ni.getTypeName();
			String subtype = ni.getSubtypeName();
			if (subtype != null && !subtype.equals(""))
				ptype = ptype + "/" + subtype;
		}
		final String type = String.valueOf(ptype);

		handler.post(new Runnable() {
			public void run() {
				conn.setText(type);
			}
		});
		
		if (currentThread.isInterrupted()) // not connected
			return;
		
		// download
		long time = System.currentTimeMillis();
		try {
			URL website = new URL("http://web.mit.edu/21w.789/www/papers/griswold2004.pdf");
			
			// latency
			time = System.currentTimeMillis();
			final InputStream stream = website.openStream();
			final int lag = (int) (System.currentTimeMillis() - time);
			handler.post(new Runnable() {
				public void run() {;
					latency.setText(String.valueOf(lag) + " ms");
				}
			});
			
			// throughput
			final ReadableByteChannel rbc = Channels.newChannel(stream);
			final FileOutputStream fos = new FileOutputStream(new File(getFilesDir()+"a.pdf"));
			time = System.currentTimeMillis();
			int bits = 0;
			try {
				byte[] buffer = new byte[1024];
				int len = stream.read(buffer);
				Log.d("len", String.valueOf(len));
				while (len != -1) {
				    fos.write(buffer, 0, len);
				    bits += len;
				    if (bits % 20480 <= 1024 && !currentThread.isInterrupted()){
					    // update every 20KB
					    final int finBits = bits;
					    final int finTime = (int) (System.currentTimeMillis() - time);
					    handler.post(new Runnable() {
							public void run() {
								int percent = (int) (finBits/650924.0*100.0);
								if(percent > 100)
									percent = 100;
								ratio.setText(String.valueOf(percent) + "%");
								Log.d("UPDATE",String.valueOf(percent));
								progress.setProgress(percent);
								double avgSpeed = 0;
								if (finTime != 0)
									avgSpeed = finBits/finTime;
								speed.setText(String.format("%.2f",avgSpeed) + " KB/s");
							}
						});
				    }
				    len = stream.read(buffer);
				    if (currentThread.isInterrupted()) {
				        break;
				    }
				}
				
				if (!currentThread.isInterrupted()){
					final int finTime = (int) (System.currentTimeMillis() - time); // end time
					handler.post(new Runnable() {
						public void run() {;
							duration.setText(String.valueOf(finTime/1000.0) + " s");
							double avgSpeed = 650924.0/finTime;
							speed.setText(String.format("%.2f",avgSpeed) + " KB/s");
							ratio.setText("100%");
							progress.setProgress(100);
						}
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				try {
					stream.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// resolve fields
		if (!currentThread.isInterrupted()) {
			handler.post(new Runnable() {
				public void run() {;
					status.setText("Complete");
					button.setText("Begin Test");
				}
			});
		}
		isDownloading = false;
	}

}
