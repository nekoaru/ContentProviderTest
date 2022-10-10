package com.bisapp.mycontentprovider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.bisapp.customrecyclerview.CustomRecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CustomRecyclerView.BindViewsListener {

    public static final int LOADER_ID = 1;
    private CustomRecyclerView customRecyclerView;
    private List<VideoFiles> videoFiles = new ArrayList<>();
    private ContentResolver contentResolver;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentResolver = getContext().getContentResolver();
        customRecyclerView = view.findViewById(R.id.category_recyclerview);
        customRecyclerView.setBindViewsListener(this);
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);


    }

    private void fetchVideos(Cursor cursor) {

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));
                Long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE));
                String dateModified = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_MODIFIED));

                VideoFiles videos = new VideoFiles();
                Uri contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                Uri thumbnailsUri = ContentUris.withAppendedId(contentUri, id);
                videos.setDate(dateModified);
                videos.setSize(size);
                videos.setContentUri(thumbnailsUri);
                videos.setFiles(data);

                videoFiles.add(videos);
                cursor.moveToNext();
            }
            cursor.close();
        }

        customRecyclerView.addModels(videoFiles);


    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projections = {
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.DATE_MODIFIED,
                MediaStore.Video.VideoColumns.SIZE

        };
        Uri contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        return new CursorLoader(getContext(), contentUri, projections, null, null, null);
    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        fetchVideos(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        loader.reset();

    }

    @Override
    public void bindViews(View view, List<?> objects, int position) {

        VideoFiles videoFile = videoFiles.get(position);
        final ImageView videoView = view.findViewById(R.id.image_view);
        TextView titleTextView = view.findViewById(R.id.title);
        TextView sizeTextView = view.findViewById(R.id.size);

        String data = videoFile.getFiles();

        File videoFil = new File(data);
        titleTextView.setText(videoFil.getName());
        sizeTextView.setText(Formatter.formatFileSize(sizeTextView.getContext(), videoFile.size));
        // if (position != 1) {
        //createVideoFile(videoFile, videoView);
        createThumbnails(videoFile, videoView);


        //}
    }

    private void createThumbnails(VideoFiles videoFile, ImageView videoView) {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                bitmap = contentResolver.loadThumbnail(videoFile.contentUri, new Size(100, 100), null);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            long id = ContentUris.parseId(videoFile.contentUri);
            bitmap = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, id, MediaStore.Video.Thumbnails.MINI_KIND, null);
        }
        videoView.setImageBitmap(bitmap);
    }

    private void createVideoFile(VideoFiles videoFile, VideoView videoView) {
        videoView.setVideoURI(videoFile.contentUri);
        videoView.requestFocus();
        videoView.start();
    }

    private static class VideoFiles {
        private String files;
        private String date;
        private long size;
        private Uri contentUri;

        public Uri getContentUri() {
            return contentUri;
        }

        public void setContentUri(Uri contentUri) {
            this.contentUri = contentUri;
        }

        public String getFiles() {
            return files;
        }

        public void setFiles(String files) {
            this.files = files;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }
    }
}