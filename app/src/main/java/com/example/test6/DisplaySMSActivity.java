package com.example.test6;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DisplaySMSActivity extends AppCompatActivity {

    EditText secretKey;

    TextView senderNum;

    TextView encryptedMsg;

    TextView decryptedMsg;

    Button submit;

    Button cancel;

    String originNum = "";

    String msgContent = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_s_m_s);


        senderNum = (TextView) findViewById(R.id.senderNum);

        encryptedMsg = (TextView) findViewById(R.id.encryptedMsg);

        decryptedMsg = (TextView) findViewById(R.id.decryptedMsg);

        secretKey = (EditText) findViewById(R.id.secretKey);

        submit = (Button) findViewById(R.id.submit);

        cancel = (Button) findViewById(R.id.cancel);



        // get the Intent extra

        Bundle extras = getIntent().getExtras();

        if (extras != null) {



            // get the sender phone number from extra

            originNum = extras.getString("originNum");



            // get the encrypted message body from extra

            msgContent = extras.getString("msgContent");



            // set the text fields in the UI

            senderNum.setText(originNum);

            encryptedMsg.setText(msgContent);

        } else {



            // if the Intent is null, there should be something wrong

            Toast.makeText(getBaseContext(), "Error Occurs!",

                    Toast.LENGTH_SHORT).show();

           finish();

        }



        // when click on the cancel button, return

        cancel.setOnClickListener(new View.OnClickListener() {



            public void onClick(View v) {

                showAlertDialog();



            }

        });



        // when click on the submit button decrypt the message body

        submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {



                // user input the AES secret key

                String secretKeyString = secretKey.getText().toString();



                //key length should be 16 characters as defined by AES-128-bit

                if (secretKeyString.length() > 0

                        && secretKeyString.length() == 16) {

                    try {



                        // convert the encrypted String message body to a byte

                        // array

                        byte[] msg = hex2byte(msgContent.getBytes());



                        // decrypt the byte array

                        byte[] result = decryptSMS(secretKey.getText()

                                .toString(), msg);



                        // set the text view for the decrypted message

                        decryptedMsg.setText(new String(result));



                    } catch (Exception e) {



                        // in the case of message corrupted or invalid key

                        // decryption cannot be carried out

                        decryptedMsg.setText("Message Cannot Be Decrypted!");

                    }



                } else

                    Toast.makeText(DisplaySMSActivity.this, "Please enter the 16 character password...", Toast.LENGTH_SHORT).show();

            }

        });



    }



    // utility function: convert hex array to byte array

    public static byte[] hex2byte(byte[] b) {

        if ((b.length % 2) != 0)

            throw new IllegalArgumentException("hello");



        byte[] b2 = new byte[b.length / 2];



        for (int n = 0; n < b.length; n += 2) {

            String item = new String(b, n, 2);

            b2[n / 2] = (byte) Integer.parseInt(item, 16);

        }

        return b2;

    }



    // decryption function

    public static byte[] decryptSMS(String secretKeyString, byte[] encryptedMsg)

            throws Exception {



        // generate AES secret key from the user input secret key

        Key key = generateKey(secretKeyString);



        // get the cipher algorithm for AES

        Cipher c = Cipher.getInstance("AES");



        // specify the decryption mode

        c.init(Cipher.DECRYPT_MODE, key);



        // decrypt the message

        byte[] decValue = c.doFinal(encryptedMsg);



        return decValue;

    }



    private static Key generateKey(String secretKeyString) throws Exception {



        // generate AES secret key from a String

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
}