package com.example.shan.admin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.ListView;
import android.widget.TextView;

import com.example.shan.admin.misc.CustomProgressDialog;
import com.example.shan.admin.misc.Helper;
import com.example.shan.admin.pojo.Scan;
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
import java.util.Set;
import java.util.TreeSet;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;

    private User user;

    private ScanListAdapter listAdapter;

    private Toolbar toolbar;

    private AppCompatEditText etDate;
    private AppCompatEditText etMembers;

    private TextView tvNoData;

    private String strMember="All";
    private String strDate="All";

    List<Scan> originalList;

    Set<String> userSet;

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

        tvNoData=(TextView) findViewById(R.id.tv_no_data);

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

        originalList = new ArrayList();

        int i=0;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference().child("data");


        final ProgressDialog pd= CustomProgressDialog.ctor(ListActivity.this);
        pd.show();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userSet=new TreeSet<>();
                userSet.add("All");
                final long count=dataSnapshot.getChildrenCount();
                try{
                    myRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            j++;
                            Scan scan= dataSnapshot.getValue(Scan.class);
                            Log.e("ListActivity","scan : " + scan);
                            if(user.getRole().contentEquals("superAdmin"))
                            {
                                if(scan.getSuperAdmin().contentEquals(Helper.stringToBase64(user.getEmail())))
                                {
                                    originalList.add(scan);
                                    userSet.add(Helper.base64ToString(scan.getSuperUser()));
                                }
                            }else  if(user.getRole().contentEquals("superUser"))
                            {
                                if(scan.getSuperUser().contentEquals(Helper.stringToBase64(user.getEmail())))
                                {
                                    originalList.add(scan);
                                    userSet.add(Helper.base64ToString(scan.getUser()));
                                }
                            } else  if(user.getRole().contentEquals("user"))
                            {
                                if(scan.getUser().contentEquals(Helper.stringToBase64(user.getEmail())))
                                {
                                    originalList.add(scan);
                                    userSet.add(Helper.base64ToString(scan.getSupervisor()));
                                }
                            }
                            if(j==count){
                                listAdapter= new ScanListAdapter(originalList,R.layout.list_item1,getApplicationContext());
                                recyclerView.setAdapter(listAdapter);
                                if(originalList.size()==0||originalList.isEmpty())
                                {
                                    tvNoData.setVisibility(View.VISIBLE);
                                }
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
                pd.dismiss();
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

                                strDate=null;
                                if(dayOfMonth<10)
                                {
                                    strDate="0"+dayOfMonth + "/";
                                }else {
                                    strDate=dayOfMonth + "/";
                                }
                                if(monthOfYear<9)
                                {
                                    strDate=strDate+"0"+(monthOfYear + 1) + "/" + year;
                                }else{
                                    strDate=strDate+(monthOfYear + 1) + "/" + year;
                                }
                                etDate.setText(strDate);
                                filterList(strMember,strDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                break;
            case R.id.et_member:

                final Dialog dialog = new Dialog(ListActivity.this);
                dialog.setContentView(R.layout.dialog_members);
                dialog.setTitle("Custom Dialog");

                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);

                ListView lv_time = (ListView) dialog.findViewById(R.id.lv_members);
                final List<String>  userList=new ArrayList<String>(userSet);
                final ArrayAdapter<String> adapter
                        = new ArrayAdapter<String>(this,
                        R.layout.list_item_members, userList);
                lv_time.setAdapter(adapter);
                lv_time.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        etMembers.setText(userList.get(position));
                        strMember=userList.get(position);
                        filterList(strMember,strDate);
                        dialog.dismiss();

                    }
                });
                dialog.show();
                break;
        }
    }

    public void filterList(String member, String date)
    {
        Log.e("ListActivity","Date Filter value : "+date);
        Log.e("ListActivity","Member Filter value : "+member);
        List<Scan> filteredList= new ArrayList<>();
        if(member.contentEquals("All"))
        {
            if(date.contentEquals("All")) {
                filteredList = originalList;
            }else{
                for (int i = 0; i < originalList.size(); i++) {
                    if( originalList.get(i).getTime().substring(0,10).contentEquals(strDate)){
                        Log.e("ListActivity","Filter Date : "+originalList.get(i));
                        filteredList.add(originalList.get(i));
                    }
                }
            }
        }else {
            for (int i = 0; i < originalList.size(); i++) {
                Log.e("ListActivity","Scan : "+originalList.get(i));
                if (user.getRole().contentEquals("superAdmin")) {
                    if (originalList.get(i).getSuperUser().contentEquals(Helper.stringToBase64(member)) &&
                            (date.contentEquals("All") || originalList.get(i).getTime().substring(0,10).contentEquals(strDate))) {
                        Log.e("ListActivity","Filter : "+originalList.get(i));
                        filteredList.add(originalList.get(i));
                    }
                } else if (user.getRole().contentEquals("superUser")) {
                    if (originalList.get(i).getUser().contentEquals(Helper.stringToBase64(member)) &&
                            (date.contentEquals("All") || originalList.get(i).getTime().substring(0,10).contentEquals(strDate))) {
                        Log.e("ListActivity","Filter : "+originalList.get(i));
                        filteredList.add(originalList.get(i));
                    }
                } else if (user.getRole().contentEquals("user")) {
                    if (originalList.get(i).getSupervisor().contentEquals(Helper.stringToBase64(member)) &&
                            (date.contentEquals("All") || originalList.get(i).getTime().substring(0,10).contentEquals(strDate))) {
                        Log.e("ListActivity","Filter : "+originalList.get(i));
                        filteredList.add(originalList.get(i));
                    }
                }
            }
        }
        tvNoData.setVisibility(View.GONE);
        listAdapter = new ScanListAdapter(filteredList, R.layout.list_item1, getApplicationContext());
        recyclerView.setAdapter(listAdapter);
        if(filteredList.size()==0||filteredList.isEmpty())
        {
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

}
