package com.yhl.higo.ec.main.personal.feeedback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yhl.higo.ui.recycler.DataConverter;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/15/015.
 */

public final class FeedbackDataConverter extends DataConverter {

    protected static List<Integer> mFeedbackId = new ArrayList<>();

    @Override
    public ArrayList<MultipleItemEntity> convert() {

        ArrayList<MultipleItemEntity> dataList = new ArrayList<>();

        final int status = JSON.parseObject(getJsonData()).getInteger("status");
        switch (status){
            case 0:
                final int pageNum = JSON.parseObject(getJsonData()).getJSONObject("data").getInteger("pageNum");
                final JSONArray listArray = JSON.parseObject(getJsonData()).getJSONObject("data").getJSONArray("list");
                final int size = listArray.size();
                if (size == 0){
                    int type = 0;
                    type = ItemType.FEEDBACK_NO;
                    final MultipleItemEntity entity = MultipleItemEntity.builder()
                            .setField(MultipleFields.ITEM_TYPE, type)
                            .build();
                    dataList.add(entity);
                }else {
                    if (pageNum == 1){
                        mFeedbackId.clear();
                    }
                    for (int i = 0; i<size; i++) {
                        final JSONObject data = listArray.getJSONObject(i);

                        final int  id = data.getInteger("id");
                        mFeedbackId.add(id);
                        final int userId = data.getInteger("userId");
                        final String username = data.getString("username");
                        final long orderNo = data.getLong("orderNo");
                        final int statusId = data.getInteger("statusId");
                        final String statusName = data.getString("statusName");

                        int type = 0;
                        type = ItemType.FEEDBACK;

                        final MultipleItemEntity entity = MultipleItemEntity.builder()
                                .setField(MultipleFields.ITEM_TYPE, type)
                                .setField(MultipleFields.ID, id)
                                .setField(MultipleFields.ORDER_NO, orderNo)
                                .setField(MultipleFields.STATUS_NAME, statusName)
                                .build();

                        dataList.add(entity);
                    }
                }
                break;
            case 1:
                break;
            default:
                break;
        }
        return dataList;

    }
}
