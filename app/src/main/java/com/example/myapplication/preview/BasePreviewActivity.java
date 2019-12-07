package com.example.myapplication.preview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.example.myapplication.entity.IncapableCause;
import com.example.myapplication.entity.MultiMedia;
import com.example.myapplication.preview.adapter.PreviewPagerAdapter;
import com.example.myapplication.preview.previewitem.PreviewItemFragment;
import com.example.myapplication.utils.PhotoMetadataUtils;
import com.example.myapplication.utils.VersionUtils;
import com.example.myapplication.widget.PreviewViewPager;


/**
 * 预览的基类
 */
public class BasePreviewActivity extends AppCompatActivity implements
        ViewPager.OnPageChangeListener, View.OnClickListener {

    public static final String EXTRA_IS_ALLOW_REPEAT = "extra_is_allow_repeat";
    public static final String EXTRA_DEFAULT_BUNDLE = "extra_default_bundle";
    public static final String EXTRA_RESULT_BUNDLE = "extra_result_bundle";
    public static final String EXTRA_RESULT_APPLY = "extra_result_apply";
    public static final String EXTRA_RESULT_ORIGINAL_ENABLE = "extra_result_original_enable";
    public static final String CHECK_STATE = "checkState";
    public static final String ENABLE_OPERATION = "enable_operation";
    public static final String IS_SELECTED_LISTENER = "is_selected_listener";
    public static final String IS_SELECTED_CHECK = "is_selected_check";

    protected PreviewPagerAdapter mAdapter;

    protected boolean mOriginalEnable;      // 是否原图

    protected int mPreviousPos = -1;    // 当前预览的图片的索引

    protected boolean mEnableOperation = true; // 启用操作，默认true,也不启动右上角的选择框自定义触发事件
    protected boolean mIsSelectedListener = true; // 是否触发选择事件，目前除了相册功能没问题之外，别的触发都会闪退，原因是uri不是通过数据库而获得的
    protected boolean mIsSelectedCheck = true;  // 设置右上角是否检测类型

    protected ViewHolder mViewHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);  // 获取样式
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_preview_zjh);
        if (VersionUtils.hasKitKat()) {
            // 使用沉倾状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        boolean isAllowRepeat = getIntent().getBooleanExtra(EXTRA_IS_ALLOW_REPEAT, false);
        mEnableOperation = getIntent().getBooleanExtra(ENABLE_OPERATION, true);
        mIsSelectedListener = getIntent().getBooleanExtra(IS_SELECTED_LISTENER,true);
        mIsSelectedCheck = getIntent().getBooleanExtra(IS_SELECTED_CHECK,true);
        if (savedInstanceState == null) {
            // 初始化别的界面传递过来的数据
             mOriginalEnable = getIntent().getBooleanExtra(EXTRA_RESULT_ORIGINAL_ENABLE, false);
        } else {
            // 初始化缓存的数据
            mOriginalEnable = savedInstanceState.getBoolean(CHECK_STATE);
        }

        mViewHolder = new ViewHolder(this);

        mAdapter = new PreviewPagerAdapter(getSupportFragmentManager(), null);
        mViewHolder.pager.setAdapter(mAdapter);
        initListener();
    }

    /**
     * 所有事件
     */
    private void initListener() {
        // 返回
        mViewHolder.button_back.setOnClickListener(this);
        // 多图时滑动事件
        mViewHolder.pager.addOnPageChangeListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("checkState", mOriginalEnable);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        sendBackResult(false);
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
    }

//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.button_back) {
//            onBackPressed();
//        }
//    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 滑动事件
     *
     * @param position 索引
     */
    @Override
    public void onPageSelected(int position) {
        PreviewPagerAdapter adapter = (PreviewPagerAdapter) mViewHolder.pager.getAdapter();
        if (mPreviousPos != -1 && mPreviousPos != position) {
            ((PreviewItemFragment) adapter.instantiateItem(mViewHolder.pager, mPreviousPos)).resetView();

            MultiMedia item = adapter.getMediaItem(position);
            updateSize(item);
        }
        mPreviousPos = position;
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


    /**
     * 如果当前item是gif就显示多少M的文本
     * 如果当前item是video就显示播放按钮
     *
     * @param item 当前图片
     */
    @SuppressLint("SetTextI18n")
    protected void updateSize(MultiMedia item) {
    }

    /**
     * 设置返回值
     *
     * @param apply 是否同意
     */
    protected void sendBackResult(boolean apply) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_APPLY, apply);
        intent.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
        setResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_back) {
            onBackPressed();
        }
    }


    public static class ViewHolder {
        public Activity activity;
        public PreviewViewPager pager;
        TextView button_back;
        public LinearLayout originalLayout;
        public FrameLayout bottom_toolbar;

        ViewHolder(Activity activity) {
            this.activity = activity;
            this.pager = activity.findViewById(R.id.pager);
            this.button_back = activity.findViewById(R.id.button_back);
            this.bottom_toolbar = activity.findViewById(R.id.bottom_toolbar);
        }

    }
}
