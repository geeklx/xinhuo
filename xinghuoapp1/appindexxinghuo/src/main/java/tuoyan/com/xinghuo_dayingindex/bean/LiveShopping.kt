package tuoyan.com.xinghuo_dayingindex.bean

class LiveShopping {
    //    {
//        "img": "https://imgtest.sparke.cn/images/english/2022/3/1491363467293682816.jpg",
//        "disprice": "0.01",
//        "sort": "1",
//        "title": "考研测试课_激活",
//        "key": "1534419818651448896"
//    }
    var msg=""
    var list :ArrayList<LiveShoppingList>? = null
    var liveId = ""
    var liveManagementKey = ""
}
class LiveShoppingList {
    //    {
//        "img": "https://imgtest.sparke.cn/images/english/2022/3/1491363467293682816.jpg",
//        "disprice": "0.01",
//        "sort": "1",
//        "title": "考研测试课_激活",
//        "key": "1534419818651448896"
//    }
    var img = ""
    var disprice = ""
    var sort = ""
    var title = ""
    var key = ""
    var isOwn = "0"

}
class LivePop : LiveBackPop() {

    var list:ArrayList<Coupon>? = null

}
open class LiveBackPop{
    var showTime=""
    var name = ""
    var coverUrl = ""
    var link = ""
    var shoppingKey = ""
    var promotionalList:ArrayList<Coupon>? = null
    var shoppingType=""
    var use=true
}
