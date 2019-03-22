package com.marissalara.chal1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.widget.Toast;
import android.provider.MediaStore;

import java.text.NumberFormat;
import java.text.ParsePosition;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_CODE_TAKE_PICTURE = 1;
    ImageView imgView;
    Button captureBtn;
    EditText lookupTxt;
    Button lookupBtn;
    dataHelp db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.imgView = (ImageView) findViewById(R.id.capturedImage);
        this.captureBtn = (Button) findViewById(R.id.btnTakePicture);
        this.lookupTxt = (EditText) findViewById(R.id.EnterId);
        this.lookupBtn = (Button) findViewById(R.id.btnEnterId);
        this.db = new dataHelp(getApplicationContext());
    }
/**************************************************************************************/

    //The function that runs when the app starts
    public void onLookup(View view){
        String idChoice = this.lookupTxt.getText().toString();
        if (isNumeric(idChoice)){
            try{
                String encodedImg = db.getPic(idChoice);
                if(encodedImg.equalsIgnoreCase("SQL Error Detected")) {
                    showPicNotFoundAlert(view);
                }
                else{
                    Bitmap bmp = db.decodeFromBase64(encodedImg);
                    this.imgView.setImageBitmap(bmp);
                }
            }
            catch (SQLiteException e){
                System.out.println("COULDN'T READ SELECT FROM DB");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        else{
            InvalidIDAlert(view);
        }
    }
/****************************************************************************************/

    //Checks to make sure the ID the user types is a number
    public boolean isNumeric(String str){
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }
/****************************************************************************************/

    //Opens Camera and Takes a Photo
    public void takePhoto(View view){
        Intent It = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (It.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(It, REQ_CODE_TAKE_PICTURE);
        }
    }
 /***************************************************************************************/

    //Checks if there is a photo taken and saves it to database and displays it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(resultCode == RESULT_OK){
            try{
                Bitmap bmp = (Bitmap) intent.getExtras().get("data");
                this.imgView.setImageBitmap(bmp);
                db.insertImg(db.encodeToBase64(bmp));
            }
            catch(SQLiteException e){
                System.out.println("COULD NOT INSERT TO DATABASE");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
        }
    }
/****************************************************************************************/

    //Gives a message if ID isn't valid
    public void InvalidIDAlert(View view){
        Toast.makeText(this, "Invalid ID", Toast.LENGTH_LONG).show();
    }
/*****************************************************************************************/

    //Gives a message if picture isn't found
    public void showPicNotFoundAlert(View view){
        Toast.makeText(this, "Picture Not Found", Toast.LENGTH_LONG).show();
    }
}
