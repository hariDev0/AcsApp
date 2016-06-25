package com.irya.acsapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class MessagePostActivity extends AppCompatActivity {

List<String> attlist;
    Spinner sp;
    String uid;
    ArrayAdapter<String> ad;
    String ar[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_post);
     uid=getIntent().getStringExtra("uid");
           sp=(Spinner)findViewById(R.id.et_sp);
        ar=new String[]{"MRO","secrateriate","government schools"};
  ad=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,ar);
        sp.setAdapter(ad);

        ListView lv=(ListView)findViewById(R.id.postlist);
attlist=new Vector<String>();
        ad=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,attlist);
        lv.setAdapter(ad);
        }

    public void addAtt(View v)
    {
        AlertDialog.Builder adb=new AlertDialog.Builder(this);
        adb.setMessage("Select Attachment type");
        adb.setPositiveButton("Image", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(it, 1);
            }
        });
        adb.setNegativeButton("Video", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(it, 1);
            }
        });
        adb.create().show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK)
        {
            Uri uri=data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            grantUriPermission("com.irya.acsapp",uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Cursor cursor = getContentResolver().query(uri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);

            attlist.add(picturePath);
ad.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void doPost(View v)
    {
        Post p=new Post();
        p.setUname(uid);
        p.setAtts(attlist);
        p.setTdate(new Date().toString());
        EditText et1=(EditText)findViewById(R.id.et_sub);
        EditText et2 = (EditText)findViewById(R.id.et_message);
        p.setSubj(et1.getText().toString());
        p.setMessage(et2.getText().toString());
        p.setStatus("New");
        p.setType(ar[sp.getSelectedItemPosition()]);
    new PostTask(p).execute();
    }

    class PostTask extends AsyncTask<String,String,String>
    {
        Post p;
        PostTask(Post p)
        {
            this.p=p;
        }
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(MessagePostActivity.this);
            pd.setMessage("Sending...");
            pd.show();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            if(s!=null)
                Toast.makeText(MessagePostActivity.this,s,Toast.LENGTH_LONG).show();finish();
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                HttpClient client = new DefaultHttpClient();

                HttpPost post = new HttpPost("http://192.168.1.5:8080/ACS/uploadpost.jsp");
                MultipartEntity ent =new MultipartEntity();


                for(String s:p.getAtts()) {
Uri uri=Uri.parse(s);
                    grantUriPermission("com.irya.acsapp",uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    File file = new File(uri.getPath());

                    FileBody fb = new FileBody(file);

                    ent.addPart(file.getName(), fb);

                }
                ent.addPart("uid", new StringBody(p.getUname()));
                ent.addPart("type", new StringBody(p.getType()));
                ent.addPart("subj", new StringBody(p.getSubj()));
                ent.addPart("status", new StringBody(p.getStatus()));
                ent.addPart("tdate", new StringBody(p.getTdate()));
                ent.addPart("message", new StringBody(p.getMessage()));


                post.setEntity(ent);

                HttpResponse response = client.execute(post);
if(response.getStatusLine().getStatusCode()==200)
    return EntityUtils.toString(response.getEntity());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
