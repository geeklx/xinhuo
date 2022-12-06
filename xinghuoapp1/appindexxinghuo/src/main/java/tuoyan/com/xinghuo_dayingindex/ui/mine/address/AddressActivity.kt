//package tuoyan.com.xinghuo_daying.ui.mine.address
//
//import androidx.recyclerview.widget.DividerItemDecoration
//import androidx.recyclerview.widget.LinearLayoutManager
//import android.view.View
//import android.widget.LinearLayout
//import kotlinx.android.synthetic.main.activity_address.*
//import org.jetbrains.anko.ctx
//import org.jetbrains.anko.startActivity
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
//import tuoyan.com.xinghuo_dayingindex.ui.mine.address.add.AddAddressActivity
//
//class AddressActivity : LifeActivity<AddressPresenter>() {
//    override val presenter = AddressPresenter(this)
//    override val layoutResId = R.layout.activity_address
//    private val adapter by lazy {
//        AddressAdapter {
//            startActivity<AddAddressActivity>(AddAddressActivity.DATA to it)
//        }
//    }
//
//    override fun configView() {
//        recycler_view.layoutManager = LinearLayoutManager(ctx)
//        val decoration = DividerItemDecoration(ctx, LinearLayout.VERTICAL)
//        decoration.setDrawable(resources.getDrawable(R.drawable.divider))
//        recycler_view.addItemDecoration(decoration)
//        recycler_view.adapter = adapter
//    }
//
//    override fun initData() {
////        val of = mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
////        adapter.setData(of)
////        presenter.addresses {
////            adapter.setData(it.body)
////        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        presenter.addresses {
//            adapter.setData(it.body)
//        }
//    }
//
//    fun add(v: View) {
//        startActivity<AddAddressActivity>(AddAddressActivity.ADD to true)
//    }
//}
