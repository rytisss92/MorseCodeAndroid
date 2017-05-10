package com.sicknote.morsecode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.Policy;

public class MainActivity extends AppCompatActivity {

    private TextView txt;
    private TextView result;
    private Button toMorseBtn;
    private Button toAlphaBtn;
    private Button flashLightOn;

    private Button btnDash;
    private Button btnDot;
    private Button btnSpace;
    private Button btnBack;
    private Button btnClear;
    private Button btnCopy;

    private boolean canUseFlash;
    private boolean deviceHasFlash;
    private boolean isFlashOn;
    private boolean morseFlashRunning;

    private static Camera camera;
    private Camera.Parameters parameters;

    private static final int MY_PERMISSIONS_FOR_CAMERA = 0;

    private Thread flashLightThread;
    private Thread getPermissionThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        txt = (TextView) findViewById(R.id.txt);
        result = (TextView) findViewById(R.id.result);
        toMorseBtn = (Button) findViewById(R.id.toMorseBtn);
        toAlphaBtn = (Button) findViewById(R.id.toAlphaBtn);
        flashLightOn = (Button) findViewById(R.id.flashLightOn);
        btnCopy = (Button) findViewById(R.id.in6);

        btnDot = (Button) findViewById(R.id.in1);
        btnDash = (Button) findViewById(R.id.in2);
        btnSpace = (Button) findViewById(R.id.in3);
        btnBack = (Button) findViewById(R.id.in4);
        btnClear = (Button) findViewById(R.id.in5);

        btnDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt.append(".");
            }
        });
        btnDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt.append("-");
            }
        });
        btnSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt.append(" ");
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = txt.getText().toString();
                if(txt.length() > 0){
                    string = string.substring(0, string.length()-1);
                    txt.setText(string);
                }
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt.setText("");
            }
        });


        toMorseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtToConvert = txt.getText().toString();
                String convertedTxt = MorseCode.alphaToMorse(txtToConvert);
                result.setText(convertedTxt);
            }
        });

        toAlphaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtToConvert = txt.getText().toString();
                String convertedTxt = MorseCode.morseToAlpha(txtToConvert);
                result.setText(convertedTxt);
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtToConvert = txt.getText().toString();
                result.setText(txtToConvert);
            }
        });

        deviceHasFlash = false;
        isFlashOn = false;
        canUseFlash = false;
        morseFlashRunning = false;

        flashLightOn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(canUseFlash)
                    System.out.println("Can use flash");
                if(morseFlashRunning)
                    System.out.println("MoresFlashRunning");

                if(canUseFlash){
                    //System.out.println("Clicked 1.");
                    if(!morseFlashRunning){
                        OutputToFlashLight();
                        if(!isFlashOn){
                            //turnOnTheFlash();
                        }else{
                            //turnOffTheFlash();
                        }
                    }
                    else{
                        //System.out.println("here 1.");
                        if(flashLightThread.isAlive()){
                            //System.out.println("here 2.");
                            flashLightThread.interrupt();
                            morseFlashRunning = false;
                        }

                    }

                }
                else{
                    //Toast.makeText(MainActivity.this, "Camera permissions must be enabled to use the flash.", Toast.LENGTH_LONG).show();
                    //System.out.println("Clicked 2.");
                    CanUseFlash();
                }

            }
        });
    }

    private void OutputToFlashLight(){
        morseFlashRunning = true;
        final String morseString = result.getText().toString();
        //System.out.println("String: " + morseString);
        //System.out.println("Loop Thread.");
        flashLightThread = new Thread() {
            public void run(){
                for(int i=0; i < morseString.length(); i++){
                    if(!this.isInterrupted()){
                        if(morseString.charAt(i) == '-'){
                            //System.out.println("DASH: " + morseString.charAt(i));
                            //On
                            turnOnTheFlash();
                            try {
                                Thread.sleep(600);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                this.interrupt();
                            }

                            //Off
                            turnOffTheFlash();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                this.interrupt();
                            }
                        }
                        if(morseString.charAt(i) == '.'){
                            //System.out.println("DOT: " + morseString.charAt(i));
                            //On
                            turnOnTheFlash();

                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                this.interrupt();
                            }

                            //Off
                            turnOffTheFlash();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                this.interrupt();
                            }
                        }
                        if(morseString.charAt(i) == ' '){
                            //System.out.println("SPACE: " + morseString.charAt(i));
                            //Off
                            turnOffTheFlash();
                            try {
                                Thread.sleep(350);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                this.interrupt();
                            }
                        }
                    }
                    else{
                        return;
                    }
                }
                morseFlashRunning = false;
            }
        };
        flashLightThread.start();
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;*/

            case R.id.action_more:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...

                // Stop flashlight thread if its active
                if(morseFlashRunning){
                    if(flashLightThread.isAlive()){
                        //System.out.println("here 3.");
                        flashLightThread.interrupt();
                        morseFlashRunning = false;
                    }
                }


                Intent intent = new Intent(this, MoreActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void CanUseFlash(){
        getPermissionThread = new Thread() {
            public void run() {
                //System.out.println("Started Thread.");
                deviceHasFlash = getApplication().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
                if(!deviceHasFlash){
                    Toast.makeText(MainActivity.this, "Sorry, you device does not have a camera.", Toast.LENGTH_LONG).show();
                }
                else {
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_FOR_CAMERA);
                    }
                    else{
                        //System.out.println("Has permission");
                        getCamera();
                    }
                }
            }
        };
        getPermissionThread.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_FOR_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //System.out.println("Got Permission.");
                    getCamera();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Toast.makeText(MainActivity.this, "Camera permission is not granted.", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void turnOffTheFlash() {
        //System.out.println("Turn Off 1.");
        if(this.camera != null) {
            //System.out.println("Turn Off 2.");
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            this.camera.setParameters(parameters);
            try {
                camera.setPreviewTexture(new SurfaceTexture(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.camera.stopPreview();
            isFlashOn = false;
        }
    }

    private void turnOnTheFlash() {
        if(this.camera != null){
            parameters = this.camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            this.camera.setParameters(parameters);
            try {
                camera.setPreviewTexture(new SurfaceTexture(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.camera.startPreview();
            isFlashOn = true;
        }
    }

    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                parameters = camera.getParameters();
                if(camera != null) {
                    canUseFlash = true;
                    OutputToFlashLight();
                }
            } catch (RuntimeException e) {
                System.out.println("Error: Failed to Open: " + e.getMessage());
            }
        }
    }

    private void getCameraNoFlash() {
        if (camera == null) {
            try {
                camera = Camera.open();
                parameters = camera.getParameters();
                if(camera != null) {
                    canUseFlash = true;
                }
            } catch (RuntimeException e) {
                System.out.println("Error: Failed to Open: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(this.camera != null){
            this.camera.release();
            this.camera = null;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(this.camera != null){
            turnOffTheFlash();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(deviceHasFlash){
            //turnOffTheFlash();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCameraNoFlash();
    }
}
