package com.phoenix.otlobbetyshipper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.phoenix.otlobbetyshipper.Common.Common;
import com.phoenix.otlobbetyshipper.Model.Push;
import com.phoenix.otlobbetyshipper.ViewHolder.ShipperViewHolder;

import info.hoang8f.widget.FButton;

public class MyOrders extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    CardView cardView;
    FirebaseRecyclerAdapter<Push, ShipperViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference pushs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        //Firebase
        database = FirebaseDatabase.getInstance();
        pushs = database.getReference("Push");

        recyclerView = (RecyclerView) findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(Common.currentUser.getPhone());

    }
    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Push, ShipperViewHolder>(
                Push.class, R.layout.my_order_layout, ShipperViewHolder.class,

                pushs.orderByChild("phone").equalTo(phone)


        ) {
            @Override
            protected void populateViewHolder(ShipperViewHolder shipperViewHolder, Push push, int position) {

                shipperViewHolder.txtOrderID.setText(adapter.getRef(position).getKey());
                shipperViewHolder.txtOrderID.setText(push.getIdnum());
                shipperViewHolder.txtShipperId.setText(push.getName());
                shipperViewHolder.txtShipperName.setText(push.getPhone());

            }
        };
        recyclerView.setAdapter(adapter);
    }

}

