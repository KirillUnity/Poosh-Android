package me.appstockus.poosh_android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class SMSReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            final Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msg_from;

            if (bundle != null) {
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[ pdus.length ];

                    for(int i = 0; i < msgs.length; ++i){
                        msgs[i] = SmsMessage.createFromPdu(
                                (byte [])pdus[i]
                        );

                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                    }
                }
                catch(Exception e) {
                    Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }
}
