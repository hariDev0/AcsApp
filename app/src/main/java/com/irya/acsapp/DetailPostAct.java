package com.irya.acsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DetailPostAct extends AppCompatActivity {

    Post p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
String id=getIntent().getStringExtra("id");
        p=(Post)getIntent().getExtras().get("post");

TextView tv1=(TextView)findViewById(R.id.tv_sub);
        TextView tv2=(TextView)findViewById(R.id.tv_message);
        TextView tv3=(TextView)findViewById(R.id.tv_sp);
        TextView tv4=(TextView)findViewById(R.id.tv_status);
tv1.setText(p.getSubj());
        tv2.setText(p.getMessage());
    tv3.setText(p.getType()+"  "+p.getTdate()+"  "+p.getUname());
        tv4.setText(p.getStatus());
        ArrayAdapter<String> ad=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,p.getAtts());
        ListView lv=(ListView)findViewById(R.id.postlist);
        lv.setAdapter(ad);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent it=new Intent(Intent.ACTION_VIEW);
                it.setData(Uri.parse("http://192.168.1.5:8080/ACS/uploads/"+p.getId()+"/"+p.getAtts().get(i)));
                startActivity(it);
            }
        });


    }

}
