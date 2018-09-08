package com.example.shan.admin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;

    private User user;

    private Toolbar toolbar;

    private AppCompatEditText etDate;
    private AppCompatEditText etMembers;

    String[] membersList={"Member1","Member2","Member3"};

    List<Model> list;
    int j=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        SharedPreferences pref = getSharedPreferences("userDetail", 0);
        Gson gson = new Gson();
        String json = pref.getString("user", "");
        user = gson.fromJson(json, User.class);

        toolbar=(Toolbar) findViewById(R.id.toolbar);

        etDate=(AppCompatEditText) findViewById(R.id.et_date);
        etMembers=(AppCompatEditText) findViewById(R.id.et_member);

        recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                onBackPressed();
            }
        });
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_back_white));

        etMembers.setOnClickListener(this);
        etDate.setOnClickListener(this);

        list= new ArrayList();

        int i=0;

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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.et_date:
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                etDate.setText(dayOfMonth + " / " + (monthOfYear + 1) + " / " + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                break;
            case R.id.et_member:

                final Dialog dialog = new Dialog(ListActivity.this);
                dialog.setContentView(R.layout.dialog_members);
                dialog.setTitle("Custom Dialog");

                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);

                ListView lv_time = (ListView) dialog.findViewById(R.id.lv_members);
                ArrayAdapter<String> adapter
                        = new ArrayAdapter<String>(this,
                        R.layout.list_item_members, membersList);
                lv_time.setAdapter(adapter);
                lv_time.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        etMembers.setText(membersList[position]);
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
        }
    }
}
