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
package com.example.myapplication.preview.previewitem;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.engine.ImageEngine;
import com.example.myapplication.engine.impl.Glide4Engine;
import com.example.myapplication.entity.MultiMedia;
import com.example.myapplication.utils.FileUtil;
import com.example.myapplication.utils.PhotoMetadataUtils;

import java.io.File;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class PreviewItemFragment extends Fragment {

    private static final String ARGS_ITEM = "args_item";
    ImageEngine imageEngine = new Glide4Engine();
    public static PreviewItemFragment newInstance(MultiMedia item) {
        PreviewItemFragment fragment = new PreviewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_ITEM, item);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview_item_zjh, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final MultiMedia item = getArguments().getParcelable(ARGS_ITEM);
        if (item == null) {
            return;
        }

        View videoPlayButton = view.findViewById(R.id.video_play_button);
        if (item.isVideo()) {
            videoPlayButton.setVisibility(View.VISIBLE);
            videoPlayButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = null;
                if (item.getMediaUri() != null) {
                    uri = item.getMediaUri();
                }else if(item.getUri() != null) {
                    item.setMediaUri(FileUtil.getFileUri(getContext(),item.getType(),new File(item.getPath())));
                    uri = item.getMediaUri();
                }
                intent.setDataAndType(uri, "video/*");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), R.string.error_no_video_activity, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            videoPlayButton.setVisibility(View.GONE);
        }


        ImageViewTouch image = view.findViewById(R.id.image_view);
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        if (item.getMediaUri() != null) {
            Point size = PhotoMetadataUtils.getBitmapSize(item.getMediaUri(), getActivity());
            if (item.isGif()) {
                imageEngine.loadGifImage(getContext(), size.x, size.y, image,
                        item.getMediaUri());
            } else {
                imageEngine.loadImage(getContext(), size.x, size.y, image,
                        item.getMediaUri());

            }
        } else if (item.getUri() != null) {
            imageEngine.loadUriImage(getContext(), image,
                    item.getUri());
        } else if (item.getUrl() != null) {
            imageEngine.loadUrlImage(getContext(), image,
                    item.getUrl());
        } else if (item.getDrawableId() != -1) {
            imageEngine.loadDrawableImage(getContext(), image,
                    item.getDrawableId());
        }


    }

    public void resetView() {
        if (getView() != null) {
            ((ImageViewTouch) getView().findViewById(R.id.image_view)).resetMatrix();
        }
    }

}
