package com.karanbajaj.ecommerce.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.karanbajaj.ecommerce.Interface.ItemClickListner;
import com.karanbajaj.ecommerce.R;

public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductDescription, txtProductPrice, txtProductStatus;
    public ImageView imageView;
    public ItemClickListner listner;

    public itemViewHolder(@NonNull View itemView) {
        super(itemView);

        txtProductDescription = itemView.findViewById(R.id.product_seller_description);
        txtProductPrice = itemView.findViewById(R.id.product_seller_price);
        txtProductName = itemView.findViewById(R.id.product_seller_name);
        imageView = itemView.findViewById(R.id.product_seller_image);
        txtProductStatus = itemView.findViewById(R.id.seller_product_state);

    }

    public void setItemClickListner(ItemClickListner listner){

        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);

    }
}
