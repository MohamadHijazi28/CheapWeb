package com.example.cheapweb;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;


public class ViewHolder extends RecyclerView.ViewHolder {

    View mView;
    protected ViewHolder.ClickListener mClickListener;

    public ViewHolder(@NonNull final View itemView) {
        super(itemView);

        mView=itemView;

        //item click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view,getAdapterPosition());
            }
        });

        //item long click
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());
                return true;
            }
        });

    }

    //set details to recycler view in item show
    public void setDetails(Context context, String itemname, String itemprice, String itemimage){

        TextView ItemPrice=mView.findViewById(R.id.Item_Price);
        TextView ItemName=mView.findViewById(R.id.Item_Name);
        ImageView ImagePath=mView.findViewById(R.id.Item_image);

        ItemPrice.setText(itemprice);
        ItemName.setText(itemname);
        Picasso.with(context).load(itemimage).into(ImagePath);
    }

    //set details to recycler view in favorite item
    public void setDetailsFavorite(Context context, String itemname, String itemprice, String itemimage){

        TextView ItemPrice=mView.findViewById(R.id.ItPricetxt);
        TextView ItemName=mView.findViewById(R.id.ItNametxt);
        ImageView ImagePath=mView.findViewById(R.id.Itimage);

        ItemPrice.setText(itemprice);
        ItemName.setText(itemname);
        Picasso.with(context).load(itemimage).into(ImagePath);
    }

    //interface to send callback
    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(ViewHolder.ClickListener ClickListener) {
        mClickListener = ClickListener;
    }
}

