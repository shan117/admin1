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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

//import info.androidhive.retrofit.model.Movie;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Model> movies;
    private int rowLayout;
    private Context context;


    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        LinearLayout moviesLayout;
        TextView movieTitle;
        TextView data;
        TextView movieDescription;
        TextView rating;
        TextView time;

        ImageView faceImage;


        public MovieViewHolder(View v) {
            super(v);
//            moviesLayout = (LinearLayout) v.findViewById(R.id.movies_layout);
//            movieTitle = (TextView) v.findViewById(R.id.title);
//            data = (TextView) v.findViewById(R.id.subtitle);
//            movieDescription = (TextView) v.findViewById(R.id.description);
            rating = (TextView) v.findViewById(R.id.barCode);
            faceImage =(ImageView) v.findViewById(R.id.rating_image2);
            time =(TextView)v.findViewById(R.id.logTime);
        }
    }

    public MoviesAdapter(List<Model> movies, int rowLayout, Context context) {
        this.movies = movies;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new MovieViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MovieViewHolder holder, final int position) {
String a;



        FirebaseStorage storage= FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReferenceFromUrl(movies.get(position).getImagePath());

       /* Glide.with(this.context)
                .using(new FirebaseImageLoader())
                .load(storageRef)
                .into(holder.faceImage);
*/

       Glide.with(this.context)
               .using(new FirebaseImageLoader())
               .load(storageRef)
               .asBitmap()
               .centerCrop()
               .dontAnimate()
               .into(new BitmapImageViewTarget(holder.faceImage){
                   @Override
                   protected void setResource(Bitmap resource){
                       RoundedBitmapDrawable circularBitmapDrawable =
                               RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                       circularBitmapDrawable.setCircular(true);
                       holder.faceImage.setImageDrawable(circularBitmapDrawable);
                   }
               });





        String time =movies.get(position).getTime();
        holder.rating.setText(movies.get(position).getBarcodeValue());
        holder.time.setText(movies.get(position).getTime());


/*       byte[] byteArray = movies.get(position).getFaceImage().getBytes();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        holder.faceImage.setImageBitmap(bitmap);*/
//        holder.movieTitle.setText(movies.get(position).getFaceImage());
//        holder.data.setText(movies.get(position).getReleaseDate());
//        holder.movieDescription.setText(movies.get(position).getOverview());
//        holder.rating.setText(movies.get(position).getVoteAverage().toString());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}