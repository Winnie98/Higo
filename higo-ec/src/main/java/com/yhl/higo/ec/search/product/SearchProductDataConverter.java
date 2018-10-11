package com.yhl.higo.ec.search.product;

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
 * Created by Administrator on 2018/5/26/026.
 */

public class SearchProductDataConverter extends DataConverter {

    protected static List<Integer> mProductId = new ArrayList<>();

    @Override
    public ArrayList<MultipleItemEntity> convert() {
        final int status = JSON.parseObject(getJsonData()).getInteger("status");
        switch (status) {
            case 0:
                final int pageNum  = JSON.parseObject(getJsonData()).getJSONObject("data").getInteger("pageNum");
                final JSONArray array = JSON.parseObject(getJsonData()).getJSONObject("data").getJSONArray("list");
                final int size = array.size();
                if (size == 0){
                    int type = 0;
                    type = ItemType.PRODUCT_NO;
                    final MultipleItemEntity entity = MultipleItemEntity.builder()
                            .setField(MultipleFields.ITEM_TYPE, type)
                            .build();
                    ENTITIES.add(entity);
                }else {
                    if (pageNum == 1){
                        mProductId.clear();
                    }
                    for (int i = 0; i < size ; i++){
                        final JSONObject data = array.getJSONObject(i);
                        final int id = data.getInteger("id");
                        mProductId.add(id);
                        final String imgHost = data.getString("imageHost");
                        final String mainImage = data.getString("mainImage");
                        final String productName = data.getString("name");
                        final Double price = data.getDouble("price");
                        final String categoryName = data.getString("categoryName");
                        final String username = data.getString("username");
                        //                final int quantity = data.getInteger("quantity");
                        final String productType = data.getString("typeName");
                        final String createTime = data.getString("createTime");

                        final int countryId = data.getInteger("countryId");
                        final String countryImg = data.getString("countryFlag");
                        final String countryName = data.getString("countryName");


                        int type = 0;
                        type = ItemType.MARKET;

                        final MultipleItemEntity entity = MultipleItemEntity.builder()
                                //                        .setItemType(ItemType.MARKET)
                                .setField(MultipleFields.ITEM_TYPE,type)
                                .setField(MultipleFields.ID,id)
                                .setField(MultipleFields.IMAGE_URL,imgHost+mainImage)
                                .setField(MultipleFields.TITLE,productName)
                                .setField(MultipleFields.PRICE,price)
                                .setField(MultipleFields.CATEGORY,categoryName)
                                //                        .setField(MultipleFields.NUMBER,quantity)
                                .setField(MultipleFields.USER_NAME,username)
                                .setField(MultipleFields.PRODUCT_TYPE,productType)
                                .setField(MultipleFields.CREATE_TIME,createTime)
                                .setField(MultipleFields.COUNTRY_ID,countryId)
                                .setField(MultipleFields.COUNTRY_IMG,countryImg)
                                .setField(MultipleFields.COUNTRY_NAME,countryName)
                                .build();
                        ENTITIES.add(entity);
                    }
                }
                break;
            default:
                break;
        }
        return ENTITIES;
    }
}
