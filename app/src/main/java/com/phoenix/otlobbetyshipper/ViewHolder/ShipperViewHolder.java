package com.phoenix.otlobbetyshipper.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.phoenix.otlobbetyshipper.R;

public class ShipperViewHolder extends RecyclerView.ViewHolder  {

    public TextView txtShipperId,txtShipperName,txtOrderID;


    public ShipperViewHolder(@NonNull View itemView) {
        super(itemView);

        txtShipperId = (TextView) itemView.findViewById(R.id.order_Shipper_ID);
        txtShipperName = (TextView) itemView.findViewById(R.id.order_Shipper_Name);
        txtOrderID = (TextView) itemView.findViewById(R.id.order_ID);



    }
}