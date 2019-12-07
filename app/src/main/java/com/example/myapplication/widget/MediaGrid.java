package com.example.myapplication.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.engine.ImageEngine;
import com.example.myapplication.engine.impl.Glide4Engine;
import com.example.myapplication.entity.MultiMedia;

public class MediaGrid extends SquareFrameLayout implements View.OnClickListener {

    private ImageView mThumbnail;
    private TextView mVideoDuration; // 文本的时长（类似指视频的时长）

    private MultiMedia mMedia;// 值
    private PreBindInfo mPreBindInfo; // 控件和一些别的变量
    private OnMediaGridClickListener mListener; // 事件
    ImageEngine imageEngine = new Glide4Engine();
    private boolean checked;

    public MediaGrid(Context context) {
        super(context);
        init(context);
    }

    public MediaGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.media_grid_content, this, true);

        mThumbnail = findViewById(R.id.media_thumbnail);
        mVideoDuration = findViewById(R.id.video_duration);

        mThumbnail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            if (view == mThumbnail) {
                // 图片的点击事件
                mListener.onThumbnailClicked(mThumbnail, mMedia, mPreBindInfo.mViewHolder);
            }
        }
    }

    public void preBindMedia(PreBindInfo info) {
        mPreBindInfo = info;
    }

    /**
     * 绑定值
     * @param item 值
     */
    public void bindMedia(MultiMedia item) {
        mMedia = item;
        setImage();
        setVideoDuration();
    }


    /**
     * 设置图片或者gif图片
     */
    private void setImage() {
        imageEngine.loadThumbnail(getContext(), mPreBindInfo.mResize,
                    mPreBindInfo.mPlaceholder, mThumbnail, mMedia.getMediaUri());
    }

    /**
     * 设置文本的时长（类似指视频的时长）
     */
    private void setVideoDuration() {
        if (mMedia.isVideo()) {
            mVideoDuration.setVisibility(VISIBLE);
            mVideoDuration.setText(DateUtils.formatElapsedTime(mMedia.duration / 1000));
        } else {
            mVideoDuration.setVisibility(GONE);
        }
    }

    public void setOnMediaGridClickListener(OnMediaGridClickListener listener) {
        mListener = listener;
    }

    public interface OnMediaGridClickListener {

        void onThumbnailClicked(ImageView thumbnail, MultiMedia item, RecyclerView.ViewHolder holder);

    }

    public static class PreBindInfo {
        int mResize;
        Drawable mPlaceholder;
        RecyclerView.ViewHolder mViewHolder;

        public PreBindInfo(int resize, Drawable placeholder,
                           RecyclerView.ViewHolder viewHolder) {
            mResize = resize;
            mPlaceholder = placeholder;
            mViewHolder = viewHolder;
        }
    }

}
