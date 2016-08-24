package com.zice.password.activity;

import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
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
import com.zice.password.base.BaseActivity;
import com.zice.password.entity.Secret;
import com.zice.password.util.Constant;
import com.zice.password.util.Md5Util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity
		implements android.view.View.OnClickListener, DeleteInterFace, OnItemSelectedListener, TextWatcher {
	@ViewInject(R.id.add)
	private ImageView add_btn;
	@ViewInject(R.id.add_data)
	RelativeLayout addLayout;
	@ViewInject(R.id.slidingmenu_btn)
	ImageView slidingMenuBtn;
	@ViewInject(R.id.list)
	ListView listView;
	@ViewInject(R.id.search_btn)
	ImageView search_btn;
	@ViewInject(R.id.list_empty_tv)
	TextView list_empty_tv;
	LayoutInflater inflater;
	private long exitTime = 0;
	private SlidingMenu menu;
	private DbUtils dbUtils;
	private TAdapter tAdapter;
	private List<Secret> secrets;
	private String mQuesSp;
	private AlertDialog dialog;
	private EditText quesEt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);
		add_btn.setClickable(false);
		setMenu();
		// 初始化数据库
		DaoConfig config = new DaoConfig(this);
		config.setDbName(Constant.DbName);
		config.setDbVersion(1);
		dbUtils = DbUtils.create(config);
		try {
			dbUtils.createTableIfNotExist(Secret.class);
			secrets = dbUtils.findAll(Selector.from(Secret.class).orderBy("id", true));
			tAdapter = new TAdapter(secrets, this, this);
			listView.setAdapter(tAdapter);
			listView.setEmptyView(list_empty_tv);
		} catch (DbException e) {
			e.printStackTrace();
		}
		initMenuViews();
	}

	private void initMenuViews() {
		TextView exit = (TextView) findViewById(R.id.exit);
		TextView about_us_tv = (TextView) findViewById(R.id.about_us_tv);
		TextView modify_lock = (TextView) findViewById(R.id.modify_lock);
		about_us_tv.setOnClickListener(this);
		exit.setOnClickListener(this);
		modify_lock.setOnClickListener(this);
	}

	private void setMenu() {
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);// 设置左滑菜单
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置滑动的屏幕范围，该设置为全屏区域都可以滑动
		menu.setShadowWidthRes(R.dimen.shadow_width);// 设置阴影图片的宽度
		menu.setShadowDrawable(R.drawable.shadow);// 设置阴影图片
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// SlidingMenu划出时主页面显示的剩余宽度
		menu.setFadeDegree(0.35f);// SlidingMenu滑动时的渐变程度
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);// 使SlidingMenu附加在Activity上
		menu.setMenu(R.layout.menu);
	}

	@OnClick({ R.id.slidingmenu_btn, R.id.add_data, R.id.search_btn })
	public void clickImg(View v) {
		switch (v.getId()) {
		case R.id.slidingmenu_btn:
			menu.showMenu();
			break;
		case R.id.add_data:
			openActivity(AddDataActivity.class);
			break;
		case R.id.search_btn:
			openActivity(SearchActivity.class);
			break;
		}
	}

	/**
	 * 返回键退出程序
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressLint("InflateParams")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.exit:
			AlertDialog.Builder mExitBuilder = new Builder(this, AlertDialog.THEME_HOLO_LIGHT);
			mExitBuilder.setTitle("是否确认退出");
			mExitBuilder.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					// 关闭滑动菜单
					// if(menu.isMenuShowing()){
					// menu.showContent();
					// }
					finish();
					System.exit(0);
				}
			});
			mExitBuilder.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			mExitBuilder.create().show();
			
			break;
		case R.id.about_us_tv:
			AlertDialog.Builder builder = new Builder(this, AlertDialog.THEME_HOLO_LIGHT);
			builder.setTitle("QQ");
			builder.setMessage("\n工程狮：505298672\n\n设计狮：946296845\n");
			builder.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					// 关闭滑动菜单
					// if(menu.isMenuShowing()){
					// menu.showContent();
					// }
				}
			});
			builder.create().show();
			break;
		case R.id.modify_lock:
			View view = LayoutInflater.from(this).inflate(R.layout.pass_dialog_confirm, null);
			quesEt = (EditText) view.findViewById(R.id.passQues_et);
			final Spinner quesSpin = (Spinner) view.findViewById(R.id.ques_sp);
			quesEt.addTextChangedListener(this);
			ArrayAdapter<String> quesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_layout,
					Constant.ques);
			quesSpin.setAdapter(quesAdapter);
			quesAdapter.setDropDownViewResource(R.layout.dropdown_view_layout);
			quesSpin.setOnItemSelectedListener(this);
			AlertDialog.Builder mPbuilder = new Builder(this, AlertDialog.THEME_HOLO_LIGHT);
			mPbuilder.setView(view);
			mPbuilder.setPositiveButton("确认", null);
			dialog = mPbuilder.create();
			dialog.show();
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String mQuesS = quesEt.getText().toString().trim();
					if (!TextUtils.isEmpty(mQuesS)) {// 密保不为空
						SharedPreferences quesSharePre = MainActivity.this.getSharedPreferences("ques", MODE_PRIVATE);
						String mQuesSharePSp = quesSharePre.getString("mQuesSp", "");
						String mQuesShareP = quesSharePre.getString("mQuesS", "");
						if (mQuesSp.equals(mQuesSharePSp) && Md5Util.getMD5String(mQuesS).equals(mQuesShareP)) {
							Intent mIntent = new Intent();
							mIntent.setClass(MainActivity.this, UnlockGesturePasswordActivity.class);
							mIntent.putExtra("modify", true);
							startActivity(mIntent);
							dialog.dismiss();
						} else {
							Toast.makeText(MainActivity.this, "密保答案错误，请重试", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(MainActivity.this, "密保答案为空", Toast.LENGTH_SHORT).show();
					}
				}
			});
			break;
		}
	}

	/**
	 * 更新Adapter
	 */
	@Override
	protected void onResume() {
		super.onResume();
		try {
			secrets.clear();
			secrets = dbUtils.findAll(Selector.from(Secret.class).orderBy("id", true));
		} catch (DbException e) {
			e.printStackTrace();
		}
		tAdapter.list = secrets;
		tAdapter.notifyDataSetChanged();
//		if (tAdapter.getCount()==0) {
//			list_empty_tv.setVisibility(View.VISIBLE);
//		}else {
//			list_empty_tv.setVisibility(View.GONE);
//		}
	}

	@Override
	public void deleteOperation() {
		tAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		mQuesSp = Constant.ques[position];
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (TextUtils.isEmpty(quesEt.getText().toString())) {
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
		}else {
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}

	// menu.setMode(SlidingMenu.LEFT);//设置左滑菜单
	// menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//设置滑动的屏幕范围，该设置为全屏区域都可以滑动
	// menu.setShadowDrawable(R.drawable.shadow);//设置阴影图片
	// menu.setShadowWidthRes(R.dimen.shadow_width);//设置阴影图片的宽度
	// menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);//SlidingMenu划出时主页面显示的剩余宽度
	// menu.setBehindWidth(400);//设置SlidingMenu菜单的宽度
	// menu.setFadeDegree(0.35f);//SlidingMenu滑动时的渐变程度
	// menu.attachToActivity(this,
	// SlidingMenu.SLIDING_CONTENT);//使SlidingMenu附加在Activity上
	// menu.setMenu(R.layout.menu_layout);//设置menu的布局文件
	// menu.toggle();//动态判断自动关闭或开启SlidingMenu
	// menu.showMenu();//显示SlidingMenu
	// menu.showContent();//显示内容
	// menu.setOnOpenListener(onOpenListener);//监听slidingmenu打开
	//
	// menu.setOnOpenedListener(onOpenedlistener);监听slidingmenu打开后
	//
	// menu.OnCloseListener(OnClosedListener);//监听slidingmenu关闭时事件
	//
	// menu.OnClosedListener(OnClosedListener);//监听slidingmenu关闭后事件
	//
	// 左右都可以划出SlidingMenu菜单只需要设置
	// menu.setMode(SlidingMenu.LEFT_RIGHT);属性，然后设置右侧菜单的布局文件
	// menu.setSecondaryMenu(R.layout.menu_fram2);//设置右侧菜单
	//
	// menu.setSecondaryShadowDrawable(R.drawable.shadowright);//右侧菜单的阴影图片
}
