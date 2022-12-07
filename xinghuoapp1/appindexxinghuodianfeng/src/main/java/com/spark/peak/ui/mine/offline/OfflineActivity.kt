package com.spark.peak.ui.mine.offline

import androidx.recyclerview.widget.LinearLayoutManager
import com.spark.peak.R
import com.spark.peak.base.BaseActivity
import com.spark.peak.bean.BookRes
import com.spark.peak.utlis.DownloadManager
import kotlinx.android.synthetic.main.activity_offlinedf.*
import org.jetbrains.anko.startActivity

class OfflineActivity(override val layoutResId: Int = R.layout.activity_offlinedf) : BaseActivity() {
    private val adapter = OfflineAdapter { type, item ->
        startActivity<OfflineResActivity>(
            OfflineResActivity.NAME to item.name,
            OfflineResActivity.TYPE to type
        )
    }

    override fun configView() {
        recycler_offline.layoutManager = LinearLayoutManager(this)
        recycler_offline.adapter = adapter
    }

    override fun handleEvent() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        rg_down.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_book -> {
                    adapter.type = "pt"
                    getData("pt")
                }
                R.id.rb_lesson -> {
                    adapter.type = "wk"
                    getData("wk")
                }
            }
        }
    }

    override fun initData() {
        adapter.type = "pt"
//        getData("pt")
    }

    override fun onResume() {
        super.onResume()
        getData(adapter.type)
    }

    /**
     * @param type pt(配套) wk（网课）
     */
    private fun getData(type: String) {
        val data = mutableListOf<BookRes>()
        val list = DownloadManager.getFileByType(type)
        list.forEach {
            if (!it.delete())
                data.add(
                    BookRes(
                        it.name,
                        it.list().size.toString(),
                        ShowLongFileSzie(it.length())
                    )
                )
        }
        adapter.setData(data)
    }

    private fun ShowLongFileSzie(length: Long): String {
        return if (length >= 1048576) {
            (length / 1048576).toString() + "MB"
        } else if (length >= 1024) {
            (length / 1024).toString() + "KB"
        } else if (length < 1024) {
            length.toString() + "B"
        } else {
            "0KB"
        }
    }
}
