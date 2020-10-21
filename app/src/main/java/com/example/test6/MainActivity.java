package com.example.test6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.Key;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    EditText recNum;
    EditText secretKey;
    EditText msgContent;
    Button send;
    Button cancel;
    private final static int REQUEST_CODE_PERMISSION_SEND_SMS = 123;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recNum = (EditText) findViewById(R.id.recNum);
        secretKey = (EditText) findViewById(R.id.secretKey);
        msgContent = (EditText) findViewById(R.id.msgContent);
        send = (Button) findViewById(R.id.Send);
        cancel = (Button) findViewById(R.id.cancel);
        send.setEnabled(false);

        if(checkPermission(Manifest.permission.SEND_SMS))
        {
            send.setEnabled(true);
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    (Manifest.permission.SEND_SMS)
            },REQUEST_CODE_PERMISSION_SEND_SMS);
        }
        // finish the activity when click Cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        // encrypt the message and send when click Send button
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String recNumString = recNum.getText().toString();
                String secretKeyString = secretKey.getText().toString();
                String msgContentString = msgContent.getText().toString();


                // check for the validity of the user input
                // key length should be 16 characters as defined by AES-128-bit
                if (recNumString.length() > 0 && secretKeyString.length() > 0
                        && msgContentString.length() > 0
                        && secretKeyString.length() == 16) {
                    // encrypt the message
                    byte[] encryptedMsg = encryptSMS(secretKeyString, msgContentString);
                    // convert the byte array to hex format in order for
                    // transmission
                    String msgString = byte2hex(encryptedMsg);
                    // send the message through SMS
                    sendSMS(recNumString, msgString);
                    // finish
                    showAlertDialogNew();
                } else
                    Toast.makeText(getBaseContext(), "Please enter phone number, secret key and the message. Secret key must be 16 characters!",
                            Toast.LENGTH_SHORT).show();



            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.contect:
               Intent intent = new Intent(MainActivity.this,Contect.class);
               startActivity(intent);
               break;
            case R.id.message:
                Intent intent1 = new Intent(MainActivity.this,Message.class);
                startActivity(intent1);
                break;

        }

        return super.onOptionsItemSelected(item);

    }

    public static void sendSMS(String recNumString, String encryptedMsg) {
        try {
            // get a SmsManager
            SmsManager smsManager = SmsManager.getDefault();
            // Message may exceed 160 characters
            // need to divide the message into multiples
            ArrayList<String> parts = smsManager.divideMessage(encryptedMsg);
            smsManager.sendMultipartTextMessage(recNumString, null, parts,
                    null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // utility function



    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs += ("0" + stmp);
            else
                hs += stmp;

        }
        return hs.toUpperCase();
    }



    // encryption function
    public static byte[] encryptSMS(String secretKeyString, String msgContentString) {
        try {
            byte[] returnArray;
            // generate AES secret key from user input
            Key key = generateKey(secretKeyString);
            // specify the cipher algorithm using AES
            Cipher c = Cipher.getInstance("AES");
            // specify the encryption mode
            c.init(Cipher.ENCRYPT_MODE, key);
            // encrypt
            returnArray = c.doFinal(msgContentString.getBytes());
            return returnArray;
        } catch (Exception e) {
            e.printStackTrace();
            byte[] returnArray = null;
            return returnArray;
        }
    }
    private static Key generateKey(String secretKeyString) throws Exception {
        // generate secret key from string
        Key key = new SecretKeySpec(secretKeyString.getBytes(), "AES");
        return key;
    }
    public void onBackPressed(){
        showAlertDialog();
    }
    private void showAlertDialog(){
        //initialize the alert dialog

        final AlertDialog.Builder  builder  =  new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to Exit?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }


    private void showAlertDialogNew(){
        //initialize the alert dialog

        final AlertDialog.Builder  builder  =  new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Exit");
        builder.setMessage("Message is sent ." + "Are you sure you want to Exit?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private boolean checkPermission(String permission){
        int checkPermission = ContextCompat.checkSelfPermission(this,permission);
        return checkPermission == PackageManager.PERMISSION_GRANTED;
    }
}