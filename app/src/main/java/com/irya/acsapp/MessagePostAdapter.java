package com.irya.acsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by srikrishna on 2/22/2016.
 */
public class MessagePostAdapter extends BaseAdapter {
Context ct;
    List<Post> posts;

    MessagePostAdapter(Context ct,List<Post> posts)
    {
        this.ct=ct;
        this.posts=posts;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater li=(LayoutInflater)ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=li.inflate(R.layout.lisitem,null);
        TextView tv=(TextView)v.findViewById(R.id.tv1);
        ImageView iv=(ImageView)v.findViewById(R.id.iv1);
        Post p= posts.get(i);
        tv.setText(p.getMessage());
        if(p.getStatus().equals("New"))
        {
            iv.setBackgroundResource(R.drawable.red);
        }
        else
        if(p.getStatus().equals("Process"))
        {
            iv.setImageResource(R.drawable.yellow);

        }
        else
        if(p.getStatus().equals("Completed"))
        {
            iv.setImageResource(R.drawable.green);

        }
        return v;
    }
}
