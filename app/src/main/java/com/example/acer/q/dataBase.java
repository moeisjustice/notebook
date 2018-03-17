package com.example.acer.q;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by acer on 2017.10.26.
 */

public class dataBase extends SQLiteOpenHelper {
    private Context mcontext;
    public SQLiteDatabase db;
    public dataBase(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,"book",null,1);
//        mcontext=context;
    }
    public static final String setBook="create table if not exists book("+"id integer primary key autoincrement,"+"content text not null," +
            "time text not null)";
    @Override
    public void onCreate(SQLiteDatabase db){
//        db = SQLiteDatabase.openOrCreateDatabase(mcontext.getFilesDir().toString()+"book.db", null);
        db.execSQL(setBook);
//        Toast.makeText(mcontext,"yes",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }

}
