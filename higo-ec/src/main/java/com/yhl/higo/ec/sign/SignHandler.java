package com.yhl.higo.ec.sign;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2018/4/27/027.
 */

public class SignHandler {
    public static String mPhone;

    public static String getPhone() {
        return mPhone;
    }

    public static void setPhone(String mPhone) {
        SignHandler.mPhone = mPhone;
    }

    public  static void onSignIn(String response,ISignListener signListener){
        final JSONObject profileJson = JSON.parseObject(response).getJSONObject("data");
        final long userId = profileJson.getLong("id");
        final String name = profileJson.getString("username");
        final String password =profileJson.getString("password");
        final String email = profileJson.getString("email");
        final String phone = profileJson.getString("phone");
        mPhone = profileJson.getString("phone");
        final String question = profileJson.getString("question");
        final String answer = profileJson.getString("answer");
        final String role = profileJson.getString("role");
        final String createTime = profileJson.getString("createTime");
        final  String updateTime = profileJson.getString("updateTime");

//        该方法调用逻辑还需梳理，先注释，之后再添加

        Log.e("onSignIn","已经登录成功");
        //已经注册并登录成功了
//        AccountManager.setSignState(true);
        signListener.onSignInSuccess();
    }

    public  static void onSignUp(String response,ISignListener signListener){

        Log.e("onSignUp","已经注册成功");
        //已经注册并登录成功了
//        AccountManager.setSignState(true);
        signListener.onSignUpSuccess();

    }
}
