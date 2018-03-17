package com.example.acer.q;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity{
    private SQLiteDatabase db;
    private dataBase dob;
    private SimpleAdapter simpleAdapter;
    List<HashMap<String,String>> data=new ArrayList<>();
    String r;
    String ifcount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }

        dob=new dataBase(this,"book",null,1);
        db=dob.getWritableDatabase();

        final ListView listView=(ListView) findViewById(R.id.list);
        Button bt_create=(Button) findViewById(R.id.create);
        Button bt_manage=(Button)findViewById(R.id.manage);
        TextView textView=(TextView) findViewById(R.id.textview);

        bt_create.setOnClickListener(new View.OnClickListener() {                               //点击新建
            @Override
            public void onClick(View v) {                                                           //新建
                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                intent.putExtra("flag","1");
                startActivityForResult(intent,1);
      //          finish();
            }
        });

        bt_manage.setOnClickListener(new View.OnClickListener() {                                       //全部删除
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("删除");
                builder.setMessage("确认全部删除吗");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db=dob.getWritableDatabase();
                 //       db.delete("book","id<?",new String[]{"10000000000000000000"});
                        db.delete("book",null,null);
                        showNotesList();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {              //点击item将item对应数据id传给下个activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String content=listView.getItemAtPosition(position)+"";

                String content0=content.substring(content.indexOf(",")+1,content.indexOf("l")+1);
                String content1=content0.substring(content0.indexOf("=")+1,content0.indexOf(","));
                dob=new dataBase(MainActivity.this,"book",null,1);
                db=dob.getWritableDatabase();
                Cursor cursor = db.rawQuery("select*from book where id=" + content1 + ";", null);
                while (cursor.moveToNext()){
                    ifcount=cursor.getString(cursor.getColumnIndex("content"));
 //                   Toast.makeText(MainActivity.this,ifcount,Toast.LENGTH_LONG).show();
                }
                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                intent.putExtra("flag","2");
                intent.putExtra("noteId",content1);
                intent.putExtra("lastContent",ifcount);
                startActivityForResult(intent,1);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {                         //长按删除
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int n=position;
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("删除");
                builder.setMessage("确认删除吗");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db=dob.getWritableDatabase();
                        ListView listView=(ListView) findViewById(R.id.list);
                        String content=listView.getItemAtPosition(n)+"";
                        String content0=content.substring(content.indexOf(",")+1,content.indexOf("l")+1);
                        String content1=content0.substring(content0.indexOf("=")+1,content0.indexOf(","));
                //        Toast.makeText(MainActivity.this,content1,Toast.LENGTH_SHORT).show();
                     /*   ContentValues cv=new ContentValues();
                        cv.put("content","");
                        db.update("book",cv,"id=?",new String[]{content1});*/
                        db.delete("book","id=?",new String[]{content1});
                        showNotesList();
                        total();
                //        Toast.makeText(MainActivity.this,x+"",Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();
                return true;
            }
        });

        showNotesList();
        total();
    //    Toast.makeText(this,x+"",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    ListView listView=(ListView) findViewById(R.id.list);
                    r=data.getStringExtra("data_return");
//                    Toast.makeText(this,r,Toast.LENGTH_LONG).show();
                    showNotesList();
                    total();
         //           Toast.makeText(this,x+"",Toast.LENGTH_LONG).show();
         //           Toast.makeText(this,"OK",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    public void total(){                                                                                //统计记事数量
        TextView textView=(TextView) findViewById(R.id.textview);
        ListView listView=(ListView) findViewById(R.id.list);
        int x=listView.getCount();
        textView.setText("记事本一共 "+x+" 条");
    }

    private void showNotesList(){                                                                   //将数据库内容显示在listview
        ListView listView=(ListView) findViewById(R.id.list);

        dob=new dataBase(this,"book",null,1);
        db=dob.getWritableDatabase();
        int size=data.size();
        if(size>0){
            data.removeAll(data);
        }
        Cursor cursor=db.rawQuery("select*from book",null);
        while (cursor.moveToNext()){
            HashMap<String,String> map=new HashMap<>();
            String id=cursor.getString(cursor.getColumnIndex("id"));
            String time=cursor.getString(cursor.getColumnIndex("time"));
            String title=cursor.getString(cursor.getColumnIndex("content"));
 //                Toast.makeText(this,id,Toast.LENGTH_SHORT).show();
            if(title.length()==0){
                db.delete("book","id=?",new String[]{id});
            }
            if(title.length()!=0){
                map.put("id",id);
                map.put("time",time);
                map.put("title",getTitle(title));
                data.add(0,map);
            }

        }
        simpleAdapter=new SimpleAdapter(MainActivity.this,
                data,
                R.layout.activity_main0,
                new String[]{"time","title"},
                new int[]{R.id.time,R.id.title});
        listView.setAdapter(simpleAdapter);

//        Toast.makeText(this,i,Toast.LENGTH_SHORT).show();
  /*     SimpleCursorAdapter adapter=new SimpleCursorAdapter(this,R.layout.activity_main0,cursor,
                new String[]{"id","time","title"},new int[]{R.id._id,R.id.time,R.id.title},0);
       listView.setAdapter(adapter);*/
/*        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/
        db.close();
    }

    public String getTitle(String x){
        String title;
        if(x.length()>30){
            title=x.substring(0,30).replaceAll("\n|\r|\t"," ");
            String s="";
            for(int i=1;i<=30;i++) s+=" ";
            if(title.equals(s)){
                title="新建记事本";
            }
            else title=title+"...";
        }
        else{
            title=x.replaceAll("\n|\r|\t"," ");
            String s="";
            for(int i=0;i<title.length();i++) s+=" ";
            if(title.equals(s)){
                title="新建记事本";
            }
        }
        return title;
    }
}
