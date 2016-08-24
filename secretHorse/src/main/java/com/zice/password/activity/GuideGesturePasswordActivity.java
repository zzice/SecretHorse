package com.zice.password.activity;

import com.zice.password.R;
import com.zice.password.base.BaseActivity;
import com.zice.password.base.YApplication;
import com.zice.password.util.Constant;
import com.zice.password.util.Md5Util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class GuideGesturePasswordActivity extends BaseActivity implements OnItemSelectedListener, TextWatcher {

	private AlertDialog dialog;
	private String mQuesSp;
	private EditText quesEt;
	private boolean passQues;
	private boolean modify;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesturepassword_guide);
		translucent();
		passQues = getIntent().getBooleanExtra("passQues", false);
		modify = getIntent().getBooleanExtra("modify", false);
		if (!passQues&&!modify) {
			showDialog();
		}
		findViewById(R.id.gesturepwd_guide_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				YApplication.getInstance().getLockPatternUtils().clearLock();
				Intent intent = new Intent(GuideGesturePasswordActivity.this, CreateGesturePasswordActivity.class);
				// 打开新的Activity
				startActivity(intent);
				finish();
			}
		});
	}

	@SuppressLint({ "InflateParams", "CommitPrefEdits" })
	private void showDialog() {
		View view = LayoutInflater.from(this).inflate(R.layout.pass_dialog_confirm_set, null);
		quesEt = (EditText) view.findViewById(R.id.passQues_et);
		final Spinner quesSpin = (Spinner) view.findViewById(R.id.ques_sp);
		quesEt.addTextChangedListener(this);
		ArrayAdapter<String> quesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_layout, Constant.ques);
		quesSpin.setAdapter(quesAdapter);
		quesAdapter.setDropDownViewResource(R.layout.dropdown_view_layout);
		quesSpin.setOnItemSelectedListener(this);
		AlertDialog.Builder builder = new Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		builder.setView(view);
		builder.setCancelable(false);
		builder.setPositiveButton("确认", null);
		dialog = builder.create();
		dialog.show();

		dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String mQuesS = quesEt.getText().toString().trim();
				if (!TextUtils.isEmpty(mQuesS)) {// 密保不为空
					SharedPreferences quesSharePre = GuideGesturePasswordActivity.this.getSharedPreferences("ques",
							MODE_PRIVATE);
					Editor editor = quesSharePre.edit();
					String mMd5QuesS = Md5Util.getMD5String(mQuesS);
					editor.putString("mQuesSp", mQuesSp);
					editor.putString("mQuesS", mMd5QuesS);
					editor.commit();
					dialog.dismiss();
				} else {
					Toast.makeText(GuideGesturePasswordActivity.this, "密保为空", Toast.LENGTH_SHORT).show();
				}
			}
		});
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
		} else {
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
