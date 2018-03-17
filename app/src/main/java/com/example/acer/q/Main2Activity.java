package com.example.acer.q;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.MediaStore.Images.Media.getBitmap;

public class Main2Activity extends AppCompatActivity implements View.OnTouchListener {
    private dataBase dob;
    SQLiteDatabase db;
    TextView textView;
    String lastContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        dob = new dataBase(this, "book", null, 1);
        db = dob.getWritableDatabase();

        ActionBar actionBar=getSupportActionBar();                                                                  //隐藏系统标题
        if(actionBar!=null){
            actionBar.hide();
        }

        textView = (TextView) findViewById(R.id.write);
        Button bt_cancel = (Button) findViewById(R.id.cancel);

        TextView showTime=(TextView) findViewById(R.id.textview1);                                                 //显示当前时间
        showTime.setText("   "+getTime());

        final Intent intent = getIntent();
        final String flag = intent.getStringExtra("flag");
        //判断是否为新建
        if (flag.equals("2")) {
            read();
        }
        if(flag.equals("1")){
            getWindow().setSoftInputMode(                                                                               //自动弹出软盘
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        bt_cancel.setOnClickListener(new View.OnClickListener() {                                      //点击返回并保存
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                Intent intent2=getIntent();
                String id=intent2.getStringExtra("noteId");
                lastContent=intent2.getStringExtra("lastContent");
//                Toast.makeText(Main2Activity.this,lastContent,Toast.LENGTH_LONG).show();
                if (flag.equals("1")) {
                    addDB();
                }
                if (flag.equals("2")) {
                    String y=textView.getText().toString();
//                    Toast.makeText(Main2Activity.this,y,Toast.LENGTH_LONG).show();
                    if(lastContent.equals(y)){
                        update();
                    }
                    else {
                        addDB();
                        delete();
                    }

                }
                intent1.putExtra("data_return", id);
                setResult(RESULT_OK, intent1);
                finish();
            }
        });

    }

/*    protected void onActivityResult(int requestCode, int resultCode, Intent data) {                            //添加图片
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String x = uri.toString();
            EditText editText = (EditText) findViewById(R.id.write);
            Toast.makeText(Main2Activity.this, x, Toast.LENGTH_LONG).show();
//            editText.append(x);
//            editText.getEditableText();
            Bitmap bitmap = null;
            ContentResolver cr=Main2Activity.this.getContentResolver();
            if (requestCode == 1) {
//                bitmap = BitmapFactory.decodeFile("/storage/emulated/0/HWThemes/HWWallpapers/null648ebe6486a0eb91.jpg");
                try{
                    bitmap=BitmapFactory.decodeStream(cr.openInputStream(uri));
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }

            }

            int imgWidth = bitmap.getWidth();
            int imgHeight = bitmap.getHeight();
            double partion = imgWidth * 1.0 / imgHeight;
            double sqrtLength = Math.sqrt(partion * partion + 1);

            double newImgW = 480 * (partion / sqrtLength);
            double newImgH = 480 * (1 / sqrtLength);
            float scaleW = (float) (newImgW / imgWidth);
            float scaleH = (float) (newImgH / imgHeight);

            Matrix matrix = new Matrix();
            matrix.postScale(scaleW, scaleH);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, imgWidth, imgHeight, matrix, true);
            final ImageSpan imageSpan = new ImageSpan(this, bitmap);
            SpannableString spannableString = new SpannableString(x);
            spannableString.setSpan(imageSpan, 0, spannableString.length(), SpannableString.SPAN_MARK_MARK);

//            EditText editText=(EditText) findViewById(R.id.write);
            editText.append("\n");
            Editable editable = editText.getEditableText();
            int selectionIndex = editText.getSelectionStart();
            spannableString.getSpans(0, spannableString.length(), ImageSpan.class);
            editable.insert(selectionIndex, spannableString);
            editText.append("\n");
        }
    }*/


    public boolean onCreateOptionsMenu(Menu menu) {                                                 //系统optionsmenu
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_picture:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return false;
    }

