package com.example.myapplication.ui.photo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.album.loader.AlbumLoader;
import com.example.myapplication.entity.Album;
import com.example.myapplication.entity.AlbumBean;
import com.example.myapplication.entity.MultiMedia;
import com.example.myapplication.model.AlbumCollection;
import com.example.myapplication.preview.AlbumPreviewActivity;
import com.example.myapplication.preview.BasePreviewActivity;
import com.example.myapplication.ui.mediaselection.MediaSelectionFragment;
import com.example.myapplication.ui.mediaselection.adapter.AlbumMediaAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.Constant.REQUEST_CODE_PREVIEW;

public class MatissFragment extends Fragment implements AlbumCollection.AlbumCallbacks
        , AlbumMediaAdapter.OnMediaClickListener{

    private static final String EXTRA_RESULT_ORIGINAL_ENABLE = "extra_result_original_enable";

    private static final String CHECK_STATE = "checkState";

    private Activity mActivity;
    private Context mContext;

    private final AlbumCollection mAlbumCollection = new AlbumCollection();

    public static MatissFragment newInstance() {
        MatissFragment matissFragment = new MatissFragment();
        Bundle args = new Bundle();
        matissFragment.setArguments(args);
        return matissFragment;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matiss_fragment, container, false);
        mAlbumCollection.onCreate(this, this);
        mAlbumCollection.onRestoreInstanceState(savedInstanceState);
        mAlbumCollection.loadAlbums();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

    void initData() {

    }

    @Override
    public void onAlbumLoadFinished(Cursor cursor) {
        List<Album> albumList = new ArrayList<Album>();
        Album album = null;
        while (cursor.moveToNext()) {
            album = new Album(
                    cursor.getString(cursor.getColumnIndex("bucket_id")),
                    cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)),
                    cursor.getString(cursor.getColumnIndex("bucket_display_name")),
                    cursor.getLong(cursor.getColumnIndex(AlbumLoader.COLUMN_COUNT)));
            albumList.add(album);
        }
        Fragment fragment = MediaSelectionFragment.newInstance(album);
        if (getFragmentManager() != null)
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment, MediaSelectionFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
    }

    @Override
    public void onAlbumReset() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 销毁相册model
        mAlbumCollection.onDestroy();
    }

    @Override
    public void onMediaClick(Album album, MultiMedia item, int adapterPosition) {
        Intent intent = new Intent(getActivity(), AlbumPreviewActivity.class);
        intent.putExtra(AlbumPreviewActivity.EXTRA_ALBUM, album);
        intent.putExtra(AlbumPreviewActivity.EXTRA_ITEM, item);
        startActivityForResult(intent, REQUEST_CODE_PREVIEW);

    }
}
