package com.zice.password.adapter;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DaoConfig;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zice.password.R;
import com.zice.password.activity.AddDataActivity;
import com.zice.password.entity.Secret;
import com.zice.password.util.Constant;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TAdapter extends BaseAdapter   {
	public 	List<Secret> list = new ArrayList<Secret>();
	private LayoutInflater inflater;
	private Context context;
	private DbUtils db;
	private DeleteInterFace deleteInterFace;

	public TAdapter(List<Secret> list, Context context,DeleteInterFace deleteInterFace) {
		super();
		this.deleteInterFace=deleteInterFace;
		this.list = list;
		this.context=context;
		this.inflater=LayoutInflater.from(context);
		//初始化数据库
		DaoConfig config=new DaoConfig(context);
		config.setDbName(Constant.DbName);
		config.setDbVersion(1); 
		db=DbUtils.create(config);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView==null) {
			viewHolder=new ViewHolder();
			convertView=inflater.inflate(R.layout.data_list_item, null);
			viewHolder.item_title=(TextView) convertView.findViewById(R.id.data_item_title);
			viewHolder.account_text=(TextView) convertView.findViewById(R.id.account_text);
			viewHolder.remark_text=(TextView) convertView.findViewById(R.id.remark_text);
			viewHolder.edit_layout=(LinearLayout) convertView.findViewById(R.id.edit_layout);
			viewHolder.delete_layout=(LinearLayout) convertView.findViewById(R.id.delete_layout);
			viewHolder.data_item_layout=(LinearLayout) convertView.findViewById(R.id.data_item_layout);
			viewHolder.remark_layout=(LinearLayout)convertView.findViewById(R.id.remark_layout);
			convertView.setTag(viewHolder);
		}
		
		viewHolder=(ViewHolder) convertView.getTag();
		viewHolder.item_title.setText(list.get(position).getTitle());
		viewHolder.account_text.setText(list.get(position).getAccount());
		if (!TextUtils.isEmpty(list.get(position).getRemark())) {
			viewHolder.remark_layout.setVisibility(View.VISIBLE);
			viewHolder.remark_text.setText(list.get(position).getRemark());
		}
		//编辑按钮点击事件
		viewHolder.edit_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.putExtra("edit", true);
				intent.putExtra("title", list.get(position).getTitle());
				intent.putExtra("account", list.get(position).getAccount());
				intent.putExtra("password", list.get(position).getPassword());
				intent.putExtra("remark", list.get(position).getRemark());
				intent.putExtra("id", list.get(position).getId());
				intent.setClass(context, AddDataActivity.class);
				context.startActivity(intent);
			}
		});
		//删除按钮点击事件
		viewHolder.delete_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder=new Builder(context,AlertDialog.THEME_HOLO_LIGHT);
				builder.setTitle("删除后不可恢复，确定要删除吗？");
				builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							db.deleteById(Secret.class, list.get(position).getId());
							list.clear();
							list=db.findAll(Selector.from(Secret.class).orderBy("id", true));
							deleteInterFace.deleteOperation();
						} catch (DbException e) {
							e.printStackTrace();
						}

					}
				});
				builder.show();
			}
		});
		//显示密码
		viewHolder.data_item_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//dialog显示
				showDialog(list.get(position));
			}
		});
		return convertView;
	}
	class ViewHolder{
		private TextView item_title;//title标题
		private TextView account_text;//account账号
		private TextView remark_text;//remark备注
		private LinearLayout edit_layout;
		private LinearLayout delete_layout;
		private LinearLayout data_item_layout;
		private LinearLayout remark_layout;
	}

	private void showDialog(Secret user) {
		AlertDialog.Builder builder=new Builder(context,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(user.getTitle());
		builder.setMessage("密码："+user.getPassword());
		builder.show();
	}

	public interface DeleteInterFace{
		public void deleteOperation();//删除操作接口
	}
}
