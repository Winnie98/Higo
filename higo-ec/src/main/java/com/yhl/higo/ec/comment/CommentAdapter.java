package com.yhl.higo.ec.comment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.app.Higo;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.main.personal.profile.OthersUserInfoDelegate;
import com.yhl.higo.ec.sign.SignInCodeDelegate;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.image.GlideApp;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.ui.recycler.MultipleRecyclerAdapter;
import com.yhl.higo.ui.recycler.MultipleViewHolder;
import com.yhl.higo.util.log.HigoLogger;

import java.util.List;

/**
 * Created by Administrator on 2018/5/26/026.
 */

public class CommentAdapter extends MultipleRecyclerAdapter{
    AlertDialog mDialog;
    private String mUsername;
    private String mAvatar;
    private Double mScore;
    private Double mSellCount;
    private String mImageHost;

    private AlertDialog.Builder builder=null;
    private AlertDialog alert;
    private final HigoDelegate DELEGATE;
    private PromoCommentDataConverter  mDataConverter = new PromoCommentDataConverter();

    protected CommentAdapter(List<MultipleItemEntity> data, HigoDelegate delegate) {
        super(data);
        DELEGATE = delegate;
        addItemType(ItemType.COMMENT_PROMO, R.layout.item_prommo_comment);
    }


    @Override
    protected void convert(final MultipleViewHolder holder, MultipleItemEntity entity) {
        super.convert(holder, entity);
        switch (holder.getItemViewType()) {
                case ItemType.COMMENT_PROMO:
                final int id = entity.getField(MultipleFields.ID);
                final int userId = entity.getField(MultipleFields.USER_ID);
                final String avatar = entity.getField(MultipleFields.AVATAR);
                final String name = entity.getField(MultipleFields.USER_NAME);
//                final Date time = entity.getField(MultipleFields.CREATE_TIME);
                final String time = entity.getField(MultipleFields.CREATE_TIME);
                final String detail = entity.getField(MultipleFields.CONTENT);


                final AppCompatImageView imgAvatar = holder.getView(R.id.tv_item_comment_user_avatar);
                final AppCompatTextView nameText = holder.getView(R.id.tv_item_comment_user);
                final AppCompatTextView timeText = holder.getView(R.id.tv_item_comment_time);
                final AppCompatTextView detailText = holder.getView(R.id.tv_item_comment_detail);

                 GlideApp.with(mContext)
                         .load("http://img.higo.party/"+avatar)
                         .into(imgAvatar);

                nameText.setText(name);
                timeText.setText(time);
//                timeText.setText(String.valueOf(time));
                detailText.setText(detail);

                imgAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final OthersUserInfoDelegate delegate  = OthersUserInfoDelegate.create(userId);
                        DELEGATE.getSupportDelegate().start(delegate);
                    }
                });

                break;
            default:
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();

                final int commentId = mDataConverter.mCommentId.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(DELEGATE.getContext());
                dialog.setMessage("确认要删除该条评论吗")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                RestClient.builder()
                                        .url("user/comment/delete_comment.do")
                                        .params("commentId",commentId)
                                        .success(new ISuccess() {
                                            @Override
                                            public void onSuccess(String response) {
                                                HigoLogger.d("DELETE_ADDRESS",response);
                                                final int status = JSON.parseObject(response).getInteger("status");
                                                final String msg = JSON.parseObject(response).getString("msg");
                                                switch (status){
                                                    case 0:
                                                        Toast.makeText(Higo.getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                                        remove(holder.getLayoutPosition());
                                                        break;
                                                    case 3:
                                                        Toast.makeText(Higo.getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                                        DELEGATE.getSupportDelegate().start(new SignInCodeDelegate());
                                                        break;
                                                    default:
                                                        Toast.makeText(Higo.getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                                        break;
                                                }
                                            }
                                        })
                                        .build()
                                        .post();

                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();


            }
        });
    }

}
