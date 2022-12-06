package tuoyan.com.xinghuo_dayingindex.utlis;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import tuoyan.com.xinghuo_dayingindex.bean.LrcRow;

public class MyLrcDataBuilder {
    private String tag = "LrcDataBuilder";

    public MyLrcDataBuilder() {
    }

    public List<LrcRow> BuiltFromAssets(Context context, String fileName, IRowsParser parser) {
        return this.Build(this.LoadContentFromAssets(context, fileName), parser);
    }

    public List<LrcRow> Build(File lrcFile, IRowsParser parser) {
        return this.Build(this.LoadContentFromFile(lrcFile), parser);
    }

//    public List<LrcRow> BuiltFromAssets(Context context, String fileName) {
//        return this.BuiltFromAssets(context, fileName, new DefaultLrcRowsParser());
//    }

//    public List<LrcRow> Build(File lrcFile) {
//        return this.Build((File)lrcFile, new DefaultLrcRowsParser());
//    }

    public List<LrcRow> Build(String rawLrcStrData, IRowsParser parser) {
        if (TextUtils.isEmpty(rawLrcStrData)) {
            Log.e(this.tag, " lrcFile do not exist");
            return null;
        } else {
            StringReader reader = new StringReader(rawLrcStrData);
            BufferedReader br = new BufferedReader(reader);
            ArrayList rows = new ArrayList();

            Iterator var8;
            try {
                String line;
                do {
                    line = br.readLine();
                    if (line != null && line.trim().length() > 0) {
                        List<LrcRow> lrcRows = parser.parse(line);
                        if (lrcRows != null && lrcRows.size() > 0) {
                            var8 = lrcRows.iterator();

                            while (var8.hasNext()) {
                                LrcRow row = (LrcRow) var8.next();
                                rows.add(row);
                            }
                        }
                    }
                } while (line != null);

                if (rows.size() > 0) {
                    Collections.sort(rows);
                } else {
                    Log.d(this.tag, "rows.size() ==0:");
                }

                return rows;
            } catch (Exception var18) {
                Log.e(this.tag, "歌词解析异常:" + var18.getMessage());
                var8 = null;
            } finally {
                try {
                    br.close();
                } catch (IOException var17) {
                    var17.printStackTrace();
                }

                reader.close();
            }

            return rows;
        }
    }

    private String LoadContentFromFile(File file) {
        try {
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file), "GB2312");
            BufferedReader bufReader = new BufferedReader(inputReader);
            String result = "";

            String line;
            while ((line = bufReader.readLine()) != null) {
                if (!line.trim().equals("")) {
                    result = result + line + "\r\n";
                }
            }

            return result;
        } catch (Exception var6) {
            var6.printStackTrace();
            return null;
        }
    }

    private String LoadContentFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String result = "";

            String line;
            while ((line = bufReader.readLine()) != null) {
                if (!line.trim().equals("")) {
                    result = result + line + "\r\n";
                }
            }

            return result;
        } catch (Exception var7) {
            var7.printStackTrace();
            return "";
        }
    }
}
