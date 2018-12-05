package com.khong.qrwarrentyreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllRegisteredItemActivity extends AppCompatActivity {

    FirebaseUser user;
    String uid;

    DatabaseReference databaseRetrieveRegisteredItem;

    ListView listViewRegisteredItem;

    List<RegisterItem> registerItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_registered_item);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        databaseRetrieveRegisteredItem = FirebaseDatabase.getInstance().getReference("registeredItem").child(uid);

        listViewRegisteredItem = (ListView) findViewById(R.id.listViewAllRegisteredItem);

        registerItemList = new ArrayList<>();
    }
    @Override
    protected void onStart(){
        super.onStart();

        databaseRetrieveRegisteredItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                registerItemList.clear();
                for(DataSnapshot registeredItemSnapshot: dataSnapshot.getChildren()){
                    RegisterItem item = registeredItemSnapshot.getValue(RegisterItem.class);
                    registerItemList.add(item);
                }
                AllRegisteredItemList adapter = new AllRegisteredItemList(AllRegisteredItemActivity.this,registerItemList);
                listViewRegisteredItem.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuLogout:

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, MainActivity.class));

                break;
            case R.id.registerItem:
                finish();
                startActivity(new Intent(this,RegisterItemActivity.class));
                break;

            case R.id.newItem:
                finish();
                startActivity(new Intent(this,NewItemActivity.class));
                break;

            case R.id.readItem:
                finish();
                startActivity(new Intent(this,ReadItemActivity.class));
                break;
            case R.id.allRegisterItem:
                finish();
                startActivity(new Intent(this,AllRegisteredItemActivity.class));
                break;
        }

        return true;
    }
}
