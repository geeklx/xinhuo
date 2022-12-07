package com.spark.peak.utlis;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hw.lrcviewlib.DefaultLrcRowsParser;
import com.hw.lrcviewlib.IRowsParser;
import com.hw.lrcviewlib.LrcRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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

    public List<LrcRow> Build(File lrcFile, IRowsParser parser, String charsetName) {
        return this.Build(this.LoadContentFromFile(lrcFile, charsetName), parser);
    }

    public List<LrcRow> BuiltFromAssets(Context context, String fileName) {
        return this.BuiltFromAssets(context, fileName, new DefaultLrcRowsParser());
    }

    public List<LrcRow> Build(File lrcFile) {
        return this.Build((File) lrcFile, new DefaultLrcRowsParser());
    }

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
//                        List<LrcRow> lrcRows = parser.parse(line.replace("<br>", " "));
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
                //todo 删除歌词文件
                File lrcFile = new File(rawLrcStrData);
                lrcFile.delete();
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
        return LoadContentFromFile(file, charsetDetect(file));
    }

    private String LoadContentFromFile(File file, String charsetName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file), charsetName);
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

    public String charsetDetect(File file) {
        String _charset = "GBK";
        try {
            InputStream fs = new FileInputStream(file);
            byte[] buffer = new byte[3];
            fs.read(buffer);
            fs.close();

            if (buffer[0] == -17 && buffer[1] == -69 && buffer[2] == -65)
                _charset = "UTF-8";
            else
                _charset = "GBK";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return _charset;
    }
}
