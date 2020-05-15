package com.example.readup;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    Button bcomic;
    Button bnovel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = null;
        view = inflater.inflate(R.layout.fragment_home, container, false);

        bcomic = view.findViewById(R.id.b_comic);
        bnovel = view.findViewById(R.id.b_novel);

        bcomic.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ComicActivity.class));
            }
        });
        bnovel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NovelActivity.class));
                NovelActivity.upating = false;
            }
        });

        return view;
    }
}
