package com.example.qq66.fragment;

import com.example.qq66.Const;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BaseFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = initView();
		initData();
		return view;
	}

	protected View initView() {
		TextView textView = new TextView(getActivity());
		textView.setText(this.getClass().getSimpleName());
		return textView;
	}

	protected void initData() {
		
	}
	/**
	 * 从sp中获取当前用户
	 * @return
	 */
	protected String getSPData(String key){
		SharedPreferences sharedPreferences = getSP();
		return sharedPreferences.getString(Const.SP_KEY_NAME, null);
	}
	
	private SharedPreferences getSP(){
		return getActivity().getSharedPreferences(Const.SP_NAME, Context.MODE_PRIVATE);
	}
	/**
	 * 将数据保存在sp中
	 * @param key
	 * @param value
	 */
	protected void setSPData(String key,String value){
		SharedPreferences sp = getSP();
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
		
	}
}
