package com.marissalara.chal1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class dataHelp {
    private Context context;
    private SQLiteDatabase db;
    private final String Name = "Pictures";
    private final String SQLTable = "DROP TABLE IF EXISTS " + this.Name;
    private final String SQLCreateTable = "CREATE TABLE " + this.Name + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " image TEXT)";
    /**************************************************************************************/
    public dataHelp(){}
    /**************************************************************************************/
    public dataHelp(Context context){
        this.context = context;
        this.db = context.openOrCreateDatabase(this.Name, Context.MODE_PRIVATE, null);
        createDB();
    }
    /**************************************************************************************/

    //Creates a database
    private void createDB(){
        try{
            this.db.execSQL(this.SQLTable);
            this.db.execSQL(this.SQLCreateTable);
        }
        catch (SQLiteException e){
            System.out.println("Couldn't create the Pictures table");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    /**************************************************************************************/

    //Inserts Image into database and adds an ID with it
    public void insertImg(String base64Img) throws SQLiteException{
        String insert = "INSERT INTO " + this.Name + "(image) VALUES ('" + base64Img + "')";
        db.execSQL(insert);
    }
    /**************************************************************************************/

    //Gets picture from SQL database using ID
    public String getPic(String id) throws SQLiteException{
        String select = "SELECT image FROM " + this.Name + " WHERE id=" + id;
        Cursor cursorz = db.rawQuery(select, null);

        String returnedImg = "SQL Error Detected";

        if(cursorz.moveToFirst()){
            do {
                returnedImg = cursorz.getString(cursorz.getColumnIndex("image"));
            }
            while (cursorz.moveToNext());
            cursorz.close();
        }
        return returnedImg;
    }
    /**************************************************************************************/

    //The last two are used to for backend of the database
    public String encodeToBase64(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bai = baos.toByteArray();
        String base64Image = Base64.encodeToString(bai, Base64.DEFAULT);
        return base64Image;
    }
    /**************************************************************************************/
    public Bitmap decodeFromBase64(String base64Img){
        byte[] data = Base64.decode(base64Img, Base64.DEFAULT);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        Bitmap bm;
        bm = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
        return bm;
    }
}
