package com.yhl.higo.ec.main.personal.profile;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.main.personal.order.OrderSubmitDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.image.GlideApp;
import com.yhl.higo.util.log.HigoLogger;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2018/5/25/025.
 */

public class OthersUserInfoDelegate extends HigoDelegate{
    private int mUserId;

    @BindView(R2.id.txt_others_user_info_username)
    TextView mUsername = null;
    @BindView(R2.id.txt_others_user_info_sell_count)
    TextView mSellCount = null;
    @BindView(R2.id.get_others_user_info_avatar)
    CircleImageView mAvatar = null;
    @BindView(R2.id.user_info_others_rb_bar)
    RatingBar mOthersRtBar = null;

    @OnClick(R2.id.icon_others_user_info_back)
    void onClickBack(){
        getSupportDelegate().pop();

    }

    public static OthersUserInfoDelegate create(int userId) {

        final Bundle args = new Bundle();
        args.putInt("userId",userId);
        final OthersUserInfoDelegate delegate = new OthersUserInfoDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    public void getUserInfo(){
        RestClient.builder()
                .url("user/user/get_user_info.do")
                .params("userId",mUserId)
                .success(new ISuccess() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("USER_INFO",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final JSONObject data = JSON.parseObject(response).getJSONObject("data");
                                final String username = data.getString("username");
                                final String avatar = data.getString("avatar");
                                final double score = data.getDouble("score");
                                final Double sellCount = data.getDouble("sellCount");
                                final String imageHost = data.getString("imageHost");

                                GlideApp.with(getContext())
                                        .load(imageHost+avatar)
                                        .into(mAvatar);
                                mUsername.setText(username);
                                mSellCount.setText(String.valueOf(sellCount));
                                mOthersRtBar.setRating((float) score);

                                break;
                            default:
                                break;
                        }
                    }
                })
                .build()
                .get();
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_user_info_others;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        final Bundle args = getArguments();
        if (args != null) {
            mUserId = args.getInt("userId");
            getUserInfo();
        }
        mOthersRtBar.setEnabled(false);
    }
}
