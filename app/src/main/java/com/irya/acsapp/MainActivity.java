package com.irya.acsapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class MainActivity extends AppCompatActivity {

    MessagePostAdapter ma;
    List<Post> posts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lv=(ListView)findViewById(R.id.lv1);
        posts=new Vector<Post>();
        ma=new MessagePostAdapter(this,posts);
lv.setAdapter(ma);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int idd, long l) {

                AlertDialog.Builder adb=new AlertDialog.Builder(MainActivity.this);
                adb.setMessage("Need Login ! Are you Sure");
                adb.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
Intent it=new Intent(MainActivity.this,LoginActivity.class);

                                startActivity(it);
                                finish();
                            }
                        }
                );
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                adb.create().show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        new MessageTaskAct().execute("All");
    }

    class MessageTaskAct extends AsyncTask<String,String,List<Post>>
    {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(MainActivity.this);
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
