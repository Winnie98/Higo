package com.yhl.higo.ec.main.index;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.ui.recycler.DataConverter;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/5/26/026.
 */

public class IndexImageDataConverter extends DataConverter {
    @Override
    public ArrayList<MultipleItemEntity> convert() {

        final String subImages = JSON.parseObject(getJsonData()).getJSONObject("data").getString("subImages");
//        final List<String> images = new ArrayList<>();
        String[] strArray = subImages.split(",");
        final int size = strArray.length;
        for (int i = 0; i < size; i++) {
//            images.add("http://img.higo.party/"+strArray[i]);
            final MultipleItemEntity entity = MultipleItemEntity.builder()
                    .setItemType(ItemType.IMAGE)
                    .setField(MultipleFields.IMAGE_URL,"http://img.higo.party/"+strArray[i])
                    .build();
            ENTITIES.add(entity);
        }

        return ENTITIES;
    }

    public ArrayList<MultipleItemEntity> convert(String subImages) {

//        final String subImages = JSON.parseObject(getJsonData()).getJSONObject("data").getString("subImages");
//        final List<String> images = new ArrayList<>();
        String[] strArray = subImages.split(",");
        final int size = strArray.length;
        for (int i = 0; i < size; i++) {
//            images.add("http://img.higo.party/"+strArray[i]);
            final MultipleItemEntity entity = MultipleItemEntity.builder()
                    .setItemType(ItemType.IMAGE)
                    .setField(MultipleFields.SINGLE_IMAGE,strArray[i])
                    .setField(MultipleFields.IMAGE_URL,"http://img.higo.party/"+strArray[i])
                    .build();
            ENTITIES.add(entity);
        }

        return ENTITIES;
    }
}
