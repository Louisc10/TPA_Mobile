package com.example.readup;

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

import com.example.readup.adapter.ComicAdapter;
import com.example.readup.model.Comic;
import com.example.readup.model.Genre;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class SearchComicFragment extends Fragment {

    private Spinner spinner;
    private Button search;

    private ComicAdapter cAdapter;
    private RecyclerView rvBook;
    private ArrayList<Genre> genres;
    private ArrayList<Comic> comics;

    private DatabaseReference genreReference;
    private DatabaseReference comicReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_search_comic, container, false);


        spinner = view.findViewById(R.id.spinner);
        search = view.findViewById(R.id.search_comic_btn);

        genres = new ArrayList<>();
        comics = new ArrayList<>();

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

        comicReference = FirebaseDatabase.getInstance().getReference().child("Comic");

        comicReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long i = 1;
                comics = new ArrayList<>();
                for (; i <= dataSnapshot.getChildrenCount(); i++) {
                    String n = dataSnapshot.child(String.valueOf(i)).child("comicName").getValue().toString();
                    String g = dataSnapshot.child(String.valueOf(i)).child("comicGenre").getValue().toString();
                    String a = dataSnapshot.child(String.valueOf(i)).child("comicAuthor").getValue().toString();

                    String k = genres.get(Integer.parseInt(g)).getGenreName();
                    comics.add(new Comic(n, k, a));
                }
                cAdapter = new ComicAdapter(getActivity().getApplicationContext(), comics);
                cAdapter.notifyDataSetChanged();

                rvBook = view.findViewById(R.id.rvBook);
                rvBook.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
                rvBook.setAdapter(cAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Genre genre = (Genre) spinner.getSelectedItem();
                if (!genre.getGenreName().equals("-"))
                    cAdapter.getFilter().filter(genre.getGenreName());
                else
                    cAdapter.getFilter().filter("");
            }
        });

        return view;
    }
}
