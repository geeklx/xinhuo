package com.geek.appindex.addrecycleview.fragment2;

import android.graphics.drawable.PictureDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.geek.appindex.R;
import com.geek.biz1.bean.BjyyActFragment251Bean;
import com.geek.libglide47.base.GlideImageView;
import com.geek.libglide47.base.ShapeImageView;
import com.geek.libglide47.base.svg.SvgSoftwareLayerSetter;

public class BjyyActFragment2Style5Provider extends BaseItemProvider<BjyyActFragment251Bean, BaseViewHolder> {

    private String currentPos = "-10000";
    private int visible = View.VISIBLE;

    public void setCurrentPos(String currentPos, int visible) {
        this.currentPos = currentPos;
        this.visible = visible;
    }

    @Override
    public int viewType() {
        return BjyyActFragment251Adapter.STYLE_FIVE;
    }

    @Override
    public int layout() {
        return R.layout.activity_bjyyact_item;
    }

    @Override
    public void convert(BaseViewHolder helper, BjyyActFragment251Bean data, int position) {
        GlideImageView iv1 = helper.itemView.findViewById(R.id.iv1);
//        ImageView iv1 = helper.itemView.findViewById(R.id.iv1);
        TextView tv1 = helper.itemView.findViewById(R.id.tv1);
        TextView tv2 = helper.itemView.findViewById(R.id.tv2);
        tv2.setBackgroundResource(R.drawable.icon_jiahao);
//        Glide.with(helper.itemView)
//                .load(data.getmBean().getImg())
//                .placeholder(R.drawable.icon_com_default1)
//                .into(iv1);
//        iv1.setShapeType(ShapeImageView.ShapeType.RECTANGLE);
//        iv1.setRadius(6);
        if (data.getmBean().getImg().contains(".svg")) {
//            iv1.setShapeType(ShapeImageView.ShapeType.NONE);
            Glide.with(helper.itemView.getContext()).as(PictureDrawable.class)
//                    .apply(options)
//                    .placeholder(R.drawable.icon_com_default1)
//                    .error(R.drawable.icon_com_default1)
//                    .transition(withCrossFade())
                    .listener(new SvgSoftwareLayerSetter())
                    .load(data.getmBean().getImg()).into(iv1);
//                    .load("http://www.clker.com/cliparts/u/Z/2/b/a/6/android-toy-h.svg").placeholder(R.drawable.icon_com_default1).into(iv1);
        } else {
            iv1.setShapeType(ShapeImageView.ShapeType.RECTANGLE);
            iv1.setRadius(6);
            iv1.loadImage(data.getmBean().getImg(), R.drawable.icon_com_default1);
//            RequestOptions options = new RequestOptions()
//                    .placeholder(R.drawable.icon_com_default1)
//                    .error(R.drawable.icon_com_default1)
//                    .fallback(R.drawable.icon_com_default1); //url为空的时候,显示的图片;
//            Glide.with(helper.itemView.getContext()).load(data.getmBean().getImg()).apply(options).into(iv1);
        }
        tv1.setText(data.getmBean().getName());
        if (!data.getmBean().isEnable()) {
            if (TextUtils.isEmpty(data.getmBean().getImg())) {
                tv2.setVisibility(View.GONE);
                iv1.setVisibility(View.GONE);
            } else {
                tv2.setVisibility(View.VISIBLE);
                iv1.setVisibility(View.VISIBLE);
            }
        } else {
            tv2.setVisibility(View.GONE);
            iv1.setVisibility(View.VISIBLE);
        }
//        if (TextUtils.equals(currentPos, data.getmBean().getId())) {
////            ToastUtils.showLong(currentPos + "," + data.getmBean().getId());
//            tv2.setVisibility(visible);
//        }
        helper.addOnClickListener(R.id.iv1);
        helper.addOnClickListener(R.id.tv2);
    }

//    @Override
//    public void onClick(BaseViewHolder helper, BjyyActFragment251Bean data, int position) {
//        if (helper.getChildClickViewIds().contains(R.id.iv)) {
//            Toasty.normal(BaseApp.get(), position + "item click=" + data.getmBean().getUserAvatar()).show();
//        } else if (helper.getChildClickViewIds().contains(R.id.tv)) {
//            Toasty.normal(BaseApp.get(), position + "item click=" + data.getmBean().getUserName()).show();
//        } else {
//        }
//
//    }

//    @Override
//    public boolean onLongClick(BaseViewHolder helper, BjyyActFragment251Bean data, int position) {
//        Toast.makeText(mContext, "longClick", Toast.LENGTH_SHORT).show();
//        return true;
//    }
}
