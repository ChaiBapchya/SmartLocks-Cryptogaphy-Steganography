package com.androidapp.smartlock;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import lipermi.handler.CallHandler;
import lipermi.net.Client;

import static com.androidapp.smartlock.MainActivity.serverIP;


public class MainActivity extends Activity {
	private static int RESULT_LOAD_IMG = 1;
	public static String imgDecodableString;
	Button send;
	EditText editText;
	public static String message;
	public static ImageView img_view;
	File abc3;
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
	public static String serverIP = "192.168.43.29";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}


	public void loadImagefromGallery(View view) {
		// Create intent to Open Image applications like Gallery, Google Photos

		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// Start the Intent
		startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
	}


	public void sendPasswordinImage(View view)
	{   enableBlu();
		editText= (EditText) findViewById(R.id.passkey);
		message= editText.getText().toString();
		Log.d("here", "In send funct");
		Steganography steganography=new Steganography();
		String img_file_path =steganography.cryptography(message,img_view);
		send_file(img_file_path);
		try {
			Thread.sleep(0);
			new Conn().execute();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
    public void enableBlu(){
// enable device discovery - this will automatically enable Bluetooth
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                DISCOVER_DURATION );

        startActivityForResult(discoveryIntent, REQUEST_BLU);
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			// When an Image is picked
			if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
					&& null != data) {
				// Get the Image from data

				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Log.d("path",filePathColumn[0]);
				// Get the cursor
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				// Move to first row
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				imgDecodableString = cursor.getString(columnIndex);
				Log.d("path",imgDecodableString);

				cursor.close();
				ImageView imgView = (ImageView) findViewById(R.id.imgView);
				img_view=  imgView;
				// Set the Image in ImageView after decoding the String
				imgView.setImageBitmap(BitmapFactory
						.decodeFile(imgDecodableString));

			} else {
				Toast.makeText(this, "You haven't picked Image",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
					.show();
		}

	}

	public void send_file(String img_file_path){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            // Device does not support Bluetooth
            // Inform user that we're done.
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/png");
        File file = new File(img_file_path);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file) );
        //startActivity(intent);
//        PackageManager pm = getPackageManager();
//        List<ResolveInfo> appsList = pm.queryIntentActivities( intent, 0);
//        String packageName = null;
//        String className = null;
//        boolean found = false;
//        if(appsList.size() > 0){
//            for(ResolveInfo info: appsList) {
//                packageName = info.activityInfo.packageName;
//                if (packageName.equals("com.android.bluetooth")) {
//                    className = info.activityInfo.name;
//                    found = true;
//                    break;// found
//                }
//            }
//        }
//
//
//        intent.setClassName(packageName, className);
        startActivity(intent);
    }

}
class Conn extends AsyncTask<Void, Void, MainActivity> {

	@Override
	protected MainActivity doInBackground(Void... params) {
		Looper.prepare();
		try {
			CallHandler callHandler = new CallHandler();
			Client client = new Client(serverIP, 7777, callHandler);
			TestService testService = (TestService) client.getGlobal(TestService.class);
			String msg = testService.getResponse("qwe");
			//Toast.makeText(MainActivity.this, testService.getResponse("abc"), Toast.LENGTH_SHORT).show();
			Log.d("conf","conf insode:" );
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("conf","conf error:"+ e.toString());
		}
		Looper.loop();
		return null;
	}

}


