package com.yhl.higo.ec.detail.tracking;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yhl.higo.ui.recycler.DataConverter;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/9/6/006.
 */

public class OrderTrackingDataConverter extends DataConverter {
    @Override
    public ArrayList<MultipleItemEntity> convert() {
        ArrayList<MultipleItemEntity> dataList = new ArrayList<>();
        final int status = JSON.parseObject(getJsonData()).getInteger("status");
        switch (status){
            case 0:
                final JSONArray listArray = JSON.parseObject(getJsonData()).getJSONObject("data").getJSONArray("traces");
                final int size = listArray.size();
                if (size == 0){
                    int type = 0;
                    type = ItemType.FEEDBACK_NO;
                    final MultipleItemEntity entity = MultipleItemEntity.builder()
                            .setField(MultipleFields.ITEM_TYPE, type)
                            .build();
                    dataList.add(entity);
                }else {
                    for (int i = size-1; i > 0; i--) {
                        final JSONObject data = listArray.getJSONObject(i);
                        final String acceptStation = data.getString("acceptStation");
                        final String acceptTime = data.getString("acceptTime");

                        final MultipleItemEntity entity = MultipleItemEntity.builder()
                                .setItemType(ItemType.TRACKING)
                                .setField(MultipleFields.ACCEPT_TIME,acceptTime)
                                .setField(MultipleFields.ACCEPT_STATION,acceptStation)
                                .build();

                        dataList.add(entity);
                    }
                }
                break;
            default:
                break;
        }
        return dataList;
    }
}
