package com.zice.password.activity;

import java.util.List;

import com.zice.password.R;
import com.zice.password.base.BaseActivity;
import com.zice.password.base.YApplication;
import com.zice.password.util.Constant;
import com.zice.password.util.LockPatternUtils;
import com.zice.password.view.LockPatternView;
import com.zice.password.view.LockPatternView.Cell;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UnlockGesturePasswordActivity extends BaseActivity
		implements OnClickListener, TextWatcher, OnItemSelectedListener {
	private LockPatternView mLockPatternView;
	private int mFailedPatternAttemptsSinceLastTimeout = 0;
	private CountDownTimer mCountdownTimer = null;
	private Handler mHandler = new Handler();
	private TextView mHeadTextView;
	private Animation mShakeAnim;

	private Toast mToast;
	private boolean modify;
	private TextView mGesturepwdUnlockForget;

	/*
	 * Toast封装
	 */
	private void showToast(CharSequence message) {
		if (null == mToast) {
			mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			mToast.setText(message);
		}

		mToast.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesturepassword_unlock);
		translucent();
		mLockPatternView = (LockPatternView) this.findViewById(R.id.gesturepwd_unlock_lockview);
		mGesturepwdUnlockForget = (TextView) findViewById(R.id.gesturepwd_unlock_forget);
		mGesturepwdUnlockForget.setOnClickListener(this);
		modify = this.getIntent().getBooleanExtra("modify", false);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(true);// 设置视图是否使用触觉反馈
		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!YApplication.getInstance().getLockPatternUtils().savedPatternExists()) {
			startActivity(new Intent(this, GuideGesturePasswordActivity.class));
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCountdownTimer != null)
			mCountdownTimer.cancel();
	}

	private Runnable mClearPatternRunnable = new Runnable() {
		@Override
		public void run() {
			mLockPatternView.clearPattern();
		}
	};

	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

		@Override
		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		@Override
		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

		@Override
		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			if (pattern == null)
				return;
			if (YApplication.getInstance().getLockPatternUtils().checkPattern(pattern)) {
				mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
				if (modify) {
					YApplication.getInstance().getLockPatternUtils().clearLock();
					Intent mIntent = new Intent();
					mIntent.setClass(UnlockGesturePasswordActivity.this, GuideGesturePasswordActivity.class);
					mIntent.putExtra("modify", modify);
					startActivity(mIntent);
					showToast("解锁成功");
					finish();
				} else {
					Intent intent = new Intent(UnlockGesturePasswordActivity.this, MainActivity.class);
					// 打开新的Activity
					startActivity(intent);
					showToast("解锁成功");
					finish();
				}
			} else {
				mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
				if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
					mFailedPatternAttemptsSinceLastTimeout++;
					int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
							- mFailedPatternAttemptsSinceLastTimeout;
					if (retry >= 0) {
						if (retry == 0)
							showToast("您已5次输错密码，请30秒后再试");
						mHeadTextView.setText("密码错误，还可以再输入" + retry + "次");
						mHeadTextView.setTextColor(Color.RED);
						mHeadTextView.startAnimation(mShakeAnim);
					}

				} else {
					showToast("输入长度不够，请重试");
				}

				if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
					mHandler.postDelayed(attemptLockout, 2000);
				} else {
					mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
				}
			}
		}

		@Override
		public void onPatternCellAdded(List<Cell> pattern) {

		}

		private void patternInProgress() {
		}
	};
	Runnable attemptLockout = new Runnable() {

		@Override
		public void run() {
			mLockPatternView.clearPattern();
			mLockPatternView.setEnabled(false);
			mCountdownTimer = new CountDownTimer(LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + 1, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
					if (secondsRemaining > 0) {
						mHeadTextView.setText(secondsRemaining + " 秒后重试");
					} else {
						mHeadTextView.setText("请绘制手势密码");
						mHeadTextView.setTextColor(Color.WHITE);
					}

				}

				@Override
				public void onFinish() {
					mLockPatternView.setEnabled(true);
					mFailedPatternAttemptsSinceLastTimeout = 0;
				}
			}.start();
		}
	};
	private EditText quesEt;
	private AlertDialog dialog;
	private String mQuesSp;

	@SuppressLint("InflateParams")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gesturepwd_unlock_forget:
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
			mPbuilder.setCancelable(false);
			mPbuilder.setPositiveButton("确认", null);
			dialog = mPbuilder.create();
			dialog.show();
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String mQuesS = quesEt.getText().toString().trim();
					if (!TextUtils.isEmpty(mQuesS)) {// 密保不为空
						SharedPreferences quesSharePre = UnlockGesturePasswordActivity.this.getSharedPreferences("ques",
								MODE_PRIVATE);
						String mQuesSharePSp = quesSharePre.getString("mQuesSp", "");
						String mQuesShareP = quesSharePre.getString("mQuesS", "");
						if (mQuesSp.equals(mQuesSharePSp) && mQuesS.equals(mQuesShareP)) {
							dialog.dismiss();
							YApplication.getInstance().getLockPatternUtils().clearLock();
							Intent intent = new Intent(UnlockGesturePasswordActivity.this,
									GuideGesturePasswordActivity.class);
							intent.putExtra("passQues", true);
							startActivity(intent);
							finish();

						} else {
							Toast.makeText(UnlockGesturePasswordActivity.this, "密保答案错误，请重试", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(UnlockGesturePasswordActivity.this, "密保答案为空", Toast.LENGTH_SHORT).show();
					}
				}
			});
			break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (TextUtils.isEmpty(quesEt.getText().toString())) {
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
		} else {
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		mQuesSp = Constant.ques[position];
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

}
