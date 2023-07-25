package algonquin.cst2335.pate0323;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SecondActivity extends AppCompatActivity {
    private static final String image = "picture.png";
    private String imagePath;
    ImageView profileImage ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent fromPrevious = getIntent();
        String emailAddress = fromPrevious.getStringExtra("EmailAddress");
        TextView textView2 = findViewById(R.id.textView2);
        textView2.setText("Welcome back " + emailAddress);
        Button changePicture = findViewById(R.id.button2);
        profileImage = findViewById(R.id.profileImage);
        TextView phoneNumber = findViewById(R.id.editTextPhone);
        Button callNumber = findViewById(R.id.callNumber);
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        prefs.getString("VariableName", "");
        String phNum = prefs.getString("Phone Number", "");

        callNumber.setOnClickListener( clk-> {
            String Number = phoneNumber.getText().toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Phone", Number);
            editor.apply();
            Intent call = new Intent(Intent.ACTION_DIAL);
            call.setData(Uri.parse("tel:" + Number));
            startActivity(call);
        });

        File picture = new File(getFilesDir(), image);
        if(picture.exists()) {
            imagePath = picture.getAbsolutePath();
            Bitmap theImage = BitmapFactory.decodeFile(imagePath);
            profileImage.setImageBitmap(theImage);
        }

        ActivityResultLauncher<Intent> cameraResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Bitmap thumbnail = data.getParcelableExtra("data");
                            profileImage.setImageBitmap(thumbnail);
                        }
                    }
                });

        changePicture.setOnClickListener( clk-> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraResult.launch(cameraIntent);
        });

        File imageFile = new File(getFilesDir(), image);
        if (!imageFile.exists() || imagePath == null) {
            profileImage.setImageResource(R.drawable.bear);
        }

    }

    private void saveImage(Bitmap bitmap) throws IOException {
        FileOutputStream fOut = null;
        try {
            fOut = openFileOutput("Picture.png", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fOut.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}