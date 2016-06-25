package com.irya.acsapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.List;
import java.util.Vector;

public class LoginActivity extends AppCompatActivity {
String un;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
id=getIntent().getIntExtra("id",-1);

    }
    public void doSignin(View v)
    {

        EditText et1=(EditText)findViewById(R.id.un);
        EditText et2=(EditText)findViewById(R.id.ps);
         un=et1.getText().toString();
        String ps=et2.getText().toString();


new LoginTask().execute(un, ps);
    }
    public void doSignup(View v)
    {
        Intent it=new Intent(this,RegisterActivity.class);
        startActivity(it);

    }
    public void doAsGuest(View v)
    {
        Intent it=new Intent(this,MainActivity.class);
        startActivity(it);
        finish();

    }

    class LoginTask extends AsyncTask<String, String, String>
    {

        @Override
        protected void onPostExecute(String result) {

            // TODO Auto-generated method stub
            if(result!=null)
            {
                if(result.trim().equals("success"))
                {
                    Intent it=new Intent(LoginActivity.this,HomeActivity.class);
                    it.putExtra("uid", un);
                    it.putExtra("id",id);
                    startActivity(it);
                    finish();
                }
                else
                    Toast.makeText(LoginActivity.this, "Invalid User Id or Password", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(LoginActivity.this, "Try agin later", Toast.LENGTH_LONG).show();


            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try
            {
                HttpClient client=new DefaultHttpClient();
                HttpPost hpost=new HttpPost("http://192.168.1.5:8080/ACS/logincheck.jsp");
                BasicNameValuePair p1=new BasicNameValuePair("uid",params[0]);
                BasicNameValuePair p2=new BasicNameValuePair("up",params[1]);
                List<BasicNameValuePair> list=new Vector<BasicNameValuePair>();
                list.add(p1);
                list.add(p2);
                UrlEncodedFormEntity ent=new UrlEncodedFormEntity(list);
                hpost.setEntity(ent);
                HttpResponse hres=client.execute(hpost);
                if(hres.getStatusLine().getStatusCode()==200)
                {
                    String s= EntityUtils.toString(hres.getEntity());
                    return s;
                }


            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }}
