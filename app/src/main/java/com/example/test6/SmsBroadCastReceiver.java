package com.example.test6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsBroadCastReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent){
        Bundle bundle = intent.getExtras();

        //specify the bundle to get the object based on SMS protocol "pdus"
        Object[] object = (Object[]) bundle.get("pdus");
        SmsMessage sms[] = new SmsMessage[object.length];

        Intent i = new Intent(context,DisplaySMSActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        String msgContent = " ";
        String originNum = " ";

        StringBuffer stringBuffer = new StringBuffer();

        for(int j=0;j<object.length;j++)
        {
            sms[j] = SmsMessage.createFromPdu((byte[]) object[j]);

            //get the received message content

            msgContent = sms[j].getDisplayMessageBody();

            //get the sender phone number

            originNum = sms[j].getDisplayOriginatingAddress();

            //aggrigate the messsage together when long message are fragmented

            stringBuffer.append(msgContent);

            //abort broadcast to cellphone inbox


            abortBroadcast();
            





        }

        //fill the sender's phone number into Intent

        i.putExtra("originNum", originNum);



        //fill the entire message body into Intent

        i.putExtra("msgContent", new String(stringBuffer));



        //start the DisplaySMSActivity.java

        context.startActivity(i);



    }

}
