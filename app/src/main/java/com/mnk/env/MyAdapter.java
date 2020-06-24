package com.mnk.env;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<UserComponentListItem> listItems;

    public MyAdapter(List<UserComponentListItem> listItems) {
        this.listItems = listItems;
    }

    public MyAdapter() {}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserComponentListItem userComponentListItem = listItems.get(position);

        holder.imageView.setImageResource(userComponentListItem.getImageView());
        holder.textView.setText(userComponentListItem.getHeading());
    }

    @Override
    public int getItemCount() { return listItems.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.rowImage);
            textView = itemView.findViewById(R.id.rowHead);
        }
    }
}
