package tuoyan.com.xinghuo_dayingindex.ui.practice.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BaseAdapter
import tuoyan.com.xinghuo_dayingindex.base.ViewHolder
import tuoyan.com.xinghuo_dayingindex.bean.RankingBean

class PracticeRankAdapter : BaseAdapter<RankingBean>() {
    override fun convert(holder: ViewHolder, item: RankingBean) {
        if (item.sort == "1") {
            holder.setVisible(R.id.img_rank_num, View.VISIBLE)
                .setImageResource(R.id.img_rank_num, R.mipmap.icon_rank_1)
                .setVisible(R.id.tv_rank_num, View.GONE)
        } else if (item.sort == "2") {
            holder.setVisible(R.id.img_rank_num, View.VISIBLE)
                .setImageResource(R.id.img_rank_num, R.mipmap.icon_rank_2)
                .setVisible(R.id.tv_rank_num, View.GONE)
        } else if (item.sort == "3") {
            holder.setVisible(R.id.img_rank_num, View.VISIBLE)
                .setImageResource(R.id.img_rank_num, R.mipmap.icon_rank_3)
                .setVisible(R.id.tv_rank_num, View.GONE)
        } else {
            holder.setVisible(R.id.img_rank_num, View.GONE)
                .setImageResource(R.id.img_rank_num, R.mipmap.icon_rank_1)
                .setVisible(R.id.tv_rank_num, View.VISIBLE)
                .setText(R.id.tv_rank_num, item.sort)
        }
        holder.setText(R.id.tv_rank_name, item.name)
        holder.setText(R.id.tv_rank_score, item.score)
    }

    override fun convertView(context: Context, parent: ViewGroup): View =
        LayoutInflater.from(context).inflate(R.layout.item_practice_rank, null)

}