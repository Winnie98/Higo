package com.yhl.higo.ec.save;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/28/028.
 */
//共享数据的存储与读取
public class ShareHelper {
    private Context mContext;

    public ShareHelper(Context mContext) {
        this.mContext = mContext;
    }

    public ShareHelper() {
    }

    //存用户名、密码方法
    public void save(String name,String password)
    {
        //打开名为mysp.xml文件，如果该文件不存在，则创建该配置文件
        SharedPreferences sp=mContext.getSharedPreferences("mysp",Context.MODE_PRIVATE);
        //使SharedPreferences处于编辑状态
        SharedPreferences.Editor editor=sp.edit();
        //存放数据
        editor.putString("username",name);
        editor.putString("password",password);
        //提交
        editor.commit();

//        Toast.makeText(mContext,"信息已写入SharedPreferences中",Toast.LENGTH_LONG).show();


    }
    //读取mysp文件中内容的方法
    public Map<String,String> read()
    {
        Map<String,String> data=new HashMap<>();
        SharedPreferences sp=mContext.getSharedPreferences("mysp",Context.MODE_PRIVATE);
        //读取出mysp.xml文件中的用户名的值，再存入data对象中
        data.put("username",sp.getString("username",""));
        data.put("password",sp.getString("password",""));
        return data;
    }
}
