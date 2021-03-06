package com.example.shan.admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.shan.admin.misc.Helper;
import com.example.shan.admin.pojo.Scan;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ScanListAdapter extends RecyclerView.Adapter<ScanListAdapter.MovieViewHolder> {

    private List<Scan> list;
    private int rowLayout;
    private Context context;


    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvDate;
        TextView tvName;
        TextView tvUserId;
        TextView tvLocation;

        ImageView iv_profile;


        public MovieViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tv_name);
            tvUserId = (TextView) v.findViewById(R.id.tv_username);
            tvLocation = (TextView) v.findViewById(R.id.tv_location);
            tvTime = (TextView) v.findViewById(R.id.tv_time);
            tvDate = (TextView) v.findViewById(R.id.tv_date);

            iv_profile = (ImageView) v.findViewById(R.id.iv_profile);
        }
    }

    public ScanListAdapter(List<Scan> list, int rowLayout, Context context) {
        this.list = list;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public ScanListAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new MovieViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MovieViewHolder holder, final int position) {
        String a;

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(list.get(position).getImagePath());

        Glide.with(this.context)
                .using(new FirebaseImageLoader())
                .load(storageRef)
                .asBitmap()
                .centerCrop()
                .dontAnimate()
                .into(new BitmapImageViewTarget(holder.iv_profile) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.iv_profile.setImageDrawable(circularBitmapDrawable);
                    }
                });
        String date=list.get(position).getTime().substring(0,10);
        String time=list.get(position).getTime().substring(11);

        if(position>0)
        {
            if(date.contentEquals(list.get(position-1).getTime().substring(0,10)))
            {
                holder.tvDate.setVisibility(View.GONE);
            }else {
                holder.tvDate.setVisibility(View.VISIBLE);
                holder.tvDate.setText(date);
            }
        }else {
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvDate.setText(date);
        }

        holder.tvLocation.setText(""+list.get(position).getLocationName());
        holder.tvTime.setText(time);
        holder.tvName.setText(list.get(position).getSupervisorName());
        holder.tvUserId.setText(Helper.base64ToString(list.get(position).getSupervisor()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}