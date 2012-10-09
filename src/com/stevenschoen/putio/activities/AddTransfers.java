package com.stevenschoen.putio.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.stevenschoen.putio.PutioUtils;
import com.stevenschoen.putio.R;
import com.stevenschoen.putio.fragments.AddTransferFile;
import com.stevenschoen.putio.fragments.AddTransferUrl;

public class AddTransfers extends SherlockFragmentActivity {
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	PagerTitleStrip mPagerTitleStrip;
	
	int fragmentType;
	
	AddTransferUrl urlFragment;
	AddTransferFile fileFragment;
	
	SharedPreferences sharedPrefs;
	
	PutioUtils utils;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_addtransfer);
		setTheme(R.style.Putio_Dialog);
		
		if (getIntent().getAction() != null) {
			if (getIntent().getScheme().matches("magnet")) {
				fragmentType = 1;
			} else if (getIntent().getScheme().matches("file")) {
				fragmentType = -1;
			}
		}
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.addtransfer_pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		mPagerTitleStrip = (PagerTitleStrip) findViewById(R.id.addtransfer_pager_title_strip);
		mPagerTitleStrip.setTextColor(Color.BLACK);
		if (fragmentType != 0) {
			mPagerTitleStrip.setVisibility(View.GONE);
			findViewById(R.id.titleDivider).setVisibility(View.VISIBLE);
		}
		
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		String token = sharedPrefs.getString("token", null);
		if (token == null || token.isEmpty()) {
			Intent putioActivity = new Intent(this, Putio.class);
			startActivity(putioActivity);
			finish();
		}
		utils = new PutioUtils(token, sharedPrefs);
		
		TextView textTitle = (TextView) findViewById(R.id.dialog_title);
		Typeface robotoLight = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
		textTitle.setTypeface(robotoLight);
		textTitle.setText(getString(R.string.addtransferstitle));
		
		Button addButton = (Button) findViewById(R.id.button_addtransfer_add);
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				switch (mViewPager.getCurrentItem()) {
					case 0:
						if (!urlFragment.getEnteredUrls().isEmpty()) {
							utils.addTransfers(urlFragment.getEnteredUrls());
							Toast.makeText(AddTransfers.this, getString(R.string.torrentadded), Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(AddTransfers.this, getString(R.string.nothingenteredtofetch), Toast.LENGTH_LONG).show();
						} break;
					case 1: break;
				}
				finish();
			}
		});
		
		Button cancelButton = (Button) findViewById(R.id.button_addtransfer_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (fragmentType == 1) {
					urlFragment = AddTransferUrl.newInstance();
					Bundle bundle = new Bundle();
					bundle.putString("url", getIntent().getDataString());
					urlFragment.setArguments(bundle);
					return urlFragment;
				} else if (fragmentType == -1) {
					fileFragment = AddTransferFile.newInstance();
					return fileFragment;
				} else {
					urlFragment = AddTransferUrl.newInstance();
					return urlFragment;
				}
			case 1:
				fileFragment = AddTransferFile.newInstance();
				return fileFragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			if (fragmentType != 0) {
				return 1;
			}
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "URLs";
			case 1:
				return "Torrent file";
			}
			return null;
		}
	}
}