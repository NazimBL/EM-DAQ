package com.example.dell.microtechlab;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.LauncherActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.Image;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {

    public static TextView[] vac_text=new TextView[3];
    public static TextView[] iac_text=new TextView[3];
    public static TextView[] dc_text=new TextView[2];
    public static TextView[] power_text=new TextView[3];
    public static TextView speedText;
    StartMyServiceAtBootReceiver receiver;
    int count=0;
    public static Typeface tp;
    public static final String ACTION_USB_ATTACHED  = "android.hardware.usb.action.USB_DEVICE_ATTACHED";

    public static UsbManager manager;
    public static UsbInterface usbInterface;
    public static UsbEndpoint outEnd,inEnd;
    UsbDevice stmUsb;

    long startTime=System.currentTimeMillis();
    HomeBroadcastReceiver homeB=new HomeBroadcastReceiver();
    IntentFilter intentFilter=new IntentFilter();

    public static byte[] bytes;
    public static byte[] received=new byte[64];
    public static short[] vl1=new short[32];
    public static short[] vl2=new short[32];
    public static short[] vl3=new short[32];
    public static short[] il1=new short[32];
    public static short[] il2=new short[32];
    public static short[] il3=new short[32];
    public static short[] vdc1=new short[32];
    public static short[] vdc2=new short[32];
    public static short[] idc=new short[32];


    public static double finalResults[]=new double[9];

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mControlsView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {

            mControlsView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private CustomGauge gauge3;
    public static boolean homeTag=true;

    long timeStamp=200;//in millis
    Timer autoupdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();
        //MainActivity.speedText.setTextSize(10);
        //MainActivity.power_text[0].setTextSize(15);

        IntentFilter inf=new IntentFilter(Intent.ACTION_MAIN);
        registerReceiver(homeB,inf);

        mControlsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });
        power_text[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 //   usbTask();
                  ref(timeStamp);


            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        hide();
        try {
           ref(timeStamp);
        }catch (Exception e){
            Log.d("Nazim","Timer exception"+e.toString());
        }

    }
    void usbTask(){
        try {


            if(checkUsb()){

                MyUSB.usbCom(stmUsb,MainActivity.this);
                if(vl1 != null) {

                   // for(int i=0;i<32;i++)Toast.makeText(MainActivity.this,"measure "+vl1[0],Toast.LENGTH_SHORT).show();
                    DB.segmentDisplay(MainActivity.this);
                    // write_toFile();
                }


            }//else Toast.makeText(MainActivity.this,"no USB connected ",Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void initialize(){

        IntentFilter intentF=new IntentFilter();
        intentF.addAction(Intent.ACTION_BOOT_COMPLETED);
        intentF.addAction(ACTION_USB_ATTACHED);


        setContentView(R.layout.beta_layout);
        mControlsView = findViewById(R.id.fullscreen_content);

        gauge3=(CustomGauge)findViewById(R.id.gauge3);
        gauge3.setValue(100);

        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.GONE);

        hide();

        receiver=new StartMyServiceAtBootReceiver();
        registerReceiver(receiver,intentFilter);
        tp= Typeface.createFromAsset(getAssets(),"7segment.ttf");
        speedText=(TextView)findViewById(R.id.speed_text_id);
        for(int i=0;i<3;i++){
            vac_text[i]=(TextView)findViewById(DB.Vac_ids[i]);
            vac_text[i].setTypeface(tp);

            iac_text[i]=(TextView)findViewById(DB.Iac_ids[i]);
            iac_text[i].setTypeface(tp);
            power_text[i]=(TextView)findViewById(DB.power_id[i]);
            power_text[i].setTypeface(tp);

            if(i<2){
                dc_text[i]=(TextView)findViewById(DB.Dc_ids[i]);
                dc_text[i].setTypeface(tp);
            }
        }
        power_text[2].setSoundEffectsEnabled(false);
        power_text[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //test usb write
                //for(int i=0;i<finalResults.length;i++)finalResults[i]=45;
                //write_toFile();
                secretClick();


            }
        });


        final View decorView = getWindow().getDecorView();

        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            // TODO: The system bars are visible. Make any desired
                            // adjustments to your UI, such as showing the action bar or
                            // other navigational controls.
                            //Toast.makeText(MainActivity.this,"FLAG 0",Toast.LENGTH_SHORT).show();
                            hide();
                        } else {
                            // TODO: The system bars are NOT visible. Make any desired
                            // adjustments to your UI, such as hiding the action bar or
                            // other navigational controls.
                           // Toast.makeText(MainActivity.this,"FLAG 1",Toast.LENGTH_SHORT).show();


                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
          ref(timeStamp);
        }catch (Exception e){
            Log.d("Nazim","Timer exception"+e.toString());
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        autoupdate.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
       autoupdate.cancel();
    }


    boolean checkUsb() throws FileNotFoundException {
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while ((deviceIterator.hasNext())) {
            UsbDevice device = deviceIterator.next();
            //Toast.makeText(MainActivity.this,"product id : "+device.getProductId(),Toast.LENGTH_LONG).show();
            //Toast.makeText(MainActivity.this,"vendor id : "+device.getVendorId(),Toast.LENGTH_LONG).show();

            if(device.getDeviceName()!=null) {
                ///Uncomment this
                if(device.getVendorId()==4660)
                usbInterface = MyUSB.findAdbInterface(device,MainActivity.this);

                if (usbInterface != null){
                    stmUsb=device;
                    return true;
                   // Toast.makeText(MainActivity.this,"usb interface : "+usbInterface.toString(),Toast.LENGTH_LONG).show();

                }
            }
        }
        return false;
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.measure_id){

          secretClick();
        }
        else if(id==android.R.id.home)Toast.makeText(MainActivity.this,"HOME",Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }


    public void write_toFile() {

        File file;

        try {

            File dir = new File("/mnt/usbhost1");
            dir.mkdir();

            file = new File(dir, "text.txt");
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

                e.printStackTrace();

            } finally {

                try {

                    if (bw != null)
                        bw.close();

                    if (fw != null)
                        fw.close();

                } catch (IOException ex) {

                    ex.printStackTrace();

                }
            }

            }catch(Exception e){
                Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();

          }
    }

    public void secretClick(){

        long difference=System.currentTimeMillis()-startTime;
        if(difference>1000)count=0;
        else count++;
        startTime=System.currentTimeMillis();
        if(count>=5) {

            homeTag=!homeTag;
            if(!homeTag){
                Toast.makeText(MainActivity.this,"Start on Boot Desactivated",Toast.LENGTH_SHORT).show();
                autoupdate.cancel();
                //shareIt();

            }
            else {
                Toast.makeText(MainActivity.this,"Start on Boot Activated",Toast.LENGTH_SHORT).show();
                ref(timeStamp);
            }

            count=0;
        }
    }

    @Override
    public void onBackPressed() {

        if(!homeTag) {
            try {
                Log.d("Nazim", getIntent().getCategories().toString());

                if (getIntent().hasCategory(Intent.CATEGORY_HOME))
                    getIntent().removeCategory(Intent.CATEGORY_HOME);


            } catch (Exception e) {
                Log.d("Nazim", e.toString());
            }
            finish();
        }
    }
    private void hide() {

        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    public void ref(final long del){
        autoupdate = new Timer();
        autoupdate.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            usbTask();

                        } catch (Exception e) {
                            Log.d("Nazim","Main error"+e.toString());
                        }
                    }
                });
            }
        }, 0, del);
    }
    public void shareIt(){
        try {

           // Intent i = getPackageManager().getLaunchIntentForPackage("com.example.dell.serial");
            Intent i = getPackageManager().getLaunchIntentForPackage("SHAREit");
            startActivity(i);

            }
            catch (Exception e) {

                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
    }

}
