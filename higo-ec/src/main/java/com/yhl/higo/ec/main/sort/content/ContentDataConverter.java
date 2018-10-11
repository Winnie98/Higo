package com.yhl.higo.ec.main.sort.content;

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
 * Created by Administrator on 2018/6/9/009.
 */

public class ContentDataConverter extends DataConverter {

    protected static List<Integer> mSortPromoId = new ArrayList<>();

    @Override
    public ArrayList<MultipleItemEntity> convert() {
        final int pageNum  = JSON.parseObject(getJsonData()).getJSONObject("data").getInteger("pageNum");
        final JSONArray array = JSON.parseObject(getJsonData()).getJSONObject("data").getJSONArray("list");
        final int size = array.size();
        if (size == 0){
            int type = 0;
            type = ItemType.PROMO_PUBLISH_NO;
            final MultipleItemEntity entity = MultipleItemEntity.builder()
                    .setField(MultipleFields.ITEM_TYPE, type)
                    .build();
            ENTITIES.add(entity);
        }else {
            if (pageNum == 1) {
                mSortPromoId.clear();
            }
            for (int i = 0; i < size ; i++) {

                final JSONObject data = array.getJSONObject(i);
                final int id = data.getInteger("id");
                mSortPromoId.add(id);
                final String categoryName = data.getString("categoryName");
                final String publisherName = data.getString("publisherName");
                final String imageHost = data.getString("imageHost");
                final String mainImage = data.getString("mainImage");
                final String promoName = data.getString("name");
                final Double price = data.getDouble("price");
                final String typeName = data.getString("typeName");

                final int countryId = data.getInteger("countryId");
                final String countryImg = data.getString("countryFlag");
                final String countryName = data.getString("countryName");
                final String createTime = data.getString("createTime");

                int type = 0;
                type = ItemType.SORT_CONTENT;

                final MultipleItemEntity entity = MultipleItemEntity.builder()
//                        .setField(MultipleFields.ITEM_TYPE, type)
                        .setItemType(ItemType.SORT_CONTENT)
                        .setField(MultipleFields.ID, id)
                        .setField(MultipleFields.IMAGE_URL, imageHost + mainImage)
                        .setField(MultipleFields.NAME, promoName)
                        .setField(MultipleFields.PRICE, price)
                        .setField(MultipleFields.PRODUCT_TYPE, typeName)
                        .setField(MultipleFields.COUNTRY_ID, countryId)
                        .setField(MultipleFields.COUNTRY_IMG, countryImg)
                        .setField(MultipleFields.COUNTRY_NAME, countryName)
                        .setField(MultipleFields.PUBLISHER,publisherName)
                        .setField(MultipleFields.CREATE_TIME,createTime)
                        .build();
                ENTITIES.add(entity);
            }
        }
        return ENTITIES;
    }
}
