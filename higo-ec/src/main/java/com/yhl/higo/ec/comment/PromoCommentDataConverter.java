package com.yhl.higo.ec.comment;


import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yhl.higo.app.Higo;
import com.yhl.higo.ui.recycler.DataConverter;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.ui.refresh.PaginBean;
import com.yhl.higo.util.log.HigoLogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/5/22/022.
 */

public class PromoCommentDataConverter extends DataConverter {

    protected static List<Integer> mCommentId = new ArrayList<>();
//    private PaginBean mPaginBean = null;

    @Override
    public ArrayList<MultipleItemEntity> convert() {

        ArrayList<MultipleItemEntity> dataList = new ArrayList<>();

        final int status = JSON.parseObject(getJsonData()).getInteger("status");
        switch (status){
            case 0:
                final int pageNum  = JSON.parseObject(getJsonData()).getJSONObject("data").getInteger("pageNum");
                final JSONObject object = JSON.parseObject(getJsonData()).getJSONObject("data");

                final JSONArray dataArray = object.getJSONArray("list");

                final int size = dataArray.size();
                if (pageNum == 1) {
                    mCommentId.clear();
                }
                for (int i = 0;i<size;i++){
                    final JSONObject data = dataArray.getJSONObject(i);
                    final int id = data.getInteger("id");
                    mCommentId.add(id);
                    final String avatar = data.getString("avatar");
                    final int userId = data.getInteger("userId");
                    final String userName = data.getString("username");
                    final String content = data.getString("content");
//                    final Date createTime= data.getDate("createTime");
                    final String createTime= data.getString("createTime");


                    final MultipleItemEntity entity = MultipleItemEntity.builder()
                            .setField(MultipleFields.ITEM_TYPE, ItemType.COMMENT_PROMO)
                            .setField(MultipleFields.ID,id)
                            .setField(MultipleFields.USER_ID,userId)
                            .setField(MultipleFields.USER_NAME,userName)
                            .setField(MultipleFields.AVATAR,avatar)
                            .setField(MultipleFields.CREATE_TIME,createTime)
                            .setField(MultipleFields.CONTENT,content)
                            .build();

                    dataList.add(entity);
                }
                break;
            default:
                final String message = JSON.parseObject(getJsonData()).getString("msg");
                Toast.makeText(Higo.getApplicationContext(),message,Toast.LENGTH_LONG).show();
                HigoLogger.d("DATALIST",dataList);
                break;
        }

        return dataList;
    }
}
