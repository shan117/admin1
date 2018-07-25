package com.example.shan.admin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {


    private RecyclerView recyclerView;

    List<Model> list;
    int j=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        list= new ArrayList();

        int i=0;


        recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        myRef.child("data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//                dataSnapshot.getChildren().iterator().

               /* while (dataSnapshot.getChildren().iterator().hasNext()){
                   Log.e("ListActivity","chiledren :" +dataSnapshot.getChildren().iterator().next()) ;
                }*/


                final long count=dataSnapshot.getChildrenCount();
                long count1=dataSnapshot.getChildrenCount();

                for(int i=0;i<count;i++){

                }
                try{
                    myRef.child("data").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            j++;

                            Log.e("","");
                            Model detailsModel= dataSnapshot.getValue(Model.class);
                            String barcodeValue= detailsModel.getBarcodeValue();
                            String barcodeValue1= detailsModel.getBarcodeValue();
                            String imagePath= detailsModel.getTime();
                            String imagePath1= detailsModel.getTime();

                            list.add(detailsModel);

                            if(j==count){
                                recyclerView.setAdapter(new MoviesAdapter(list,R.layout.list_item1,getApplicationContext()));
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                catch (Exception e){
                    Log.e("ListActivity" ," error :"+e.getMessage());
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







//        myRef.child("data").child( String.valueOf(System.currentTimeMillis())).setValue(pref.getAll());


    }
}
