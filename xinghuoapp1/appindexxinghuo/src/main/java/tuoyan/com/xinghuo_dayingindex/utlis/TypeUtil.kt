package tuoyan.com.xinghuo_dayingindex.utlis

/**
 * Created by Zzz on 2021/6/11
 * Email:
 */

/**
 * 资源类型
 */
class TypeUtil {
    companion object {
        /**
         * 资源类型
         */
        fun getType(type: String): String {
            return when (type) {
                "1" -> "普通试卷"
                "2" -> "试卷解析"
                "3" -> "视频"
                "4" -> "图片"
                "5" -> "图文"
                "6" -> "文档"
                "7" -> "音频"
                "8" -> "链接"
                "9" -> "字幕"
                "10" -> "测评"
                "11" -> "资讯"
                "12" -> "模考"
                "13" -> "口语测评"
                else -> type
            }
        }

        /**
         * 题目类型
         */
        fun getQType(type: String): String {
            return when (type) {
                "1" -> "单选题"
                "2" -> "多选题"
                "3" -> "判断题"
                "4" -> "填空题"
                "5" -> "材料题"
                "6" -> "主观题"
                "7" -> "口语题"
                "8" -> "连线题"
                "9" -> "纠错题"
                "10" -> "听力填空题"
                else -> ""
            }
        }

        /**
         * 扫码返回类型
         */
        fun getScanType(type: String): String {
            return when (type) {
                "1" -> "资源列表"
                "2" -> "图书资源"
                "3" -> "网课"
                "3.1" -> "网课营销"
                "4" -> "资讯"
                "5" -> "资讯外链"
                "6" -> "问答提问"
                "7" -> "下载"
                "8" -> "外链*"
                "9" -> "常用词汇"
                "10" -> "测评管理"
                "11" -> "防伪码"
                "12" -> "全真考场"
                "13" -> "教材答案"
                else -> ""
            }
        }
    }
}