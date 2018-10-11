package com.yhl.higo.ec.main.index;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yhl.higo.ui.recycler.DataConverter;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.ui.refresh.PaginBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/15/015.
 */

public final class IndexDataConverter extends DataConverter {

    protected static List<Integer> mPromoId = new ArrayList<>();

    @Override
    public ArrayList<MultipleItemEntity> convert() {

        ArrayList<MultipleItemEntity> dataList = new ArrayList<>();

        final int status = JSON.parseObject(getJsonData()).getInteger("status");
        switch (status){
            case 0:
                final int pageNum  = JSON.parseObject(getJsonData()).getJSONObject("data").getInteger("pageNum");
                final JSONArray listArray = JSON.parseObject(getJsonData()).getJSONObject("data").getJSONArray("list");
                final int size = listArray.size();
                if (size == 0){
                    int type = 0;
                    type = ItemType.PROMO_PUBLISH_NO;
                    final MultipleItemEntity entity = MultipleItemEntity.builder()
                            .setField(MultipleFields.ITEM_TYPE, type)
                            .build();
                    dataList.add(entity);
                }else {
                    if (pageNum == 1) {
                        mPromoId.clear();
                    }
                    for (int i = 0; i < size; i++) {
                        final JSONObject data = listArray.getJSONObject(i);

                        final int id = data.getInteger("id");
                        mPromoId.add(id);
                        final String mainImage = data.getString("mainImage");
                        final String categoryName = data.getString("categoryName");
                        final String publisherName = data.getString("publisherName");
                        final String promoName = data.getString("name");
                        final Double price = data.getDouble("price");
                        final int statusId = data.getInteger("statusId");
                        final String statusName = data.getString("statusName");
                        final String publishTime = data.getString("createTime");

                        final int countryId = data.getInteger("countryId");
                        final String countryImg = data.getString("countryFlag");
                        final String countryName = data.getString("countryName");

                        int type = 0;

                        type = ItemType.INDEX_ITEM;

                        final MultipleItemEntity entity = MultipleItemEntity.builder()
                                .setField(MultipleFields.ITEM_TYPE, type)
                                .setField(MultipleFields.ID, id)
                                .setField(MultipleFields.IMAGE_URL, mainImage)
                                .setField(MultipleFields.CATEGORY, categoryName)
                                .setField(MultipleFields.PUBLISHER, publisherName)
                                .setField(MultipleFields.PROMOTION, promoName)
                                .setField(MultipleFields.PRICE, price)
                                .setField(MultipleFields.PROMO_STATUS_ID, statusId)
                                .setField(MultipleFields.PROMO_STATUS, statusName)
                                .setField(MultipleFields.CREATE_TIME, publishTime)
                                .setField(MultipleFields.COUNTRY_ID, countryId)
                                .setField(MultipleFields.COUNTRY_IMG, countryImg)
                                .setField(MultipleFields.COUNTRY_NAME, countryName)
//                            .setField(MultipleFields.STATUS, statusName)
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
