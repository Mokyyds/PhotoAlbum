package com.example.myapplication.preview;

import android.os.Bundle;

import androidx.annotation.Nullable;


import com.example.myapplication.entity.MultiMedia;
import com.example.myapplication.model.SelectedItemCollection;

import java.util.List;


/**
 * 预览界面进来的
 */
public class SelectedPreviewActivity extends BasePreviewActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getBundleExtra(EXTRA_DEFAULT_BUNDLE);
        List<MultiMedia> selected = bundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
        mAdapter.addAll(selected);
        mAdapter.notifyDataSetChanged();
        mPreviousPos = 0;
        updateSize(selected.get(0));
    }

}