    @Override
    public void onBackPressed() {                                                        //系统返回键
        Intent intent1 = new Intent();
        Intent intent2=getIntent();
        String flag=intent2.getStringExtra("flag");
        String id=intent2.getStringExtra("noteId");
        lastContent=intent2.getStringExtra("lastContent");
//                Toast.makeText(Main2Activity.this,lastContent,Toast.LENGTH_LONG).show();
        if (flag.equals("1")) {
            addDB();
        }
        if (flag.equals("2")) {
            String y=textView.getText().toString();
//            Toast.makeText(Main2Activity.this,y,Toast.LENGTH_LONG).show();
            if(lastContent.equals(y)){
                update();
            }
            else {
                addDB();
                delete();
            }

        }
        intent1.putExtra("data_return", id);
        setResult(RESULT_OK, intent1);
        finish();
    }

    public void addDB() {                                                            //保存数据
        SQLiteDatabase db = dob.getWritableDatabase();
        ContentValues cv = new ContentValues();
        EditText editText = (EditText) findViewById(R.id.write);
        String x = textView.getText().toString();
        cv.put("content", x);
        cv.put("time", getTime());
        db.insert("book", null, cv);
    }

    public String getTime() {                                                                                       //获取时间
    /*    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date=new Date();
        String str=dateFormat.format(date);*/

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String str = "" + year + "年" + month + "月" + day + "日" + " " + hour + ":" + minute;
        return str;
    }

    public String getTitle(String x) {
        String title;
        if (x.length() > 15) {
            title = x.substring(0, 15).replaceAll("\n|\r|\t", " ");
        } else title = x.replaceAll("\n|\r|\t", " ");
        return title;
    }

    public void delete() {                                                                                       //删除数据
        Intent intent = getIntent();
        String id = intent.getStringExtra("noteId");
//        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        db.delete("book", "id=?", new String[]{id});
    }

    public void update() {                                                                                               //更新数据
        ContentValues cv = new ContentValues();
        String x = textView.getText().toString();
        cv.put("content", textView.getText().toString());
        if(!lastContent.equals(x)){
            cv.put("time", getTime());
        }
        Intent intent = getIntent();
        String id = intent.getStringExtra("noteId");
        db.update("book", cv, "id=?", new String[]{id});
        Intent in=new Intent();
    }


    public void read() {
        EditText editText = (EditText) findViewById(R.id.write);
        dob = new dataBase(this, "book", null, 1);
        db = dob.getWritableDatabase();
        Intent intent = getIntent();
        String id = intent.getStringExtra("noteId");
        //  Toast.makeText(Main2Activity.this,id,Toast.LENGTH_SHORT).show();
        Cursor cursor = db.rawQuery("select*from book where id=" + id + ";", null);
        cursor.moveToFirst();
        String content = cursor.getString(cursor.getColumnIndex("content"));
        editText.setText(content);
        editText.setSelection(content.length());
        cursor.close();

 /*       Pattern p = Pattern.compile("/([^\\\\.]*)\\\\.\\\\w{3}");
        Matcher m = p.matcher(content);
        StringBuffer strBuffer = new StringBuffer();
        int startIndex = 0;
        while (m.find()) {
            if (m.start() > 0)
                editText.append(content.substring(startIndex, m.start()));
            Uri uri = Uri.parse(m.group());
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeFile(m.group());

            ImageSpan imageSpan = new ImageSpan(this, bitmap);
            SpannableString spannableString = new SpannableString(m.group());
            spannableString.setSpan(imageSpan, 0, m.end() - m.start(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            editText.setText(spannableString);
            startIndex = m.end();
            editText.append(content.substring(startIndex, content.length()));
            db.close();
        }*/

    }


}

    //正则表达式取标题
 /*   private String getTitle(String context){
        Pattern p=Pattern.compile("/([^\\.]*)\\.\\w{3}");
        Matcher m=p.matcher(context);
        StringBuffer stringBuffer=new StringBuffer();
        String title="";
        int startIndex=0;
        while (m.find()){
            if(m.start()>0) stringBuffer.append(context.substring(startIndex,m.start()));
        }
        String path=m.group().toString();
        String type=path.substring(path.length()-3,path.length());
        if(type.equals("amr")){
            stringBuffer.append("[录音]");
        }
        else stringBuffer.append("[图片]");
        startIndex=m.end();
        if(stringBuffer.length()>15){
            title=stringBuffer.toString().replaceAll("\n|\r|\t"," ");
            return title;
        }
        stringBuffer.append(context.substring(startIndex,context.length()));
        title=stringBuffer.toString().replaceAll("\n|\r|\t"," ");
        return title;
    }*/

