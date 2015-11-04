package mahmoodh.scaleit;

import mahmoodh.scaleit.util.SystemUiHider;


import android.app.Activity;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import android.view.View;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ImageCapture extends Activity {

    private static int TAKE_PICTURE = 1;
    private static int EDIT_PICTURE = 2;
    private Uri imageUri;
    ImageView imageView;
    static private final String TAG = "ImageCapture class :";
    static private final String  CAM_IMAGE_URI_KEY = "cameraImageUri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_capture);

        findViewById(R.id.takePicBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(v);
            }
        });
        findViewById(R.id.editPicBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPic(v);
            }
        });
        imageView = (ImageView) findViewById(R.id.ImageView);
    }

    /*
    * method for loading image
    */
    public void takePhoto(View view) {
        Log.i(TAG, "in takePhoto");

        try {
            // Create AndroidExampleFolder at sdcard
            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AndroidExampleFolder");

            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }

            // Create camera captured image file path and name
            File file = new File(imageStorageDir + File.separator + "IMG_"+ String.valueOf(System.currentTimeMillis()) + ".jpg");
            Log.i(TAG,"file : "+file);
            imageUri = Uri.fromFile(file);
            Log.i(TAG,"imageUri : "+imageUri);
            // Camera capture image intent
            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");

            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Parcelable[]{captureIntent});

            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, TAKE_PICTURE);

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Exception:" + e,
                    Toast.LENGTH_LONG).show();
        }

    }

    /*
    * method to start another activity where we edit the image (draw lines for measurement)
    */
    public void editPic(View v) {
        try {
            Intent editIntent = new Intent(ImageCapture.this, ImageEditor.class);
            if (imageUri != null) {
                editIntent.putExtra("imageUri", imageUri.toString());
                startActivityForResult(editIntent, EDIT_PICTURE);
            } else {
                Toast.makeText(this, "Take image first", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                    .show();
            Log.e("Camera", e.toString());
        }

    }

    /*
    * On returning from camera/take_picture activity
    * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
        super.onActivityResult(requestCode, resultCode, returnIntent);
        Log.i(TAG, "in onActivityResult reqCode : " + requestCode + " resltCode : " + resultCode);
        switch (requestCode) {
            case 1: //TODO : use TAKE_PICTURE
                if (resultCode == Activity.RESULT_OK ) {
                    try {
                        Log.i(TAG,"onActivityResult : returnIntent : "+returnIntent);
                        // retrieve from the private variable if the intent is null
                        if (returnIntent == null) {
                            Log.i(TAG,"onActivityResult : intent is null. imageUri : "+imageUri);
                            imageUri = imageUri;
                        } else {
                            imageUri = returnIntent.getData();
                            Log.i(TAG,"onActivityResult : intent is NotNull. newImageUri : "+imageUri);
                        }
                        getContentResolver().notifyChange(imageUri, null);
                        imageView.setImageURI(imageUri);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "activity :" + e,
                                Toast.LENGTH_LONG).show();
                    }
                }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageUri != null) {
            outState.putString(CAM_IMAGE_URI_KEY, imageUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(CAM_IMAGE_URI_KEY)) {
            imageUri = Uri.parse(savedInstanceState.getString(CAM_IMAGE_URI_KEY));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }


}