package com.example.myapplication.model;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;


import com.example.myapplication.R;
import com.example.myapplication.entity.IncapableCause;
import com.example.myapplication.entity.MultiMedia;
import com.example.myapplication.utils.PathUtils;
import com.example.myapplication.utils.PhotoMetadataUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * 选择的数据源
 * Created by zhongjh on 2018/8/28.
 */
public class SelectedItemCollection {

    public static final String STATE_SELECTION = "state_selection"; // 数据源的标记
    public static final String STATE_COLLECTION_TYPE = "state_collection_type";

    /**
     * 空的数据类型
     */
    public static final int COLLECTION_UNDEFINED = 0x00;
    /**
     * 图像数据类型
     */
    public static final int COLLECTION_IMAGE = 0x01;
    /**
     * 视频数据类型
     */
    public static final int COLLECTION_VIDEO = 0x01 << 1;
    /**
     * 图像和视频混合类型
     */
    private static final int COLLECTION_MIXED = COLLECTION_IMAGE | COLLECTION_VIDEO;

    private final Context mContext;
    private Set<MultiMedia> mItems;       // 数据源
    private int mCollectionType = COLLECTION_UNDEFINED; // 类型

    public SelectedItemCollection(Context context) {
        mContext = context;
    }

    /**
     * @param bundle        数据源
     * @param isAllowRepeat 是否允许重复
     */
    public void onCreate(Bundle bundle, boolean isAllowRepeat) {
        if (bundle == null) {
            mItems = new LinkedHashSet<>();
        } else {
            // 获取缓存的数据
            List<MultiMedia> saved = bundle.getParcelableArrayList(STATE_SELECTION);
            if (saved != null) {
                if (isAllowRepeat) {
                    mItems = new LinkedHashSet<>();
                    mItems.addAll(saved);
                } else {
                    mItems = new LinkedHashSet<>(saved);
                }
            }

            mCollectionType = bundle.getInt(STATE_COLLECTION_TYPE, COLLECTION_UNDEFINED);
        }
    }

    /**
     * 缓存数据
     *
     * @param outState 缓存
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
        outState.putInt(STATE_COLLECTION_TYPE, mCollectionType);
    }

    /**
     * 将数据保存进Bundle并且返回
     *
     * @return Bundle
     */
    public Bundle getDataWithBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
        bundle.putInt(STATE_COLLECTION_TYPE, mCollectionType);
        return bundle;
    }

    /**
     * 将资源对象添加到已选中集合
     *
     * @param item 数据
     */
    public boolean add(MultiMedia item) {
        boolean added = mItems.add(item);
        // 如果只选中了图片Item， mCollectionType设置为COLLECTION_IMAGE
        // 如果只选中了图片影音资源，mCollectionType设置为COLLECTION_IMAGE
        // 如果两种都选择了，mCollectionType设置为COLLECTION_MIXED
        if (added) {
            // 如果是空的数据源
            if (mCollectionType == COLLECTION_UNDEFINED) {
                if (item.isImage()) {
                    // 如果是图片，就设置图片类型
                    mCollectionType = COLLECTION_IMAGE;
                } else if (item.isVideo()) {
                    // 如果是视频，就设置视频类型
                    mCollectionType = COLLECTION_VIDEO;
                }
            } else if (mCollectionType == COLLECTION_IMAGE) {
                // 如果当前是图片类型
                if (item.isVideo()) {
                    // 选择了视频，就设置混合模式
                    mCollectionType = COLLECTION_MIXED;
                }
            } else if (mCollectionType == COLLECTION_VIDEO) {
                // 如果当前是图片类型
                if (item.isImage()) {
                    // 选择了图片，就设置混合模式
                    mCollectionType = COLLECTION_MIXED;
                }
            }
        }
        return added;
    }




    /**
     * 重置数据源
     *
     * @param items          数据源
     * @param collectionType 类型
     */
    public void overwrite(ArrayList<MultiMedia> items, int collectionType) {
        if (items.size() == 0) {
            mCollectionType = COLLECTION_UNDEFINED;
        } else {
            mCollectionType = collectionType;
        }
        mItems.clear();
        mItems.addAll(items);
    }

    /**
     * 转换成list
     *
     * @return list<Item>
     */
    public List<MultiMedia> asList() {
        return new ArrayList<>(mItems);
    }

    /**
     * 获取uri的集合
     *
     * @return list<Uri>
     */
    public List<Uri> asListOfUri() {
        List<Uri> uris = new ArrayList<>();
        for (MultiMedia item : mItems) {
            if (item.getMediaUri() != null)
                uris.add(item.getMediaUri());
            else
                uris.add(item.getUri());
        }
        return uris;
    }

    /**
     * 获取path的集合
     *
     * @return list<path>
     */
    public List<String> asListOfString() {
        List<String> paths = new ArrayList<>();
        for (MultiMedia item : mItems) {
            if (item.getMediaUri() != null)
                paths.add(PathUtils.getPath(mContext, item.getMediaUri()));
            else
                paths.add(PathUtils.getPath(mContext, item.getUri()));

        }
        return paths;
    }

    /**
     * 该item是否在选择中
     *
     * @param item 数据源
     * @return 返回是否选择
     */
    public boolean isSelected(MultiMedia item) {
        return mItems.contains(item);
    }



    /**
     * 获取数据源长度
     *
     * @return 数据源长度
     */
    public int count() {
        return mItems.size();
    }



}
