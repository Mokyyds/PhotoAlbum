/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.myapplication.ui.mediaselection.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.base.RecyclerViewCursorAdapter;
import com.example.myapplication.entity.Album;
import com.example.myapplication.entity.MultiMedia;
import com.example.myapplication.widget.MediaGrid;

/**
 * 相册适配器
 */
public class AlbumMediaAdapter extends
        RecyclerViewCursorAdapter<RecyclerView.ViewHolder> implements
        MediaGrid.OnMediaGridClickListener {

    private static final int VIEW_TYPE_MEDIA = 0x02;
    private final Drawable mPlaceholder;
    private CheckStateListener mCheckStateListener;
    private OnMediaClickListener mOnMediaClickListener;
    private RecyclerView mRecyclerView;
    private int mImageResize;

    public AlbumMediaAdapter(Context context, RecyclerView recyclerView) {
        super(null);

        TypedArray ta = context.getTheme().obtainStyledAttributes(new int[]{R.attr.item_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();

        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 相片的item
        return new MediaViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.media_grid_item, parent, false));
    }


    @Override
    protected void onBindViewHolder(final RecyclerView.ViewHolder holder, Cursor cursor) {
        // 相片的item
        MediaViewHolder mediaViewHolder = (MediaViewHolder) holder;

        final MultiMedia item = MultiMedia.valueOf(cursor);
        // 传递相关的值
        mediaViewHolder.mMediaGrid.preBindMedia(new MediaGrid.PreBindInfo(
                getImageResize(mediaViewHolder.mMediaGrid.getContext()),
                mPlaceholder,
                holder
        ));
        mediaViewHolder.mMediaGrid.bindMedia(item);
        mediaViewHolder.mMediaGrid.setOnMediaGridClickListener(this);
    }


    /**
     * 点击事件
     *
     * @param thumbnail 图片控件
     * @param item      数据
     * @param holder    控件
     */
    @Override
    public void onThumbnailClicked(ImageView thumbnail, MultiMedia item, RecyclerView.ViewHolder holder) {
        if (mOnMediaClickListener != null) {
            mOnMediaClickListener.onMediaClick(null, item, holder.getAdapterPosition());
        }
    }


    /**
     * 刷新数据
     */
    private void notifyCheckStateChanged() {
        notifyDataSetChanged();
        if (mCheckStateListener != null) {
            mCheckStateListener.onUpdate();
        }
    }

    /**
     * 返回类型
     *
     * @param position 索引
     * @param cursor   数据源
     */
    @Override
    public int getItemViewType(int position, Cursor cursor) {
        return VIEW_TYPE_MEDIA;
    }


    /**
     * 注册选择事件
     *
     * @param listener 事件
     */
    public void registerCheckStateListener(CheckStateListener listener) {
        mCheckStateListener = listener;
    }

    /**
     * 注销选择事件
     */
    public void unregisterCheckStateListener() {
        mCheckStateListener = null;
    }

    /**
     * 注册图片点击事件
     *
     * @param listener 事件
     */
    public void registerOnMediaClickListener(OnMediaClickListener listener) {
        mOnMediaClickListener = listener;
    }

    /**
     * 注销图片点击事件
     */
    public void unregisterOnMediaClickListener() {
        mOnMediaClickListener = null;
    }


    /**
     * 返回图片调整大小
     *
     * @param context 上下文
     * @return 列表的每个格子的宽度 * 缩放比例
     */
    private int getImageResize(Context context) {
        if (mImageResize == 0) {
            RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
            int spanCount = ((GridLayoutManager) lm).getSpanCount();
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int availableWidth = screenWidth - context.getResources().getDimensionPixelSize(
                    R.dimen.media_grid_spacing) * (spanCount - 1);
            // 图片调整后的大小：获取列表的每个格子的宽度
            mImageResize = availableWidth / spanCount;
            // 图片调整后的大小 * 缩放比例
            mImageResize = (int) (mImageResize * 0.5f);
        }
        return mImageResize;
    }

    public interface CheckStateListener {
        void onUpdate();
    }

    public interface OnMediaClickListener {
        void onMediaClick(Album album, MultiMedia item, int adapterPosition);
    }

    private static class MediaViewHolder extends RecyclerView.ViewHolder {

        private MediaGrid mMediaGrid;

        MediaViewHolder(View itemView) {
            super(itemView);
            mMediaGrid = (MediaGrid) itemView;
        }
    }

}
