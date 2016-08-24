package com.zice.password.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;


public class VerticalPagerAdapter extends PagerAdapter {

	private static final String TAG = "PagerAdapter";
	private int imgs[];
	
	public VerticalPagerAdapter(int[] imgs) {
		super();
		this.imgs = imgs;
	}

	@Override
	public int getCount() {
		return imgs.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Log.d(TAG, "instantiateItem:" + position);
		
		ImageView img=new ImageView(container.getContext());
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		img.setBackgroundResource(imgs[position]);
		
		container.addView(img);

		return img;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		Log.d(TAG, "destroyItem:" + position);
		container.removeView((View) object);
	}

}
