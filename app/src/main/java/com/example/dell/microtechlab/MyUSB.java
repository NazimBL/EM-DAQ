package com.example.dell.microtechlab;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;

import static com.example.dell.microtechlab.MainActivity.bytes;
import static com.example.dell.microtechlab.MainActivity.inEnd;
import static com.example.dell.microtechlab.MainActivity.outEnd;
import static com.example.dell.microtechlab.MainActivity.received;
import static com.example.dell.microtechlab.MainActivity.usbInterface;
import static com.example.dell.microtechlab.MainActivity.vl1;

/**
 * Created by DELL on 15/02/2018.
 */

public class MyUSB {


    // Toast.makeText(MainActivity.this,"productId :"+device.getProductId(),Toast.LENGTH_SHORT).show();
    // Toast.makeText(MainActivity.this,"vendor id :"+device.getVendorId(),Toast.LENGTH_SHORT).show();
    public static byte[] first=new byte[64];
    public static byte[] second=new byte[64];
    public static byte[] third=new byte[64];
    public static byte[] four=new byte[64];
    public static byte[] five=new byte[64];
    public static byte[] six=new byte[64];
    public static byte[] seven=new byte[64];
    public static byte[] eight=new byte[64];
    public static byte[] nine=new byte[64];


    public static UsbInterface findAdbInterface(UsbDevice device, Context context) {

        int count = device.getInterfaceCount();
       // Toast.makeText(context,"count : "+count,Toast.LENGTH_SHORT).show();
        for (int i = 0; i < count; i++) {
       //     Toast.makeText(context,"interface loop : "+i,Toast.LENGTH_SHORT).show();
            UsbInterface intf = device.getInterface(i);
            return intf;

        }
        return null;
    }

    public static void manageEndpoints(Context contenxt){

        outEnd=null;
        inEnd=null;

        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint ep = usbInterface.getEndpoint(i);

            if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                outEnd = ep;
               // Toast.makeText(contenxt,"out end point get direction  : "+MainActivity.outEnd.getDirection(),Toast.LENGTH_LONG).show();
            } else {
                inEnd = ep;
               // Toast.makeText(contenxt,"in end point get direction  : "+inEnd.getDirection(),Toast.LENGTH_LONG).show();
            }

        }

    }
    public static  void usbCom(UsbDevice device,Context context){

        manageEndpoints(context);


        if(outEnd!=null && device!=null && usbInterface!=null) {

            PendingIntent pendingIntent=PendingIntent.getBroadcast(context,0,
                    new Intent(UsbManager.EXTRA_PERMISSION_GRANTED),0);
            MainActivity.manager.requestPermission(device,pendingIntent);
            UsbDeviceConnection connection=null;
            if(MainActivity.manager.hasPermission(device)) connection=MainActivity.manager.openDevice(device);

            try {
                if (connection != null) {
                    if (connection.claimInterface(usbInterface,true)) {

                        bytes ="OK".getBytes();
                        try {

                            int transfer = connection.bulkTransfer(outEnd,bytes, bytes.length,0);
                            receiveBruteForce(connection,context);
                            //if reception=2 read encoder value
                            //int reception1=connection.bulkTransfer(inEnd,first,first.length,0);
                            //int reception2=connection.bulkTransfer(inEnd,second,second.length,0);

                            vl1= convertBigEndian(first,context);
                            MainActivity.vl2=convertBigEndian(second,context);
                            MainActivity.vl3=convertBigEndian(third,context);
                            MainActivity.il1=convertBigEndian(four,context);
                            MainActivity.il2=convertBigEndian(five,context);
                            MainActivity.il3=convertBigEndian(six,context);
                            MainActivity.vdc1=convertBigEndian(seven,context);
                            MainActivity.vdc2=convertBigEndian(eight,context);
                            MainActivity.idc=convertBigEndian(nine,context);



                        }catch (Exception e){
                            Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();

                        }

                    } else Toast.makeText(context,"couldnt claim interface",Toast.LENGTH_SHORT).show();

                } else {
                    connection.close();
                    Toast.makeText(context,"couldnt open connection  ",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();

            }

        }else  Toast.makeText(context,"no end point found",Toast.LENGTH_SHORT).show();


    }

    public static short[] convertBigEndian(byte[] reception,Context c){
        short[] measures=new short[32];

        byte[] rec=new byte[2];
        int j=0;

        for(int i=0;i<64;i++){

            rec[0]=reception[i];
            rec[1]=reception[i+1];

            //Toast.makeText(c,"high "+(int)(rec[0]&0xff)+" low "+(int)(rec[1]&0xff),Toast.LENGTH_SHORT).show();
            ByteBuffer wrapped = ByteBuffer.wrap(rec);
            measures[j] = wrapped.getShort();
            //Toast.makeText(c,"measure : "+measures[j],Toast.LENGTH_SHORT).show();
            j++;
            i++;
        }
        for(int i=0;i<32;i++) measures[i]=(short)(measures[i]-2048);


        return measures;
    }


    public static void receiveBruteForce(UsbDeviceConnection connection, Context context){

        if(connection==null)return;
        int reception1=connection.bulkTransfer(inEnd,first,first.length,0);
        int reception2=connection.bulkTransfer(inEnd,second,second.length,0);
        int reception3=connection.bulkTransfer(inEnd,third,third.length,0);
        int reception4=connection.bulkTransfer(inEnd,four,four.length,0);
        int reception5=connection.bulkTransfer(inEnd,five,five.length,0);
        int reception6=connection.bulkTransfer(inEnd,six,six.length,0);
        int reception7=connection.bulkTransfer(inEnd,seven,seven.length,0);
        int reception8=connection.bulkTransfer(inEnd,eight,eight.length,0);
        int reception9=connection.bulkTransfer(inEnd,nine,nine.length,0);

    }

}
