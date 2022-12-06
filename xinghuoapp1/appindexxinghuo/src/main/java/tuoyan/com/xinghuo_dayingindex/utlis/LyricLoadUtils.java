//
//package tuoyan.com.xinghuo_daying.utlis;
//
//import android.annotation.SuppressLint;
//import android.util.Log;
//
//import com.spark.peak.bean.Lyric;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//
//
//public class LyricLoadUtils {
//
//    public interface LyricListener {
//
//        void onLyricSentenceChanged(int indexOfCurSentence);
//    }
//
//    private static final String TAG = LyricLoadUtils.class.getSimpleName();
//
//    /**
//     * 句子集合
//     */
//    private List<Lyric> mLyric = new ArrayList<Lyric>();
//
//    private LyricListener mLyricListener = null;
//
//    private boolean mHasLyric = false;
//
//    private int mIndexOfCurrentSentence = -1;
//
//    private final Pattern mBracketPattern = Pattern
//            .compile("(?<=\\[).*?(?=\\])");
//    private final Pattern mTimePattern = Pattern
//            .compile("(?<=\\[)(\\d{2}:\\d{2}\\.?\\d{0,3})(?=\\])");
//
//    private final String mEncoding = "utf-8";
//
//    public void setLyricListener(LyricListener listener) {
//        this.mLyricListener = listener;
//    }
//
//    public void setmLyric(List<Lyric> mLyric) {
//        this.mLyric = mLyric;
//    }
//
//
//    /**
//     * 根据传递过来的已播放的毫秒数，计算应当对应到句子集合中的哪一句，再通知监听者播放到的位置。
//     *
//     * @param millisecond 已播放的毫秒数
//     */
//    public void notifyTime(long millisecond) {
//        Log.i(TAG, "notifyTime");
//        if (mLyric != null && mLyric.size() != 0) {
//            Log.i(TAG, "1111111111");
//            int newLyricIndex = seekSentenceIndex(millisecond);
//
//            if (newLyricIndex != -1 && newLyricIndex != mIndexOfCurrentSentence) {// 如果找到的歌词和现在的不是一句。
//                Log.i(TAG, "222222222");
//                if (mLyricListener != null) {
//                    Log.i(TAG, "3333333333");
//                    // 告诉一声，歌词已经变成另外一句啦！
//                    mLyricListener.onLyricSentenceChanged(newLyricIndex);
//                }
//                mIndexOfCurrentSentence = newLyricIndex;
//            }
//        }
//    }
//
//    private int seekSentenceIndex(long millisecond) {
//        int findStart = 0;
//
////        if (mIndexOfCurrentSentence >= 0) {
////            // 如果已经指定了歌词，则现在位置开始
////            findStart = mIndexOfCurrentSentence;
////        }
//
//        try {
//            double lyricTime = mLyric.get(findStart).startTime;
//
//            if (millisecond > lyricTime) { // 如果想要查找的时间在现在字幕的时间之后
//                // 如果开始位置经是最后一句了，直接返回最后一句。
//                if (findStart == (mLyric.size() - 1)) {
//                    return findStart;
//                }
//                int new_index = findStart + 1;
//                // 找到第一句开始时间大于输入时间的歌词
//                while (new_index < mLyric.size()
//                        && mLyric.get(new_index).startTime <= millisecond) {
//                    ++new_index;
//                }
//                // 这句歌词的前一句就是我们要找的了。
//                return new_index - 1;
//            } else if (millisecond < lyricTime) { // 如果想要查找的时间在现在字幕的时间之前
//                // 如果开始位置经是第一句了，直接返回第一句。
//                if (findStart == 0)
//                    return 0;
//
//                int new_index = findStart - 1;
//                // 找到开始时间小于输入时间的歌词
//                while (new_index > 0
//                        && mLyric.get(new_index).startTime > millisecond) {
//                    --new_index;
//                }
//                // 就是它了。
//                return new_index;
//            } else {
//                // 不用找了
//                return findStart;
//            }
//        } catch (IndexOutOfBoundsException e) {
//            Log.i(TAG, "seekSentenceIndex: ");
//            Log.i(TAG, "新的歌词载入了，所以产生了越界错误，不用理会，返回0");
//            return 0;
//        }
//    }
//
//    /**
//     * 去除指定字符串中包含[XXX]形式的字符串
//     */
//    private String trimBracket(String content) {
//        String s = null;
//        String result = content;
//        Matcher matcher = mBracketPattern.matcher(content);
//        while (matcher.find()) {
//            s = matcher.group();
//            result = result.replace("[" + s + "]", "");
//        }
//        return result;
//    }
//
//    /**
//     * 将歌词的时间字符串转化成毫秒数，如果参数是00:01:23.45
//     */
//    @SuppressLint("DefaultLocale")
//    private long parseTime(String strTime) {
//        String beforeDot = new String("00:00:00");
//        String afterDot = new String("0");
//
//        // 将字符串按小数点拆分成整秒部分和小数部分。
//        int dotIndex = strTime.indexOf(".");
//        if (dotIndex < 0) {
//            beforeDot = strTime;
//        } else if (dotIndex == 0) {
//            afterDot = strTime.substring(1);
//        } else {
//            beforeDot = strTime.substring(0, dotIndex);// 00:01:23
//            afterDot = strTime.substring(dotIndex + 1); // 45
//        }
//
//        long intSeconds = 0;
//        int counter = 0;
//        while (beforeDot.length() > 0) {
//            int colonPos = beforeDot.indexOf(":");
//            try {
//                if (colonPos > 0) {// 找到冒号了。
//                    intSeconds *= 60;
//                    intSeconds += Integer.valueOf(beforeDot.substring(0,
//                            colonPos));
//                    beforeDot = beforeDot.substring(colonPos + 1);
//                } else if (colonPos < 0) {// 没找到，剩下都当一个数处理了。
//                    intSeconds *= 60;
//                    intSeconds += Integer.valueOf(beforeDot);
//                    beforeDot = "";
//                } else {// 第一个就是冒号，不可能！
//                    return -1;
//                }
//            } catch (NumberFormatException e) {
//                return -1;
//            }
//            ++counter;
//            if (counter > 3) {// 不会超过小时，分，秒吧。
//                return -1;
//            }
//        }
//        // intSeconds=83
//
//        String totalTime = String.format("%d.%s", intSeconds, afterDot);// totaoTimer
//        // =
//        // "83.45"
//        Double doubleSeconds = Double.valueOf(totalTime); // 转成小数83.45
//        return (long) (doubleSeconds * 1000);// 转成毫秒8345
//    }
//
//
//    public List<Lyric> loadLyricForHttp(InputStream inputStream) throws IOException {
//        String Lrc_data = "";
//        Lyric lyric = new Lyric();
//        List<Lyric> lyrics = new ArrayList<>();
//        InputStreamReader mInputStreamReader = new InputStreamReader(inputStream, "GB2312");
//        BufferedReader mBufferedReader = new BufferedReader(mInputStreamReader);
//        while ((Lrc_data = mBufferedReader.readLine()) != null) {
//            Lrc_data = Lrc_data.replace("[", "");
//            Lrc_data = Lrc_data.replace("]", "@");
//            String splitLrc_data[] = Lrc_data.split("@");
//            if (splitLrc_data.length > 1) {
//                lyric.lyricText = splitLrc_data[1];
//                int LyricTime = TimeStr(splitLrc_data[0]);
//                lyric.startTime = LyricTime;
//                lyrics.add(lyric);
//                lyric = new Lyric();
//            }
//        }
//        mBufferedReader.close();
//        mInputStreamReader.close();
//        return lyrics;
//    }
//
//    public static int TimeStr(String timeStr) {
//        timeStr = timeStr.replace(":", ".");
//        timeStr = timeStr.replace(".", "@");
//        String timeData[] = timeStr.split("@");
//        int minute = Integer.parseInt(timeData[0]);
//        int second = Integer.parseInt(timeData[1]);
//        int millisecond = Integer.parseInt(timeData[2]);
//        int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
//        return currentTime;
//    }
//
//}
