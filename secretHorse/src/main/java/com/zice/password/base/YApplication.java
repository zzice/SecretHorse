package com.zice.password.base;


import com.zice.password.util.LockPatternUtils;

import android.app.Application;

public class YApplication extends Application {
	private static YApplication mInstance;
	private LockPatternUtils mLockPatternUtils;

	public static YApplication getInstance() {
		return mInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		mLockPatternUtils = new LockPatternUtils(this);
		// 初始化 Bmob SDK
//		Bmob.initialize(this, "9f69ac8021ff6f21ad149219423e27fa");
	}

	public LockPatternUtils getLockPatternUtils() {
		return mLockPatternUtils;
	}
}
