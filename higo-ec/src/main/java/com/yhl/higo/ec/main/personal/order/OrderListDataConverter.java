package com.yhl.higo.ec.main.personal.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;;
import com.yhl.higo.ui.recycler.DataConverter;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/5/20/020.
 */

public class OrderListDataConverter extends DataConverter {

    @Override
    public ArrayList<MultipleItemEntity> convert() {
//        final JSONArray array = JSON.parseObject(getJsonData()).getJSONArray("data");
        final int status = JSON.parseObject(getJsonData()).getInteger("status");
        switch (status){
            case 0:
                final JSONArray array = JSON.parseObject(getJsonData()).getJSONObject("data").getJSONArray("list");
                final int size = array.size();
                for (int i = 0; i < size ; i++){
                    final JSONObject data = array.getJSONObject(i);
                    final long orderNo = data.getLong("orderNo");
                    final String imageHost =data.getString("imageHost");
                    final String productImage = data.getString("productImage");
                    final String title = data.getString("productName");
                    final int id = data.getInteger("id");
                    final Double price = data.getDouble("productPrice");
                    final Double totalPrice = data.getDouble("totalAmount");
                    final int productQuantity = data.getInteger("productQuantity");
                    final String time = data.getString("time");
                    final int statusId = data.getInteger("statusId");
                    final String statusName = data.getString("statusName");
                    final int sellerUserId = data.getInteger("sellerUserId");
                    final String sellerUsername = data.getString("sellerUsername");


                    final MultipleItemEntity entity = MultipleItemEntity.builder()
                            .setItemType(ItemType.ITEM_ORDER_LIST)
                            .setField(MultipleFields.ID,id)
                            .setField(MultipleFields.IMAGE_URL,imageHost+productImage)
                            .setField(MultipleFields.TITLE,title)
                            .setField(MultipleFields.PRICE,price)
                            .setField(MultipleFields.NUMBER,productQuantity)
                            .setField(MultipleFields.TOTAL_PRICE,totalPrice)
                            .setField(MultipleFields.STATUS_ID,statusId)
                            .setField(MultipleFields.ORDER_NO,orderNo)
                            .setField(MultipleFields.SELLER_ID,sellerUserId)
                            .setField(MultipleFields.SELLER,sellerUsername)
                            .build();

                    ENTITIES.add(entity);
                }
                break;
            case 1:
                //用户无此类订单
                final MultipleItemEntity entity = MultipleItemEntity.builder()
                        .setItemType(ItemType.ITEM_ORDER_NO)
                        .build();
                ENTITIES.add(entity);
                break;
        }

        return ENTITIES;
    }
}
