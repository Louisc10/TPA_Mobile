package com.example.readup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readup.adapter.NovelAdapter;
import com.example.readup.model.Genre;
import com.example.readup.model.Novel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class SearchNovelFragment extends Fragment {

    private Spinner spinner;
    private Button search;

    private NovelAdapter nAdapter;
    private RecyclerView rvBook;
    private ArrayList<Genre> genres;
    private ArrayList<Novel> novels;

    private DatabaseReference genreReference;
    private DatabaseReference novelReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_search_novel, container, false);

        spinner = view.findViewById(R.id.spinner);
        search = view.findViewById(R.id.search_novel_btn);

        genres = new ArrayList<>();
        novels = new ArrayList<>();

        String lang = Locale.getDefault().getLanguage();
        System.out.println("Lang: " + lang);
        genreReference = FirebaseDatabase.getInstance().getReference().child("Genre").child(lang);

        genreReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long i = 1;
                genres = new ArrayList<>();
                genres.add(new Genre("-"));
                for (; i <= dataSnapshot.getChildrenCount(); i++) {
                    String n = dataSnapshot.child(String.valueOf(i)).child("genreName").getValue().toString();
                    System.out.println("Genre: " + n);
                    genres.add(new Genre(n));
                }

                ArrayAdapter<Genre> adapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, genres);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        novelReference = FirebaseDatabase.getInstance().getReference().child("Novel");

        novelReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long i = 1;
                novels = new ArrayList<>();
                for (; i <= dataSnapshot.getChildrenCount(); i++) {
                    String n = dataSnapshot.child(String.valueOf(i)).child("novelName").getValue().toString();
                    String g = dataSnapshot.child(String.valueOf(i)).child("novelGenre").getValue().toString();
                    String a = dataSnapshot.child(String.valueOf(i)).child("novelAuthor").getValue().toString();

                    String k = genres.get(Integer.parseInt(g)).getGenreName();
                    novels.add(new Novel(n, k, a));
                }
                nAdapter = new NovelAdapter(getActivity().getApplicationContext(), novels);
                nAdapter.notifyDataSetChanged();

                rvBook = view.findViewById(R.id.rvBook);
                rvBook.setLayoutManager(new GridLayoutManager(getActivity().getBaseContext(), 2));
                rvBook.setAdapter(nAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Genre genre = (Genre) spinner.getSelectedItem();
                if(!genre.getGenreName().equals("-"))
                    nAdapter.getFilter().filter(genre.getGenreName());
                else
                    nAdapter.getFilter().filter("");
            }
        });

        return view;
    }
}
