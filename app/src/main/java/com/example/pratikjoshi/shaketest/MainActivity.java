package com.example.pratikjoshi.shaketest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ShakeEventManager.ShakeListener {

    Button btnreset;
    Button btncontacts;
    EmergContactDBHelper dbHelper;
    int REQUEST_SMS=1;
    int ctr=0;
    private ShakeEventManager sd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        dbHelper = ((ContactDB)getApplication()).dbHelper;

        sd = new ShakeEventManager();
        sd.setListener(this);
        sd.init(this);


        btnreset=(Button)findViewById(R.id.reset);
        btncontacts=(Button)findViewById(R.id.btncontacts);

        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS},REQUEST_SMS);

        btnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctr=0;
            }
        });
        btncontacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(MainActivity.this,"ok",Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(MainActivity.this,ContactsActivity.class);
               intent.putExtra("ok",0);
                startActivity(intent);
            }
        });


    }

    public void ConfirmDialogShow() {
        final int[] check = {0};
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("").setTitle("Confirm Emergency");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                PermissionCheck();
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                check[0] =1;
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                alertDialog.setMessage("Seconds remaining: " +(millisUntilFinished / 1000));
            }

            public void onFinish() {
                if(check[0] ==0) {
                    PermissionCheck();
                }
            }
        }.start();

        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        };

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 10000);
    }
    public void PermissionCheck(){
        String sms="Help!";
        String number=dbHelper.getNumber();
        if(number.length()>=10&&sms.length()>0){
            if(checkSelfPermission(Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED){
                sendMessage();
            }
            else{
                if(shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)){
                    Toast.makeText(MainActivity.this,"No permission!",Toast.LENGTH_SHORT).show();
                }

                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS},REQUEST_SMS);
            }
        }
        else
            Toast.makeText(getBaseContext(),"Enter number and sms!",Toast.LENGTH_LONG).show();

    }

    private void sendMessage() {
        String sms="Help!";
        //String number=txtPhoneNo.getText().toString();
        String number=dbHelper.getNumber();
        SmsManager smsManager=SmsManager.getDefault();
        smsManager.sendTextMessage(number,null,sms,null,null);
        Toast.makeText(this,"Done!",Toast.LENGTH_SHORT).show();
    }


    public void onRequestPermissionResult(int requestCode,String[] permission,int[] grantResults){
        if(requestCode==REQUEST_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendMessage();
            } else {
                Toast.makeText(this, "Sorry!", Toast.LENGTH_SHORT).show();
            }
        }else {
            super.onRequestPermissionsResult(requestCode,permission,grantResults);
        }
    }


    @Override
    public void onShake() {
        if(ctr==0) {
            if(dbHelper.checkCount()==0){
                Toast.makeText(MainActivity.this,"Please enter a contact",Toast.LENGTH_SHORT).show();

            }
            else {
                ConfirmDialogShow();

            }
            ctr = 1;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sd.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sd.deregister();
    }

}
