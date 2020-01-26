package com.example.dell.microtechlab;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import de.nitri.gauge.Gauge;

import static com.example.dell.microtechlab.Shared.ACTION_USB_ATTACHED;
import static com.example.dell.microtechlab.Shared.ACTION_USB_DETACHED;
import static com.example.dell.microtechlab.Shared.Dc_ids;
import static com.example.dell.microtechlab.Shared.ENCODER_MAX_RPM;
import static com.example.dell.microtechlab.Shared.ENCODER_PULSES;
import static com.example.dell.microtechlab.Shared.HELP;
import static com.example.dell.microtechlab.Shared.Iac_ids;
import static com.example.dell.microtechlab.Shared.NAZIM;
import static com.example.dell.microtechlab.Shared.VENDOR_ID;
import static com.example.dell.microtechlab.Shared.Vac_ids;
import static com.example.dell.microtechlab.Shared.finalResults;
import static com.example.dell.microtechlab.Shared.manager;
import static com.example.dell.microtechlab.Shared.power_id;
import static com.example.dell.microtechlab.Shared.stmUsb;
import static com.example.dell.microtechlab.Shared.timeStamp;
import static com.example.dell.microtechlab.Shared.tp;
import static com.example.dell.microtechlab.Shared.units_ids;
import static com.example.dell.microtechlab.Shared.usbInterface;

public class MainActivity extends Activity {


    private boolean pressed=false,flashDiscTag=false;


    private final BroadcastReceiver UsbDetachReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            UsbDevice device;

