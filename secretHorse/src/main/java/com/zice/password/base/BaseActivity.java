package com.zice.password.base;

import com.zice.password.util.LogUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class BaseActivity extends Activity{
	private String msg="Activity Life";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.i(msg, this.getClass().getSimpleName()+"----onCreate");
	}
	
	public void translucent(){
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		LogUtil.i(msg, this.getClass().getSimpleName()+"----onStart");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(msg, this.getClass().getSimpleName()+"----onResume");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		LogUtil.i(msg, this.getClass().getSimpleName()+"----onStop");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		LogUtil.i(msg, this.getClass().getSimpleName()+"----onRestart");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(msg, this.getClass().getSimpleName()+"----onDestroy");
	}
	
	public void openAndCloseActivity(Class<?> cls){
		Intent intent=new Intent(this,cls);
		startActivity(intent);
		finish();
	}
	public void openActivity(Class<?> cls){
		Intent intent=new Intent(this,cls);
		startActivity(intent);
	}
}
