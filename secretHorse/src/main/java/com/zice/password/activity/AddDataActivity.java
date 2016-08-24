package com.zice.password.activity;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DaoConfig;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zice.password.R;
import com.zice.password.base.BaseActivity;
import com.zice.password.entity.Secret;
import com.zice.password.util.Constant;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class AddDataActivity extends BaseActivity {
	@ViewInject(R.id.remark_et)
	private EditText remark_et;
	@ViewInject(R.id.account_et)
	private EditText account_et;
	@ViewInject(R.id.title_et)
	private EditText title_et;
	@ViewInject(R.id.password_et)
	private EditText password_et;
	@ViewInject(R.id.save_data)
	private RelativeLayout saveLayout;
	@ViewInject(R.id.save)
	private Button saveBtn;
	@ViewInject(R.id.back_btn)
	private ImageView backBtn;
	private DbUtils db;
	private String title;
	private String account;
	private String password;
	private String remark;
	private boolean edit;
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_data);
		ViewUtils.inject(this);
		saveBtn.setClickable(false);
		DaoConfig config=new DaoConfig(this);
		config.setDbName(Constant.DbName);
		config.setDbVersion(1); 
		db=DbUtils.create(config);
		try {
			db.createTableIfNotExist(Secret.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		intent=this.getIntent();
		edit = intent.getBooleanExtra("edit", false);
		if (edit) {
			title=intent.getStringExtra("title");
			account=intent.getStringExtra("account");
			password=intent.getStringExtra("password");
			remark=intent.getStringExtra("remark");
			title_et.setText(title);
			remark_et.setText(remark);
			password_et.setText(password);
			account_et.setText(account);
		}
	}
	
	@OnClick(R.id.save_data)
	private void saveData(View v){
		String title=title_et.getText().toString();
		if (!TextUtils.isEmpty(title)) {//判断title
			if (!edit) {
				Secret user=new Secret();
				user.setTitle(title_et.getText().toString());
				user.setPassword(password_et.getText().toString());
				user.setAccount(account_et.getText().toString());
				user.setRemark(remark_et.getText().toString());
				try {
					db.save(user);
				} catch (DbException e) {
					e.printStackTrace();
				}
			}else{
				int id = intent.getIntExtra("id", 0);
				try {
					Secret u = db.findById(Secret.class, id);
					u.setTitle(title_et.getText().toString());
					u.setPassword(password_et.getText().toString());
					u.setAccount(account_et.getText().toString());
					u.setRemark(remark_et.getText().toString());
					db.saveOrUpdate(u);
				} catch (DbException e) {
					e.printStackTrace();
				}
			}
			this.finish();
		}else {
			Toast.makeText(this, "标题不能为空", Toast.LENGTH_LONG).show();
		}
	}
	@OnClick(R.id.back_btn)
	private void backTo(View view){
		this.finish();
	}

}
