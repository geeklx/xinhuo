package com.geek.appindex.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.geek.appcommon.AppCommonUtils;
import com.geek.appindex.R;
import com.geek.biz1.bean.BjyyBeanYewu3;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Fenlei4Adapter extends RecyclerView.Adapter<Fenlei4Adapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<BjyyBeanYewu3> mratings;

    public Fenlei4Adapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        mratings = new ArrayList<BjyyBeanYewu3>();
    }

    public void setContacts(List<BjyyBeanYewu3> ratings) {
        this.mratings = ratings;
    }

    public void addConstacts(List<BjyyBeanYewu3> ratings) {
        this.mratings.addAll(ratings);
    }

    public List<BjyyBeanYewu3> getMratings() {
        return mratings;
    }

    @Override
    public int getItemCount() {
        if (mratings == null) {
            return 0;
        }
        return mratings.size();
    }

    public Object getItem(int position) {
        return mratings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_fenlei4_footer_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.ll1 = view.findViewById(R.id.ll1);
        viewHolder.iv_imgurl = view.findViewById(R.id.iv_imgurl);
        viewHolder.tv_content1 = view.findViewById(R.id.tv_content1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        final BjyyBeanYewu3 ratings = mratings.get(position);
        //设置图片bufen
//        GlideUtil.display(context, viewHolder.iv_imgurl, ratings.getSku_image(), GlideOptionsFactory.get(GlideOptionsFactory.Type.RADIUS));
//        Glide.with(context).load(ratings.getSku_image()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(viewHolder.iv_imgurl);
        if (TextUtils.isEmpty(ratings.getName())) {
            viewHolder.tv_content1.setVisibility(View.GONE);
        } else {
            viewHolder.tv_content1.setVisibility(View.VISIBLE);
        }
        viewHolder.tv_content1.setText(ratings.getName());
//        AppCommonUtils.addSeletorFromNet(ratings.getImg_press(), ratings.getImg_normal(), viewHolder.iv_imgurl);
        if (ratings.isEnable()) {
            //选中
            viewHolder.iv_imgurl.setPressed(true);
            AppCommonUtils.addSeletorFromNet(ratings.getImg_press(), ratings.getImg_normal(), viewHolder.iv_imgurl);
            viewHolder.tv_content1.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            //未选中
            viewHolder.iv_imgurl.setPressed(false);
            AppCommonUtils.addSeletorFromNet(ratings.getImg_normal(), ratings.getImg_press(), viewHolder.iv_imgurl);
            viewHolder.tv_content1.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
        //如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, position);
                }
            });
        }
    }

    private void set_img(BjyyBeanYewu3 ratings, ImageView iv, TextView tv) {
        if (ratings.isEnable()) {
            //选中
            iv.setPressed(true);
            AppCommonUtils.addSeletorFromNet(ratings.getImg_press(), ratings.getImg_normal(), iv);
            tv.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            //未选中
            iv.setPressed(false);
            AppCommonUtils.addSeletorFromNet(ratings.getImg_normal(), ratings.getImg_press(), iv);
            tv.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll1;//
        private LinearLayout ll2;//
        private ImageView iv_imgurl;//ImgUrl
        private ImageView iv_imgurl2;//ImgUrl
        private TextView tv_content1;//

        ViewHolder(View view) {
            super(view);
        }
    }

    /**
     * ItemClick的回调接口
     *
     * @author geek
     */
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public String formatPrice2(double price) {
        DecimalFormat df = new DecimalFormat("######0.00");

        return df.format(price);
    }
}