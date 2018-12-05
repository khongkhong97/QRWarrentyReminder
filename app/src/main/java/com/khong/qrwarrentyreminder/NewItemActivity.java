package com.khong.qrwarrentyreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewItemActivity extends AppCompatActivity{

    EditText editTextItemRegister;
    Button buttonNewItem;

    FirebaseUser user;
    String uid;

    DatabaseReference databaseNewItem;

    ListView listViewItem;

    List<NewItem> itemList;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        databaseNewItem = FirebaseDatabase.getInstance().getReference("item").child(uid);

        editTextItemRegister = (EditText) findViewById(R.id.editTextRegisterName);
        buttonNewItem = (Button) findViewById(R.id.buttonNewItem);

        listViewItem = (ListView)findViewById(R.id.listViewItem);

        itemList = new ArrayList<>();
        buttonNewItem.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View view){
                newItem();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseNewItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemList.clear();

                for(DataSnapshot itemSnapShot: dataSnapshot.getChildren()){
                    NewItem item = itemSnapShot.getValue(NewItem.class);

                    itemList.add(item);
                }

                ItemList adapter = new ItemList(NewItemActivity.this, itemList);
                listViewItem.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void newItem(){
        boolean checkAvailable = false;
        String name = editTextItemRegister.getText().toString().trim();
        for(int i=0;i<itemList.size();i++) {
            if(name.equals(itemList.get(i).getItemDescription())){
                checkAvailable = true;
            }
        }
        if(!TextUtils.isEmpty(name) && (checkAvailable == false)){
            String ID = databaseNewItem.push().getKey();

            NewItem registerItem = new NewItem(uid,name);

            databaseNewItem.child(ID).setValue(registerItem);
            Toast.makeText(this,"Item register success",Toast.LENGTH_SHORT).show();
        }
        else if(checkAvailable == true){
            Toast.makeText(this,"Duplicated item",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"You should enter a name",Toast.LENGTH_SHORT).show();
        }
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

