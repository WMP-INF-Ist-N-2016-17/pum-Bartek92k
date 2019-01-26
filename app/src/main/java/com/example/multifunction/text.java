package com.example.multifunction;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;

public class text extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private final static int REQ_CAMERA = 1;
    private  ZXingScannerView scannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M)
        {

            if(checkPermission())
            {
                Toast.makeText(text.this,"Grandeed",Toast.LENGTH_LONG).show();
            }else
                {

            }
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(text.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this,new String[]{CAMERA},REQ_CAMERA);
    }
    public  void onRequestPermissionResault(int requestCode, String permission[],int grantResult[])
    {
        switch(requestCode)
        {
            case REQ_CAMERA:

                if(grantResult.length>0)
                {
                    boolean cameraAccepted = grantResult[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted)
                    {
                        Toast.makeText(text.this,"Granted",Toast.LENGTH_LONG).show();
                        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M)
                        {
                            if(shouldShowRequestPermissionRationale(CAMERA))
                            {
                                displayAllertMessage("Need to allow perrmitions", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                                            requestPermissions(new String[]{CAMERA}, REQ_CAMERA);
                                        }
                                    }
                                });
                            return;
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void  onResume()
    {
        super.onResume();

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                if(scannerView == null)
                {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }else{
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        scannerView.stopCamera();
    }
    public void displayAllertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(text.this).setMessage(message)
                .setPositiveButton("ok",listener)
                .setPositiveButton("cancel",null)
                .create()
                .show();
    }
    @Override
    public void handleResult(Result result) {
        final String scanResult = result.getText();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan result");
        builder.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(text.this);
            }
        });
        builder.setNegativeButton("Visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
                startActivity(intent);
            }
        });
        builder.setMessage(scanResult);
        AlertDialog alert = builder.create();
        alert.show();
    }


}
