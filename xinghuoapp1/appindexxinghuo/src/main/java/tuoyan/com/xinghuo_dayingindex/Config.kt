package tuoyan.com.xinghuo_dayingindex

/**
 * 创建者：
 * 时间：  2018/6/19.
 */
//从那个商品进入客服,用pc的地址
const val WEB_BASE_URL_PC = "https://www.sparke.cn/"
//const val WEB_BASE_URL_PC = "http://pcdev.sparke.cn/"

// MARK: 涉及jenkins选择集成环境，可更换行的注释，不要删除
const val BASE_URL = "https://api2.sparke.cn/"
//const val BASE_URL = "https://apidev.sparke.cn/"
//const val BASE_URL = "https://apitest.sparke.cn/"
//const val BASE_URL = "http://10.20.0.195:8080/"
//const val BASE_URL = "http://10.20.0.104:8083/"

const val WEB_BASE_URL = "https://m.sparke.cn/"
//const val WEB_BASE_URL = "https://mdev.sparke.cn/"
//const val WEB_BASE_URL = "https://mtest.sparke.cn/"
//const val WEB_BASE_URL = "http://10.20.0.66:4000/"

//神策测试
//const val SA_SERVER_URL = "https://datasink.sparke.cn/sa?project=default"
//const val SA_SERVER_SCHEME = "sa95a5a37d"
//神策正式
const val SA_SERVER_URL = "https://datasink.sparke.cn/sa?project=production"
const val SA_SERVER_SCHEME = "sad0d4d2c5"

//神策弹窗地址
const val SA_FOCUS_URL = "https://sparke.sfn-tx-shanghai-01.saas.sensorsdata.cn/api/v2"

const val UMENG_APP_KEY = "5785f5ae67e58eb942000341"
const val UMENG_MESSAGE_SECRET = "0984a59086e9bd77479454751f6eb3ca"

const val appId = "210951669544977408" //223543530112693760
const val servicePhone = "4008320009"

const val DOWNLOAD_PATH = "星火英语" //如果要修改，filepath中的目录名也要改

const val GRAD_KEY_CET4 = "532648787350438272"
const val GRAD_KEY_CET6 = "532648976328999296"
const val GRAD_KEY_YAN = "532649680703635840"
const val GRAD_KEY_TEM4 = "532649491725076608"
const val GRAD_KEY_TEM8 = "532649586214355328"

const val GRAD_KEY_RECOMMED = "532649586214999999"
const val GRAD_KEY_CET4_CET6 = "532649586214355328"
const val GRAD_KEY_TEM4_TEM8 = "532649491725076608"

const val UDESK_DOMAIN = "sparke.udesk.cn"
const val UDESK_KEY = "2463f483be5af71206ebfd95709e8760"
const val UDESK_ID = "b4a65189d090bb62"

const val SEND_WEB_TOKEN = "send_web_token"

const val DNS_APP_KEY = "0I000QBSR53A8XOG"
const val DNS_ID = "5439"
const val DNS_KEY = "Jrs9KAcu"
const val MEDIA_HOST = "vod.sparke.cn"

//微信背单词小程序
const val WX_APPID = "wx35bdc73a79e2a430"
const val WX_PROGRAM_ID = "gh_3ae78e7839cc"

//虐耳精听小程序
const val WX_PROGRAM_ID_LISTEN = "gh_d130a7da2d28"
//网课小程序 原始id
const val WX_PROGRAM_ID_SPARKE = "gh_e874065f7ff7"

//意见反馈
const val YJFK = "890182678493021247"//异常问题
const val YHJY = "890182678493021260"//优化建议

//对应到DownloadBean中的资源类型，同步修改；以后逐渐用此处的类型，
//1:试卷,2:试卷解析,3:视频,4:图片,5:图文,6:文档,7:音频,8:链接,10:测评,11:资讯
const val TYPE_EX = "1" //做题
const val TYPE_PARS = "2" //题目解析
const val TYPE_VIDEO = "3" //视频
const val TYPE_IMG = "4" //图片
const val TYPE_CONTENT = "5" //富文本
const val TYPE_PDF = "6" //pdf
const val TYPE_AUDIO = "7" //音频
const val TYPE_LINK = "8" //外链
const val TYPE_TEST = "10" //测评
const val TYPE_NEWS = "11" //资讯







