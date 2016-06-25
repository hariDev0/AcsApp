package com.irya.acsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class HomeActivity extends AppCompatActivity {

    MessagePostAdapter ma;
    List<Post> posts;
    Spinner sp;
String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp=(Spinner)findViewById(R.id.sp1);
        final String ar[]={"All","MRO","secrateriate","government schools"};
        ArrayAdapter<String>  ad=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,ar);
        sp.setAdapter(ad);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                new MessageTaskAct().execute(ar[i]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
uid=getIntent().getStringExtra("uid");
/*        int id=getIntent().getExtras().getInt("id");
        if(id!=-1)
        {
            Intent it=new Intent(HomeActivity.this,DetailPostAct.class);
            it.putExtra("id",id);
            startActivity(it);

        }*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent it = new Intent(HomeActivity.this, MessagePostActivity.class);
                it.putExtra("uid",uid);
                startActivity(it);

            }
        });
        ListView lv=(ListView)findViewById(R.id.lv1);
        posts=new Vector<Post>();
        ma=new MessagePostAdapter(this,posts);
        lv.setAdapter(ma);
lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent it=new Intent(HomeActivity.this,DetailPostAct.class);
      it.putExtra("post",posts.get(i));
        it.putExtra("id",i);
        startActivity(it);
    }
});
    }

    class MessageTaskAct extends AsyncTask<String,String,List<Post>>
    {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(HomeActivity.this);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<Post> tposts) {
            pd.dismiss();

            if(tposts!=null) {
                posts.clear();
                posts.addAll(tposts);
                ma.notifyDataSetChanged();
            }

                super.onPostExecute(posts);
        }

        @Override
        protected List<Post> doInBackground(String... strings) {
            try
            {
                HttpClient client=new DefaultHttpClient();
                HttpPost hpost=new HttpPost("http://192.168.1.5:8080/ACS/getPosts.jsp");
                BasicNameValuePair p1=new BasicNameValuePair("cat",strings[0]);
                List<BasicNameValuePair> list=new Vector<BasicNameValuePair>();
                list.add(p1);

                UrlEncodedFormEntity ent=new UrlEncodedFormEntity(list);
                hpost.setEntity(ent);
                HttpResponse hres=client.execute(hpost);
                if(hres.getStatusLine().getStatusCode()==200)
                {
                    DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
                    DocumentBuilder db=dbf.newDocumentBuilder();
                    Document doc=db.parse(hres.getEntity().getContent());

                    doc.getDocumentElement().normalize();
List<Post> tposts=new Vector<Post>();
                    NodeList nl=doc.getElementsByTagName("post");
                    for(int i=0;i<nl.getLength();i++)
                    {
                        Element el=(Element)(nl.item(i));

                        Post p=new Post();
                        p.setId(el.getElementsByTagName("id").item(0).getFirstChild().getTextContent());
                        p.setMessage(el.getElementsByTagName("message").item(0).getFirstChild().getTextContent());
                        p.setStatus(el.getElementsByTagName("status").item(0).getFirstChild().getTextContent());
                        p.setType(el.getElementsByTagName("type").item(0).getFirstChild().getTextContent());

                        p.setSubj(el.getElementsByTagName("subj").item(0).getFirstChild().getTextContent());
                        p.setUname(el.getElementsByTagName("uid").item(0).getFirstChild().getTextContent());
                        p.setTdate(el.getElementsByTagName("tdate").item(0).getFirstChild().getTextContent());
                   Element atel=(Element)el.getElementsByTagName("atts").item(0);
                       NodeList atlist=atel.getElementsByTagName("att");
                    List<String> atts=new Vector<String>();
                        for(int j=0;j<atlist.getLength();j++)
                    {

                        String s=new String(atlist.item(j).getFirstChild().getTextContent());
                    atts.add(s);
                    }
                        p.setAtts(atts);
                    tposts.add(p);
                    }

                    return tposts;
                }


            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
