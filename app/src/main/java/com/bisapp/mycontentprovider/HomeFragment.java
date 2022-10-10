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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bisapp.customrecyclerview.CustomRecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements CustomRecyclerView.BindViewsListener {

    public static final int LOADER_ID = 1;
    private CustomRecyclerView customRecyclerView;
    private List<String> headers = new ArrayList<>();
    private NavController navController;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        headers.clear();
        headers.add("Query for Videos");
        headers.add("Using In App Provider");
        customRecyclerView = view.findViewById(R.id.category_recyclerview);
        customRecyclerView.setBindViewsListener(this);
        customRecyclerView.addModels(headers);

        navController = Navigation.findNavController(view);

    }

    @Override
    public void bindViews(View view, List<?> objects, final int position) {

        String header = headers.get(position);
        final TextView path = view.findViewById(R.id.music);
        path.setText(header);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position == 0){
                    navController.navigate(R.id.action_homeFragment_to_FirstFragment);
                }else if (position == 1){
                    navController.navigate(R.id.action_homeFragment_to_SecondFragment);
                }

            }
        });

    }

}