package com.phoenix.otlobbetyshipper;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.phoenix.otlobbetyshipper.Common.Common;
import com.phoenix.otlobbetyshipper.Interface.ItemClickListener;
import com.phoenix.otlobbetyshipper.Model.MyResponse;
import com.phoenix.otlobbetyshipper.Model.Notification;
import com.phoenix.otlobbetyshipper.Model.Push;
import com.phoenix.otlobbetyshipper.Model.Request;
import com.phoenix.otlobbetyshipper.Model.Sender;
import com.phoenix.otlobbetyshipper.Model.Token;
import com.phoenix.otlobbetyshipper.Remote.APIService;
import com.phoenix.otlobbetyshipper.ViewHolder.OrderViewHolder;
import com.phoenix.otlobbetyshipper.Model.Request;
import com.phoenix.otlobbetyshipper.ViewHolder.OrderViewHolder;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.phoenix.otlobbetyshipper.Common.Common.convertCodeToStatus;

public class OrdrerStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    OrderViewHolder orderViewHolder;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    Button bntedit;

    FirebaseDatabase db;
    DatabaseReference requests ;


    MaterialSpinner spinner;
    Push push;

    Request request;
    FButton btnmyorders;
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordrer_status);

        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");
        btnmyorders = (FButton)findViewById(R.id.myOrders);


        //Init Service
        mService = Common.getFCMService();

        recyclerView = (RecyclerView) findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        btnmyorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ordersintent = new Intent(OrdrerStatus.this,MyOrders.class);
                startActivity(ordersintent);
            }
        });

        loadOrders();

    }

    private void loadOrders() {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.layout_order,
                OrderViewHolder.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, final Request request, final int position) {
                orderViewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                orderViewHolder.txtOrderStatus.setText(convertCodeToStatus(request.getStatus()));


                if (orderViewHolder.txtOrderStatus.getText()=="Accepted"){
                    orderViewHolder.btnEdit.setVisibility(View.GONE);

                }
                orderViewHolder.txtOrderAddress.setText(request.getAddress());
                orderViewHolder.txtOrderPhone.setText(request.getPhone());

                //New event Button
                orderViewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(adapter.getRef(position).getKey(),
                                adapter.getItem(position));
                    }
                });

                orderViewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent orderDetail = new Intent (OrdrerStatus.this,OrderDetail.class);
                        Common.currentRequest = request;
                        orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void showUpdateDialog(final String key, final Request item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrdrerStatus.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_show,null);

        spinner = (MaterialSpinner)view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed","Accepted");

        alertDialog.setView(view);

        final String localKey = key;

        final DatabaseReference ref = db.getReference("Push");

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                 EditText editTextshippernum = (EditText)view.findViewById(R.id.id_order_for_shipper);
                //TextView txtOrderId = (TextView)findViewById(R.id.order_id_num);
               // editTextshippernum.setText(txtOrderId.getText().toString());
                push = new Push(Common.currentUser.getName(), Common.currentUser.getPhone(), editTextshippernum.getText().toString());
                String edit = editTextshippernum.getText().toString();

                if (spinner.getSelectedIndex() == 1) {
                    if (TextUtils.isEmpty(edit)) {
                        Toast.makeText(OrdrerStatus.this, "Please Enter Id number of Order", Toast.LENGTH_SHORT).show();
                    }else{
                            bntedit = (Button) findViewById(R.id.btnEdit);
                            bntedit.setVisibility(View.GONE);
                            ref.push().setValue(push);

                            finish();

                            Toast.makeText(OrdrerStatus.this, "Done", Toast.LENGTH_SHORT).show();

                            requests.child(localKey).setValue(item);
                            adapter.notifyDataSetChanged(); // Add to Alert
                            sendOrderStatusToUser(localKey, item);
                        }
                } else {
                    dialog.dismiss();
                }

            }


        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();


    }


    private void sendOrderStatusToUser(final String key, Request item) {
        DatabaseReference tokens = db.getReference("Token");
        tokens.orderByKey().equalTo(item.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            Token token = postSnapShot.getValue(Token.class);

                            //Make raw payload
                            Notification notification = new Notification("OtlobBetyServer","Your Order "+key+" was updated");
                            Sender content = new Sender(token.getToken(),notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                            if (response.body().success ==1) //  هنا في مشكه مش عارفها
                                            {
                                                Toast.makeText(OrdrerStatus.this, "Order was Updated !", Toast.LENGTH_SHORT).show();
                                            }else
                                            {
                                                Toast.makeText(OrdrerStatus.this, "Order was Updated but failed to send notification !", Toast.LENGTH_SHORT).show();

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());
                                        }
                                    });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}

