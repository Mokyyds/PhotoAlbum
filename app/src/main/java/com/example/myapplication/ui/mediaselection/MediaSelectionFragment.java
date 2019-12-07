package com.example.myapplication.ui.mediaselection;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.entity.Album;
import com.example.myapplication.entity.MultiMedia;
import com.example.myapplication.model.AlbumMediaCollection;
import com.example.myapplication.ui.mediaselection.adapter.AlbumMediaAdapter;
import com.example.myapplication.ui.photo.MatissFragment;
import com.example.myapplication.widget.MediaGridInset;


/**
 * 相册 界面
 * Created by zhongjh on 2018/8/30.
 */
public class  MediaSelectionFragment extends Fragment implements
        AlbumMediaAdapter.CheckStateListener, AlbumMediaAdapter.OnMediaClickListener {

    private static final String EXTRA_ALBUM = "extra_album";     // 专辑数据

    private final AlbumMediaCollection mAlbumMediaCollection = new AlbumMediaCollection();
    private RecyclerView mRecyclerView;
    private AlbumMediaAdapter mAdapter;
    private AlbumMediaAdapter.OnMediaClickListener mOnMediaClickListener;   // 点击事件

    /**
     * 实例化
     *
     * @param album 专辑
     */
    public static MediaSelectionFragment newInstance(Album album) {
        MediaSelectionFragment fragment = new MediaSelectionFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ALBUM, album);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 生命周期 onAttach() - onCreate() - onCreateView() - onActivityCreated()
     *
     * @param context 上下文
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // 旧版的知乎是用Activity，而这边则使用Fragments的获取到MatissFragment
        Fragment matissFragment = null;
        for (Fragment fragment : getFragmentManager().getFragments()) {
            if (fragment instanceof MatissFragment) {
                matissFragment = fragment;
            }
        }
        if (matissFragment == null)
            throw new IllegalStateException("matissFragment 不能为null");
        mOnMediaClickListener = (AlbumMediaAdapter.OnMediaClickListener) matissFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_selection_zjh, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.recyclerview);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Album album = getArguments().getParcelable(EXTRA_ALBUM);

        // 实例化适配器并且传递数据源
        mAdapter = new AlbumMediaAdapter(getContext(),
                 mRecyclerView);
        mAdapter.registerCheckStateListener(this);
        mAdapter.registerOnMediaClickListener(this);
        mRecyclerView.setHasFixedSize(true);

        // 设置recyclerView的布局
        int spanCount = 3;
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        // 加载线，recyclerView加载数据
        int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
        mRecyclerView.addItemDecoration(new MediaGridInset(spanCount, spacing, false));
        mRecyclerView.setAdapter(mAdapter);
        mAlbumMediaCollection.onCreate(getActivity(), new AlbumMediaCollection.AlbumMediaCallbacks() {

            /**
             * 加载数据完毕
             *
             * @param cursor 光标数据
             */
            @Override
            public void onAlbumMediaLoad(Cursor cursor) {
                mAdapter.swapCursor(cursor);
            }

            /**
             * 当一个已创建的加载器被重置从而使其数据无效时，此方法被调用
             */
            @Override
            public void onAlbumMediaReset() {
                // 此处是用于上面的onLoadFinished()的游标将被关闭时执行，我们需确保我们不再使用它
                mAdapter.swapCursor(null);
            }
        });
        mAlbumMediaCollection.load(album);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAlbumMediaCollection.onDestroy();
    }

    /**
     * 刷新数据源
     */
    public void refreshMediaGrid() {
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onUpdate() {
    }

    @Override
    public void onMediaClick(Album album, MultiMedia item, int adapterPosition) {
        if (mOnMediaClickListener != null) {
            mOnMediaClickListener.onMediaClick((Album) getArguments().getParcelable(EXTRA_ALBUM),
                    item, adapterPosition);
        }
    }


}
