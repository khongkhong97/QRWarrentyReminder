package com.khong.qrwarrentyreminder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadItemActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    CameraSource cameraSource;
    TextView textView;
    BarcodeDetector barcodeDetector;
    Button buttonCheck;
    ListView listViewCheckItem;
    EditText editTextEnterUID;

    private boolean oneTimeDone = false;

    DatabaseReference databaseRetrieveItem;
    FirebaseUser user;
    String uid;
    String tempReadQr ="";

    List<RegisterItem> registerItemList;


    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_read_item);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        surfaceView = (SurfaceView) findViewById(R.id.cameraPreview);
        textView = (TextView) findViewById(R.id.textViewCaution);
        buttonCheck = (Button) findViewById(R.id.buttonCheck);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector).setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920, 1080).build();

        databaseRetrieveItem = FirebaseDatabase.getInstance().getReference("registeredItem").child(uid);

        listViewCheckItem = (ListView) findViewById(R.id.listViewCheck);
        editTextEnterUID = (EditText) findViewById(R.id.editTextEnterUID);

        registerItemList = new ArrayList<RegisterItem>();

        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    checkItem();
            }
        });

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }                try{
                    cameraSource.start(holder);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if(qrCodes.size() !=0){
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            tempReadQr = qrCodes.valueAt(0).displayValue;
                            textView.setText(tempReadQr);
                            checkItem();
                        }
                    });
                    cameraSource.stop();
                }
            }
        });
    }
    private void checkItem(){
            databaseRetrieveItem.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    registerItemList.clear();
                    for (DataSnapshot itemSnapShot : dataSnapshot.getChildren()) {
                        RegisterItem item = itemSnapShot.getValue(RegisterItem.class);
                        registerItemList.add(item);
                    }
                    CheckItemList adapter = new CheckItemList(ReadItemActivity.this, registerItemList);
                    listViewCheckItem.setAdapter(adapter);
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
