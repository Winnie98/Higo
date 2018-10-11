package com.yhl.higo.ec.main.personal.list;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhl.higo.ec.R;

import java.util.List;

/**
 * Created by Administrator on 2018/5/20/020.
 */

public class ListAdapter extends BaseMultiItemQuickAdapter<ListBean,BaseViewHolder> {

    private static final RequestOptions OPTIONS = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .dontAnimate()
            .centerCrop();

    public ListAdapter(List<ListBean> data) {
        super(data);
        addItemType(ListItemType.ITEM_NORMAL, R.layout.arrow_item_layout);
        addItemType(ListItemType.ITEM_AVATAR, R.layout.arrow_item_avatar);
        addItemType(ListItemType.ITEM_TEXT_VALUE,R.layout.item_text_value);
        addItemType(ListItemType.ITEM_BUTTON,R.layout.item_button);
    }

    @Override
    protected void convert(BaseViewHolder helper, ListBean item) {
        switch (helper.getItemViewType()){
            case ListItemType.ITEM_NORMAL:
                helper.setText(R.id.tv_arrow_text,item.getText());
                helper.setText(R.id.tv_arrow_value,item.getValue());
                break;
            case ListItemType.ITEM_AVATAR:
                Glide.with(mContext)
                        .load(item.getImageUrl())
                        .apply(OPTIONS)
                        .into((ImageView) helper.getView(R.id.img_arrow_avatar));
                break;
            case ListItemType.ITEM_TEXT_VALUE:
                helper.setText(R.id.tv_user_text,item.getText());
                helper.setText(R.id.edt_user_value,item.getValue());
                break;
            case ListItemType.ITEM_BUTTON:
                helper.setText(R.id.btn_operation,item.getText());
                break;
            default:
                break;
        }
    }
}
