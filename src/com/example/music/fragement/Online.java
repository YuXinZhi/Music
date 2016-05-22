package com.example.music.fragement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.music.MainActivity;
import com.example.music.OnlineMusicSite;
import com.example.music.R;
import com.example.music.model.Site;
import com.example.music.service.PlayService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Online extends Base {

	private ListView mListView;
	private List<Site> mSiteList;
	private MainActivity mActivity;
	private PlayService mPlayService;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mListView = (ListView) inflater.inflate(R.layout.fragment_all, null);
		initSites();
		initListView();
		return mListView;
	}

	private void initListView() {
		ArrayList<HashMap<String, Object>> siteNames = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < mSiteList.size(); i++) {
			HashMap<String, Object> siteName = new HashMap<String, Object>();
			siteName.put("SITENAME", mSiteList.get(i).getName());
			siteNames.add(siteName);
		}

		// data List<? extends Map<String, ?>>
		SimpleAdapter adapter = new SimpleAdapter(mActivity, siteNames, R.layout.layout_site_item,
				new String[] { "SITENAME" }, new int[] { R.id.site_name });
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), OnlineMusicSite.class);
				intent.putExtra("url", mSiteList.get(position).getUrl());
				startActivity(intent);
			}
		});
	}

	// "http://m.xiami.com/", "http://m.kugou.com","http://m.kuwo.cn"
	private void initSites() {
		mSiteList = new ArrayList<Site>();
		mSiteList.add(new Site("虾米音乐", "http://m.xiami.com"));
		mSiteList.add(new Site("酷狗音乐", "http://m.kugou.com"));
		mSiteList.add(new Site("酷我音乐", "http://m.kuwo.cn"));
	}

	@Override
	public void onAttach(Activity activity) {
		mActivity = (MainActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public void onPraisedPressed() {

	}

}
