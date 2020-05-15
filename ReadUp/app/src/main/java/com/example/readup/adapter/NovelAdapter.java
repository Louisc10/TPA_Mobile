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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.readup.ComicDetailActivity;
import com.example.readup.NovelDetailActivity;
import com.example.readup.R;
import com.example.readup.model.Comic;
import com.example.readup.model.Novel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class NovelAdapter extends RecyclerView.Adapter<NovelAdapter.ViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Novel> novels;
    private ArrayList<Novel> full;

    public NovelAdapter(Context context, ArrayList<Novel> novels) {
        this.context = context;
        this.novels = novels;
        this.full = new ArrayList<>(novels);
    }

    public NovelAdapter(Context context) {
        this.context = context;
    }

    public void setNovels(ArrayList<Novel> novels) {
        this.novels = novels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.item_book, parent, false);

        return new ViewHolder(view,context,novels);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tvTitle.setText(novels.get(position).getNovelName());
        holder.tvBook.setText(novels.get(position).getNovelAuthor());
        holder.tvGenre.setText(novels.get(position).getNovelGenre());
        StorageReference reff = FirebaseStorage.getInstance().getReference("Images").child(novels.get(position).getNovelName());
        reff.child("Tumbnail.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
        return novels.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Novel> list = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                list.addAll(full);
            }
            else{
                String pattern = constraint.toString().toLowerCase().trim();

                for (Novel c: full) {
                    if(c.getNovelGenre().toLowerCase().contains(pattern)){
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
            novels.clear();
            novels.addAll((ArrayList<Novel>)results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        ImageView iv;
        TextView tvTitle, tvBook, tvGenre;

        Context context;
        ArrayList<Novel> novels;

        public ViewHolder(@NonNull View itemView,Context context,ArrayList<Novel> novels) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBook = itemView.findViewById(R.id.tvBook);
            tvGenre = itemView.findViewById(R.id.tvGenre);

            itemView.setOnClickListener(this);
            this.context = context;
            this.novels = novels;

            iv = itemView.findViewById(R.id.tumb);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, NovelDetailActivity.class);
            i.putExtra("Novel_Id",getAdapterPosition()+1);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

}
