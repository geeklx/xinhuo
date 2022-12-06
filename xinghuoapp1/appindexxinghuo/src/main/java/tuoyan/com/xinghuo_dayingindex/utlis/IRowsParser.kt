package tuoyan.com.xinghuo_dayingindex.utlis

import tuoyan.com.xinghuo_dayingindex.bean.LrcRow

interface IRowsParser {
    /**
     * @param lrcData        歌词字符串数据
     * @return 解析后的歌词数据
     */
    fun parse(lrcData: String): List<LrcRow>
}
