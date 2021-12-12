package com.example.mybooks;

import android.content.ContentValues;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    List<BooksModal> list;
    Context context;
    private final int VIEW_TYPE_ITEMS=0,VIEW_TYPE_LOADING=1;
    public BookAdapter(List<BooksModal> list, Context context) {
        this.context=context;
        this.list = list;
    }

    @NonNull
    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.ViewHolder holder, int position) {
        String x=list.get(position).getImage();
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttpDownloader(context));
        builder.build().load(x).into(holder.image);
        holder.name.setText(list.get(position).getName());
        holder.author.setText(list.get(position).getAuthor());
        holder.rating.setText(list.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,author,rating;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.imageView);
            name=itemView.findViewById(R.id.book_title);
            author=itemView.findViewById(R.id.author);
            rating=itemView.findViewById(R.id.ratings);

        }
    }
    public class LoadingViewHolder extends RecyclerView.ViewHolder{
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar=itemView.findViewById(R.id.progressBarx);
        }
    }
}
