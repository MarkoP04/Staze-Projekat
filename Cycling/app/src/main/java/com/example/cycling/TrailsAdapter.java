package com.example.cycling;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TrailsAdapter extends RecyclerView.Adapter<TrailsAdapter.TrailViewHolder> {

    List<Trail> trails;

    public TrailsAdapter(List<Trail> trails){
        this.trails = trails;
    }

    @NonNull
    @Override
    public TrailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trail, parent, false);
        return new TrailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailViewHolder holder, int position) {
        Trail t = trails.get(position);

        // Prikaz imena lokacija
        holder.trailName.setText(TextUtils.join(", ", t.getLocations()));

        // Učitavanje slike iz lokalnog storage
        String path = t.getImagePath();
        if (path != null && !path.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                holder.trailImage.setImageBitmap(bitmap);
            } else {
                // fallback ako bitmap ne može da se učita
                holder.trailImage.setImageResource(R.drawable.ic_trail_placeholder);
            }
        } else {
            holder.trailImage.setImageResource(R.drawable.ic_trail_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return trails.size();
    }

    static class TrailViewHolder extends RecyclerView.ViewHolder {
        ImageView trailImage;
        TextView trailName;
        public TrailViewHolder(@NonNull View itemView) {
            super(itemView);
            trailImage = itemView.findViewById(R.id.trailImage);
            trailName = itemView.findViewById(R.id.trailName);
        }
    }
}

