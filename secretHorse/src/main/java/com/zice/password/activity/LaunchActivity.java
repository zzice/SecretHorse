package com.zice.password.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zice.password.R;
import com.zice.password.base.BaseActivity;
import com.zice.password.util.Constant;

public class LaunchActivity extends BaseActivity {
    @ViewInject(R.id.launch_skip_btn)
    Button SkipBtn;
    @ViewInject(R.id.launch_img)
    ImageView launchImg;
    @ViewInject(R.id.version_name_tv)
    TextView versionNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ViewUtils.inject(this);//注入View和事件
        versionNameTv.setText(getVersion());
        startAnim();
    }


    private void startAnim() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.4f, 1);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                CustomTimer timer = new CustomTimer(Constant.LaunchDelayMillis, 1000);
                timer.start();

            }
        });
        launchImg.setAnimation(alphaAnimation);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return  "v"+version;
        } catch (Exception e) {
            e.printStackTrace();
            return "v1.0";
        }
    }

    /*
     * 继承 CountDownTimer
     *
     * 重写 父类的方法 onTick() 、 onFinish()
     *
     */
    class CustomTimer extends CountDownTimer {
        /**
         * @param millisInFuture    表示以毫秒为单位 倒计时的总数
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick 方法
         */
        public CustomTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            openAndCloseActivity(UnlockGesturePasswordActivity.class);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            SkipBtn.setVisibility(View.VISIBLE);
            SkipBtn.setText("倒计时：" + millisUntilFinished / 1000);
        }

    }

}
