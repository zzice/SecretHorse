package com.zice.password.activity;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DaoConfig;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zice.password.R;
import com.zice.password.adapter.TAdapter;
import com.zice.password.adapter.TAdapter.DeleteInterFace;
import com.zice.password.entity.Secret;
import com.zice.password.util.Constant;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends Activity implements DeleteInterFace, TextWatcher {
	@ViewInject(R.id.search_tx)
	EditText search_tx;
	@ViewInject(R.id.search_list)
	ListView searchListView;
	@ViewInject(R.id.back_btn)
	private ImageView backBtn;
	@ViewInject(R.id.list_empty_tv)
	TextView list_empty_tv;
	private DbUtils dbUtils;
	private TAdapter tAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		ViewUtils.inject(this);
		searchListView.setEmptyView(list_empty_tv);
		//初始化数据库
		DaoConfig config=new DaoConfig(this);
		config.setDbName(Constant.DbName);
		config.setDbVersion(1);
		dbUtils=DbUtils.create(config);
		try {
			dbUtils.createTableIfNotExist(Secret.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
//		search_tx.setOnQueryTextListener(this);
		search_tx.addTextChangedListener(this);
	}
	@Override
	public void deleteOperation() {
		tAdapter.notifyDataSetChanged();
	}
	@OnClick(R.id.back_btn)
	private void backTo(View view){
		this.finish();
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		try {
			List<Secret> secrets=dbUtils.findAll(Selector.from(Secret.class));
			List<Secret> searchLists = new ArrayList<Secret>();
			searchLists.clear();
			for (int i = 0; i < secrets.size(); i++) {
				for (int j = 0; j < secrets.get(i).getTitle().length(); j++) {
					String ss=secrets.get(i).getTitle().substring(0, j+1);
					if (ss.equals(s.toString())||ss==s.toString()) {
						searchLists.add(secrets.get(i));
					}
				}
			}
			tAdapter=new TAdapter(searchLists, this, this);
			searchListView.setAdapter(tAdapter);
		} catch (DbException e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public void afterTextChanged(Editable s) {
		
	}

}
