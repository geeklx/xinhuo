//package tuoyan.com.xinghuo_daying.ui.practice.composition
//
//import androidx.recyclerview.widget.LinearLayoutManager
//import kotlinx.android.synthetic.main.activity_composition_list.*
//import org.jetbrains.anko.startActivity
//import tuoyan.com.xinghuo_dayingindex.R
//import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
//import tuoyan.com.xinghuo_dayingindex.base.LifeActivity
//import tuoyan.com.xinghuo_dayingindex.ui.practice.Composition
//import tuoyan.com.xinghuo_dayingindex.ui.practice.adapter.CompositionListAdapter
//
//class CompositionListActivity : LifeActivity<BasePresenter>() {
//    override val presenter: BasePresenter
//        get() = BasePresenter(this)
//    override val layoutResId: Int
//        get() = R.layout.activity_composition_list
//
//    override fun configView() {
//        super.configView()
//        setSupportActionBar(tb_composition)
//        tb_composition.setNavigationOnClickListener { onBackPressed() }
//        rlv_list.layoutManager = LinearLayoutManager(this)
//        rlv_list.adapter = adapter
//    }
//
//    override fun handleEvent() {
//        super.handleEvent()
//    }
//
//    override fun initData() {
//        super.initData()
//        var dataList = mutableListOf<Composition>()
//        dataList.add(Composition("1", "只"))
//        dataList.add(Composition("1", "只"))
//        dataList.add(Composition("1", "只"))
//        dataList.add(Composition("1", "只"))
//        adapter.setData(dataList)
//    }
//
//    private val adapter by lazy {
//        CompositionListAdapter { position, item ->
//            //未支付；待提交；批改中；已批改；0：未作状态
//            startActivity<CompositionDetailActivity>(CompositionDetailActivity.TITLE to "只能家具")
//        }
//    }
//}