            if (intent.getAction().toString() == ACTION_USB_DETACHED) {


                device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device.getVendorId() == VENDOR_ID) {
                    usbTag=false;
                    guiUpdate=false;

                }

            }
        }

    };

    public static TextView[] vac_text = new TextView[3];
    public static TextView[] iac_text = new TextView[3];
    public static TextView[] dc_text = new TextView[2];
    public static TextView[] power_text = new TextView[4];
    public static TextView[] units_text = new TextView[4];


    public static TextView speedText,speedSegment;
    private int count = 0;

    public static  long startprocess;
    public static android.os.Handler handler;
    public static Runnable runnable;

    long startTime = System.currentTimeMillis();

    private ImageButton save,scope,thd,encoder_config,help;
    public static Gauge g;

    public static boolean homeTag = true;
    public static boolean usbConnected = false;

    public static boolean sync=false,guiUpdate=false,usbTag=true;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();
        scope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this,ScopeActivity.class));
            }
        });

        thd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,HarmonicsActivity.class));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pressed=!pressed;

            }
        });


        power_text[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                secretClick(1);

            }
        });

        dc_text[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                secretClick(4);
            }
        });



        help.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                helpDialog();
               // write_toFile();

            }
        });



        handler=new android.os.Handler(Looper.getMainLooper());
        runnable=new Runnable() {
            @Override
            public void run() {

                try{

                    if(usbTag)usbTask();
                    if(guiUpdate && sync ){

                        DB.segmentDisplay(MainActivity.this);
                        sync=false;

                    }

                    if(pressed){
                        write_toFile();
                        if(flashDiscTag)Toast.makeText(MainActivity.this,"Saving Data to Flash Disc !",Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    Toast.makeText(MainActivity.this,""+e.toString(),Toast.LENGTH_SHORT).show();
                }
                handler.postDelayed(runnable,timeStamp);
            }
        };

        handler.post(runnable);


    }


    void usbTask(){

        sync=false;
        startprocess=System.currentTimeMillis();
        try {

            if(checkUsb()){

                MyUSB.usbCom(stmUsb,MainActivity.this);

            }

        } catch (Exception e) {
            Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public void initialize(){



        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        setContentView(R.layout.beta_layout);

        g=(Gauge)findViewById(R.id.gauge3);
        scope=(ImageButton)findViewById(R.id.scope_id);
        encoder_config=(ImageButton)findViewById(R.id.encoder_id);
        save=(ImageButton)findViewById(R.id.save_id);
        thd=(ImageButton)findViewById(R.id.thd_id);
        help=(ImageButton)findViewById(R.id.help_id);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tp= Typeface.createFromAsset(getAssets(),"7segment.ttf");
        speedText=(TextView)findViewById(R.id.speed_text_id);
        speedSegment=(TextView)findViewById(R.id.speed_segment);

        Shared.l1=(TextView) findViewById(R.id.callib_l1);
        Shared.l2=(TextView) findViewById(R.id.callib_l2);
        Shared.l3=(TextView) findViewById(R.id.callib_l3);


        speedSegment.setTypeface(tp);
        for(int i=0;i<3;i++){

            vac_text[i]=(TextView)findViewById(Vac_ids[i]);
            vac_text[i].setTypeface(tp);

            iac_text[i]=(TextView)findViewById(Iac_ids[i]);
            iac_text[i].setTypeface(tp);

            power_text[i]=(TextView)findViewById(power_id[i]);
            power_text[i].setTypeface(tp);

            units_text[i]=(TextView)findViewById(units_ids[i]);

            if(i<2){
                dc_text[i]=(TextView)findViewById(Dc_ids[i]);
                dc_text[i].setTypeface(tp);
            }
        }
        units_text[3]=(TextView)findViewById(units_ids[3]);
        power_text[3]=(TextView)findViewById(power_id[3]);
        power_text[3].setTypeface(tp);


        power_text[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                secretClick(1);
            }
        });

        encoder_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                encoderDialog();
            }
        });



        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION_USB_DETACHED);
        intentFilter.addAction(ACTION_USB_ATTACHED);
        registerReceiver(UsbDetachReceiver,intentFilter);


        usbTag=true;
        loadPreferences();



    }

    @Override
    protected void onResume() {
        super.onResume();

            guiUpdate=true;

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_USB_ATTACHED.equalsIgnoreCase(intent.getAction().toString())){

            Toast.makeText(MainActivity.this, "USB ATTACHED", Toast.LENGTH_SHORT).show();
            guiUpdate=true;
            usbTag=true;
        }
    }



    @Override
    protected void onRestart() {

        super.onRestart();
        guiUpdate=true;


    }

    @Override
    protected void onPause() {

        super.onPause();
        guiUpdate=false;
    }

    @Override
    protected void onStop() {

        super.onStop();
        guiUpdate=false;
    }

    @Override
    protected void onStart() {

        super.onStart();
        guiUpdate=true;


    }


    boolean checkUsb() throws FileNotFoundException {

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while ((deviceIterator.hasNext())) {
            UsbDevice device = deviceIterator.next();


                if(device.getVendorId()==VENDOR_ID){
                    usbInterface = MyUSB.findAdbInterface(device);
                    if (usbInterface != null){
                        stmUsb=device;
                        return true;
                    }
                }
            }

        return false;
    }


    public void write_toFile() {

        File file;
        try {


            File dir = new File("/mnt/usbhost1");
            dir.mkdir();

            file = new File(dir, "DATA.txt");
            String msg = "";
            if (finalResults != null) {
                for (int i = 0; i < finalResults.length; i++) {
                    msg += finalResults[i] + ",";
                }
                Calendar c = Calendar.getInstance();
                CharSequence s = DateFormat.format("yyyy-MM-dd hh:mm:ss", c.getTime());
                msg += s;
                msg+="\r\n";

            }

            BufferedWriter bw = null;
            FileWriter fw = null;

            try {

                // true = append file
                fw = new FileWriter(file.getAbsoluteFile(), true);
                bw = new BufferedWriter(fw);

                bw.write(msg);
                bw.newLine();


            } catch (IOException e) {

               //Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
               e.printStackTrace();
                flashDiscTag=false;

            } finally {

                try {

                    if (bw != null)
                        bw.close();

                    if (fw != null)
                        fw.close();

                    flashDiscTag=true;

                } catch (IOException ex) {

                    ex.printStackTrace();
                    Toast.makeText(MainActivity.this,"No Flash Disc detected !",Toast.LENGTH_SHORT).show();
                    flashDiscTag=false;

                }
            }

            }catch(Exception e){

            flashDiscTag=false;
          }
        if(pressed && flashDiscTag){
            save.setAlpha((float).5);

        }
        else if(!pressed && flashDiscTag) {
            save.setAlpha((float)1);
            save.setBackgroundColor(Color.TRANSPARENT);
            Toast.makeText(MainActivity.this,"Saving stoped !",Toast.LENGTH_SHORT).show();
        }
    }

    public void secretClick(int code){

        long difference=System.currentTimeMillis()-startTime;
        if(difference>1000)count=0;
        else count++;
        startTime=System.currentTimeMillis();
        if(count>=8) {

            homeTag=!homeTag;
            if(!homeTag){

                if(code==1){
                    Toast.makeText(MainActivity.this,"Flash Mode",Toast.LENGTH_LONG).show();
                    shareIt();
                }else {
                    Toast.makeText(MainActivity.this,NAZIM,Toast.LENGTH_LONG).show();
                }
            }
            count=0;
        }
    }





    void helpDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.help_dialog,null);
        builder.setView(dialogView);


        builder.setCancelable(true);
        TextView textView=(TextView)dialogView.findViewById(R.id.help_text);
        textView.setText(HELP);

        LinearLayout layout=(LinearLayout)dialogView.findViewById(R.id.help_layout);
        layout.getBackground().setAlpha(255);

        AlertDialog alertDialog=builder.create();
        alertDialog.show();


    }


    public void shareIt(){
        try {

            Intent i = getPackageManager().getLaunchIntentForPackage("com.lenovo.anyshare.gps");
            startActivity(i);

            }
            catch (Exception e) {

                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
    }

    public void encoderDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.encoder_dialog, null);
        builder.setView(dialogView);

        final EditText rpmEdit=(EditText)dialogView.findViewById(R.id.edit_rpm);
        final EditText encoderRatio=(EditText)dialogView.findViewById(R.id.edit_encoder);

        rpmEdit.setHint("Maximum RPM : "+ENCODER_MAX_RPM);
        encoderRatio.setHint("Encoder Pulses/Revolution : "+ENCODER_PULSES);
        builder.setCancelable(true);

        Button cancel=(Button)dialogView.findViewById(R.id.cancel);
        Button validate=(Button)dialogView.findViewById(R.id.validate);

        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                savePreferences();
                alertDialog.dismiss();

            }
        });
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    ENCODER_MAX_RPM=Integer.parseInt(rpmEdit.getText().toString());
                    ENCODER_PULSES=Integer.parseInt(encoderRatio.getText().toString());
                    Toast.makeText(MainActivity.this,"Configuration set to\nMax RPM : "+ENCODER_MAX_RPM+
                            "\nEncoder ratio : "+ENCODER_PULSES,Toast.LENGTH_SHORT).show();

                    g.setMaxValue(ENCODER_MAX_RPM/10);
                    g.setValuePerNick(ENCODER_MAX_RPM/1000);
                    savePreferences();
                    alertDialog.dismiss();

                }catch (Exception e){

                    Toast.makeText(MainActivity.this,"Wrong input format",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    void savePreferences(){

        @SuppressLint("WrongConstant") SharedPreferences save = getSharedPreferences("save",400);
        SharedPreferences.Editor editor = save.edit();
        editor.putInt("rpm",ENCODER_MAX_RPM);
        editor.putInt("pulseRate",ENCODER_PULSES);
        editor.commit();

    }

    public void loadPreferences(){

        SharedPreferences load = getSharedPreferences("save",0);

        ENCODER_MAX_RPM = load.getInt("rpm",ENCODER_MAX_RPM);
        ENCODER_PULSES = load.getInt("pulseRate",ENCODER_PULSES);


    }





}
