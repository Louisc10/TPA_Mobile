package com.example.readup.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.readup.ComicDetailActivity;
import com.example.readup.R;
import com.example.readup.model.Comic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ViewHolder>implements Filterable {

    private Context context;
    private ArrayList<Comic> comics;
    private ArrayList<Comic> full;

    public ComicAdapter(Context context, ArrayList<Comic> comics) {
        this.context = context;
        this.comics = comics;
        this.full = new ArrayList<>(comics);
    }

    public ComicAdapter(Context context) {
        this.context = context;
    }

    public void setComics(ArrayList<Comic> comics) {
        this.comics = comics;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.item_book, parent, false);

        return new ViewHolder(view,context,comics);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tvTitle.setText(comics.get(position).getComicName());
        holder.tvBook.setText(comics.get(position).getComicAuthor());
        holder.tvGenre.setText(comics.get(position).getComicGenre());
        StorageReference reff = FirebaseStorage.getInstance().getReference("Images").child(comics.get(position).getComicName());
        reff.child("Page0.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(context)
                        .load(uri)
                        .into(holder.iv);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    @Override
    public int getItemCount() {
        return comics.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Comic> list = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                list.addAll(full);
            }
            else{
                String pattern = constraint.toString().toLowerCase().trim();

                for (Comic c: full) {
                    if(c.getComicGenre().toLowerCase().contains(pattern)){
                        list.add(c);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = list;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            comics.clear();
            comics.addAll((ArrayList<Comic>)results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView iv;
        TextView tvTitle, tvBook, tvGenre;

        Context context;
        ArrayList<Comic> comics;

        public ViewHolder(@NonNull final View itemView, Context context, ArrayList<Comic> comics) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBook = itemView.findViewById(R.id.tvBook);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            itemView.setOnClickListener(this);
            this.context=context;
            this.comics=comics;

            iv = itemView.findViewById(R.id.tumb);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, ComicDetailActivity.class);
            i.putExtra("Comic_Id",getAdapterPosition()+1);
            i.putExtra("Comic_Name",tvTitle.getText().toString());
            i.putExtra("Comic_Author",tvBook.getText().toString());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }


    }
}
