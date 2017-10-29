package com.example.anirudh.driverapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by anirudh on 21/1/17.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder> {

    FirebaseDatabase mDataBase;
    ArrayList<Requests> reqList;
    Context context;
    DatabaseReference mrefReq;
    DatabaseReference mrefCustomer;
public RecyclerAdapter(ArrayList<Requests> reqList,Context context){

    this.reqList=reqList;
    this.context=context;
    mDataBase=FirebaseDatabase.getInstance();
    mrefReq =mDataBase.getReference().child("Driver").child("requests");
    mrefCustomer =mDataBase.getReference().child("Customer");

}
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        holder.name.setText(reqList.get(position).name);
        if(!reqList.get(position).assigned.equals("No")){
            holder.choose.setEnabled(false);
            holder.choose.setText("Assigned");
        }
        else {
            holder.choose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.choose.setText("Assigned");
                    holder.choose.setEnabled(false);
                    mrefReq.child(reqList.get(position).pushID).child("assigned")
                            .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    mrefCustomer.child(reqList.get(position).customerUID)
                            .child("DriverUID")
                            .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());


                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return reqList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        TextView name;
        Button choose;
        public MyHolder(View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.name);
            choose=(Button)itemView.findViewById(R.id.btnChoose);
        }
    }
}
