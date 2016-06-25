package com.irya.acsapp;


import android.app.Activity;
import android.os.AsyncTask;
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


public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acitivity);
    }

    public void doSignup(View v)
    {

        EditText et1=(EditText)findViewById(R.id.et_uname);
        EditText et2=(EditText)findViewById(R.id.et_ps);
        EditText et3=(EditText)findViewById(R.id.et_name);
        EditText et4=(EditText)findViewById(R.id.et_mobile);
        EditText et5=(EditText)findViewById(R.id.et_email);

        String un=et1.getText().toString();
        String ps=et2.getText().toString();
        String name=et3.getText().toString();

        String mob=et4.getText().toString();
        String email=et5.getText().toString();

        new RegisterTask().execute(un,ps,name,mob,email);


    }
    class RegisterTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if(result!=null)
                if(result.equals("success"))
                {
                    Toast.makeText(RegisterActivity.this, "Success" , Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                    Toast.makeText(RegisterActivity.this, result , Toast.LENGTH_LONG).show();
finish();
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try
            {
                HttpClient client=new DefaultHttpClient();
                HttpPost hpost=new HttpPost("http://192.168.1.7:8080/ACS/regsave.jsp");
                BasicNameValuePair p1=new BasicNameValuePair("uid",params[0]);
                BasicNameValuePair p2=new BasicNameValuePair("up",params[1]);
                BasicNameValuePair p3=new BasicNameValuePair("name",params[2]);
                BasicNameValuePair p4=new BasicNameValuePair("mob",params[3]);
                BasicNameValuePair p5=new BasicNameValuePair("email",params[4]);
                List<BasicNameValuePair> list=new Vector<BasicNameValuePair>();
                 list.add(p1);
                list.add(p2);
                list.add(p3);
                list.add(p4);
                list.add(p5);
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
    }
}
