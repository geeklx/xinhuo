package tuoyan.com.xinghuo_dayingindex.bean

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * 创建者：
 * 时间：  2018/10/29.
 */
/**
 * 订单列表
 */
class Order(
    var img: String,            //商品缩略图
    var title: String,          //商品名称
    var period: String,         //课时
    var status: Int,            //订单状态 订单状态，0 :未付款；1:已付款;;3:已评价;4:已失效
    var goodkey: String,        //商品主键
    var goodtype: String,       //商品类型
    var orderkey: String,       //订单主键
    var price: String,          //原价
    var buyers: String,          //人数
    var realprice: String? = null,      //实际价格
    var originalcost: String,   //原价
    var money: String,          //金额
    var validitytime: String,   //有效期
    var isunsub: Int,           //0:不支持退订
    var coupontitile: String,   //优惠券名称
    var facevalue: String,      //面值
    var discount: String,       //优惠后价格
    var ordernumber: String,    //订单编号
    var createtime: String,     //创建时间
    var paytime: String
) : Serializable    //付款时间

class AssembleTeam() : Parcelable {
    var assembleTeamKey: String = ""
    var assembleLimitNum: Int = 0
    var assembleTeamEndTime: Long = 0
    var assembleState: String = "0"//assembleState   0拼团中  1拼团成功 2拼团失败
    var assembleOrderList: ArrayList<AssembleOrderBean>? = null
    var isAdditiveGroup = "0"//开始一键加群，1是，0否;只要开了，所有付款后走设置加群页面
    var remark = "0"//一键加群说明
    var qrCode = "0"//一键加群二维码

    constructor(parcel: Parcel) : this() {
        assembleTeamKey = parcel.readString()?:""
        assembleLimitNum = parcel.readInt()
        assembleTeamEndTime = parcel.readLong()
        assembleState = parcel.readString()?:""
        isAdditiveGroup = parcel.readString()?:""
        remark = parcel.readString()?:""
        qrCode = parcel.readString()?:""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(assembleTeamKey)
        parcel.writeInt(assembleLimitNum)
        parcel.writeLong(assembleTeamEndTime)
        parcel.writeString(assembleState)
        parcel.writeString(isAdditiveGroup)
        parcel.writeString(remark)
        parcel.writeString(qrCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AssembleTeam> {
        override fun createFromParcel(parcel: Parcel): AssembleTeam {
            return AssembleTeam(parcel)
        }

        override fun newArray(size: Int): Array<AssembleTeam?> {
            return arrayOfNulls(size)
        }
    }
}

class AssembleOrderBean {
    var img: String = ""
    var userKey: String = ""
}

class OrderDetail {
    var orderNumber = ""
    var orderType = ""//订单类型 2. 网课 1.图书
    var goodsList = mutableListOf<Good>()
}

class Good {
    var goodsName = ""
    var goodsKey = ""
    var goodsType = ""
    var privateName = ""
}