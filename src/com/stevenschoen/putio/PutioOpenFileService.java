package com.stevenschoen.putio;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;

import com.stevenschoen.putio.activities.FileFinished;
import com.stevenschoen.putio.activities.Putio;

public class PutioOpenFileService extends Service {
	DownloadManager downloadManager;
	String Download_ID = "DOWNLOAD_ID";
	long downloadId;
	String filename;
	
	PutioFileUtils utils;
	
	IntentFilter intentFilter3 = new IntentFilter(Putio.CUSTOM_INTENT3);

	@Override
	public void onCreate() {
		super.onCreate();

		downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		
		utils = new PutioFileUtils(null, null);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		downloadId = intent.getExtras().getLong("downloadId");
		filename = intent.getExtras().getString("filename");
		
		IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		registerReceiver(downloadReceiver, intentFilter);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return Service.START_STICKY;
	}
	
	private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			DownloadManager.Query query = new DownloadManager.Query();
			query.setFilterById(downloadId);
			Cursor cursor = downloadManager.query(query);

			if (cursor.moveToFirst()) {
				int columnIndex = cursor
						.getColumnIndex(DownloadManager.COLUMN_STATUS);
				int status = cursor.getInt(columnIndex);
				int columnReason = cursor
						.getColumnIndex(DownloadManager.COLUMN_REASON);
				int reason = cursor.getInt(columnReason);

				if (status == DownloadManager.STATUS_SUCCESSFUL) {
					ParcelFileDescriptor file;
//					file = downloadManager.openDownloadedFile(downloadId);
					Intent finishedIntent = new Intent(PutioOpenFileService.this, FileFinished.class);
					finishedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					finishedIntent.putExtra("downloadId", downloadId);
					finishedIntent.putExtra("filename", filename);
					startActivity(finishedIntent);
				} else if (status == DownloadManager.STATUS_FAILED) {
					
				} else if (status == DownloadManager.STATUS_PAUSED) {
					
				} else if (status == DownloadManager.STATUS_PENDING) {
					
				} else if (status == DownloadManager.STATUS_RUNNING) {
					
				}
			}
		}

	};

	@Override
	public void onDestroy() {
		unregisterReceiver(downloadReceiver);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}