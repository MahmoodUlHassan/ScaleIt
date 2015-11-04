package mahmoodh.scaleit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by ivy4619 on 3/10/15.
 */
public class ImageEditor extends Activity {

static private final String TAG1= "ImageEditor : ";

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG1, "in EDITOR onCreate");
        setContentView(R.layout.activity_image_editor);
        LinearLayout mDrawingPad=(LinearLayout)findViewById(R.id.view_drawing_pad);
        final MyImageView myImageView=new MyImageView(this);

        findViewById(R.id.setReferenceBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int saveReferenceResult = myImageView.SaveAsReference();
                if (saveReferenceResult==0) {
                     Toast.makeText(getApplicationContext(),"Reference Saved Successfully",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Use your finger to draw a reference dimension",Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.clearBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myImageView.clearBtnAction();
            }
        });

        findViewById(R.id.setTargetBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int saveTargetResult = myImageView.SaveAsTarget();
                if (saveTargetResult == 0) {
                    Toast.makeText(getApplicationContext(), "Target Saved Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Use your finger to draw a target dimension", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.measureBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float measurement = myImageView.Measure();
                if (measurement == 0) {
                    Toast.makeText(getApplicationContext(), "measurement failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Target/Reference : " + measurement, Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.saveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myImageView.saveToGallery();
            }
        });

        mDrawingPad.addView(myImageView);

        Intent theStrtEditorIntent = getIntent();
        Uri imgUri = Uri.parse(theStrtEditorIntent.getExtras().getString("imageUri"));
        String path = "";
        path = getRealPathFromURI(imgUri);
        try {
            Log.i(TAG1,"Drawble A: ");
            clearImageViewCache(myImageView);
            Log.i(TAG1, "Drawble B: ");

            Drawable d = Drawable.createFromPath(path);
            Log.i(TAG1,"Drawble d: " + d);
            myImageView.setBackground(d);
        }
        catch (Exception e){
            Log.i(TAG1,"Ops someexception while getting path from file");
        }
    }
    public static void clearImageViewCache(MyImageView view) {
//        if ( view.getDrawable() instanceof BitmapDrawable ) {
//            ((BitmapDrawable) view.getDrawable())
//            ((BitmapDrawable)view.getDrawable()).getBitmap().recycle();
//        }
//        view.getDrawable().setCallback(null);
//        view.setImageDrawable(null);

//        view.getDrawingCache().recycle();
//        view.getResources().flushLayoutCache();
//        view.destroyDrawingCache();
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result = "";
        try {
            Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
                Log.i(TAG1, "getRealPathFromURI : IF : cursor : "+cursor);
            } else if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
                Log.i(TAG1, "getRealPathFromURI : ELSE : cursor : "+cursor);
            }
            cursor = null;
        } catch (Exception e){
            result = contentURI.getPath();
        }
        return result;
    }

// Lifecycle callback overrides

@Override
public void onStart() {
    super.onStart();
}

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
