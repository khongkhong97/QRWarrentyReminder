package com.khong.qrwarrentyreminder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RegisterItemActivity extends AppCompatActivity {

    DatabaseReference databaseRetrieveItem;
    DatabaseReference databaseRegisterItem;

    String uid;
    FirebaseUser user;
    List<NewItem> itemList;
    List<RegisterItem> registeredItemList;

    Spinner spinnerName;
    Spinner spinnerColour;
    Spinner spinnerWarrentyPeriod;

    TextView textViewPurchaseDate;
    TextView textViewGenerateID;
    EditText editTextSerialNo;
    Button buttonRegisterItem;
    ImageView imageViewBarcodeGenerator;

    @Override
    protected void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_register_item);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();


        databaseRetrieveItem = FirebaseDatabase.getInstance().getReference("item").child(uid);
        databaseRegisterItem = FirebaseDatabase.getInstance().getReference("registeredItem").child(uid);


        spinnerName = (Spinner) findViewById(R.id.spinnerItemName);
        spinnerColour = (Spinner) findViewById(R.id.spinnerItemColor);
        spinnerWarrentyPeriod = (Spinner) findViewById(R.id.spinnerItemWarrentyPeriod);
        textViewPurchaseDate = (TextView) findViewById(R.id.textViewPurchaseDate);
        editTextSerialNo = (EditText) findViewById(R.id.editTextSerialNumber);
        buttonRegisterItem = (Button) findViewById(R.id.buttonRegisterItem);
        imageViewBarcodeGenerator = (ImageView) findViewById(R.id.imageViewBarcodeGenerate);
        textViewGenerateID = (TextView) findViewById(R.id.textViewIDGenerate);

        ArrayAdapter adapterColour = ArrayAdapter.createFromResource(this,R.array.colors,android.R.layout.simple_spinner_dropdown_item);
        adapterColour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColour.setAdapter(adapterColour);

        ArrayAdapter adapterWarrentyPeriod = ArrayAdapter.createFromResource(this,R.array.warrentyDuration,android.R.layout.simple_spinner_dropdown_item);
        adapterWarrentyPeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWarrentyPeriod.setAdapter(adapterWarrentyPeriod);

        itemList = new ArrayList<>();
        registeredItemList = new ArrayList<RegisterItem>();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd / MM / yyyy");
        String todayDate = mdformat.format(calendar.getTime());
        textViewPurchaseDate.setText(todayDate);

        buttonRegisterItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerItem();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        databaseRetrieveItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemList.clear();

                for(DataSnapshot itemSnapShot: dataSnapshot.getChildren()){
                    NewItem item = itemSnapShot.getValue(NewItem.class);
                    itemList.add(item);
                }
                ItemList adapter = new ItemList(RegisterItemActivity.this,itemList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerName.setAdapter(adapter);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseRegisterItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                registeredItemList.clear();
                for(DataSnapshot itemSnapShot: dataSnapshot.getChildren()){
                    RegisterItem registerItem = itemSnapShot.getValue(RegisterItem.class);
                    registeredItemList.add(registerItem);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void registerItem(){
        boolean checkAvailable = false;
        String itemName = spinnerName.getSelectedItem().toString();
        String serialNumber = editTextSerialNo.getText().toString().trim();
        String itemColour = spinnerColour.getSelectedItem().toString().trim();
        String warrentyPeriod = spinnerWarrentyPeriod.getSelectedItem().toString();
        String manufacturingDate = textViewPurchaseDate.getText().toString().trim();

        for(int i=0;i<registeredItemList.size();i++){
            if(serialNumber.equals(registeredItemList.get(i).getSerialNumber())){
                checkAvailable = true;
            }
        }
        if(!TextUtils.isEmpty(serialNumber) &&(checkAvailable==false)){
            String ID = databaseRegisterItem.push().getKey();

            RegisterItem registerItem = new RegisterItem(uid,itemName,serialNumber,itemColour,warrentyPeriod,manufacturingDate);

            databaseRegisterItem.child(ID).setValue(registerItem);

            textViewGenerateID.setText(ID);

            Toast.makeText(this,"Item register success",Toast.LENGTH_SHORT).show();

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try{
                BitMatrix bitMatrix = multiFormatWriter.encode(ID,BarcodeFormat.QR_CODE,400,400);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                imageViewBarcodeGenerator.setImageBitmap(bitmap);
            }
            catch(WriterException e){
                e.printStackTrace();
            }
        }
        else if(checkAvailable == true){
            Toast.makeText(this,"Duplicated serial no",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"You should enter a serial number",Toast.LENGTH_SHORT).show();
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
