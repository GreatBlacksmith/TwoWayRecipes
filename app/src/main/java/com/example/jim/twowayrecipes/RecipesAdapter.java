package com.example.jim.twowayrecipes;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by Jim on 4/24/2016.
 */
public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.MyViewHolder> {
    private List<Recipe> recipeList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView titleView;
        SimpleDraweeView imageView;

        public MyViewHolder(View view) {
            super(view);
            titleView = (TextView) view.findViewById(R.id.list_item_title);
            imageView = (SimpleDraweeView) view.findViewById(R.id.list_item_image_view);
        }
    }

    public RecipesAdapter(List<Recipe> recipes){
        this.recipeList = recipes;
    }
    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);


        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Recipe recipe = recipeList.get(position);
        holder.titleView.setText(recipe.getTitle());

        Uri uri = Uri.parse(recipe.getImage_url());
        holder.imageView.setImageURI(uri);
    }
    @Override
    public int getItemCount(){
        return recipeList.size();
    }
}

