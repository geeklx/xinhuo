package com.geek.appindex.addrecycleview;

import android.text.TextUtils;

import com.geek.biz1.bean.BjyyActFragment251Bean2;
import com.geek.biz1.bean.BjyyBeanYewu1;
import com.geek.biz1.bean.BjyyBeanYewu2;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.libutils.data.MmkvUtils;

import java.util.ArrayList;
import java.util.List;

public class BjyyUtils {

    public static BjyyBeanYewu1 choose_data(BjyyBeanYewu1 data1, String id, boolean ismy) {
//        BjyyBeanYewu1 data1 = MmkvUtils.getInstance().get_common_json("BjyyActFragment2", BjyyBeanYewu1.class);
        List<BjyyBeanYewu2> data2 = null;
        List<BjyyBeanYewu3> data3 = null;
        if (data1 != null && data1.getData().size() > 0) {
            data2 = data1.getData();
            for (int i = 0; i < data2.size(); i++) {
                BjyyBeanYewu2 bjyyBeanYewu2 = data2.get(i);
                bjyyBeanYewu2.setId(data2.get(i).getId());
                bjyyBeanYewu2.setImg(data2.get(i).getImg());
                bjyyBeanYewu2.setName(data2.get(i).getName());
                bjyyBeanYewu2.setUrl(data2.get(i).getUrl());
                bjyyBeanYewu2.setEnable(data2.get(i).isEnable());
                data3 = data2.get(i).getData();
                for (int j = 0; j < data3.size(); j++) {
                    if (TextUtils.equals(id, data3.get(j).getId())) {
                        BjyyBeanYewu3 bjyyBeanYewu3 = data3.get(j);
                        bjyyBeanYewu3.setId(data3.get(j).getId());
                        bjyyBeanYewu3.setImg(data3.get(j).getImg());
                        bjyyBeanYewu3.setName(data3.get(j).getName());
                        bjyyBeanYewu3.setUrl(data3.get(j).getUrl());
                        bjyyBeanYewu3.setEnable(ismy);
                        bjyyBeanYewu2.setData(data3);
                        data1.setData(data2);
                    }
                }
            }
        }
        return data1;
    }

    public static BjyyBeanYewu1 get_data_other(String id, boolean ismy) {
        BjyyBeanYewu1 data1 = MmkvUtils.getInstance().get_common_json("BjyyActFragment2", BjyyBeanYewu1.class);
        List<BjyyBeanYewu2> data2 = null;
        List<BjyyBeanYewu3> data3 = null;
        if (data1 != null && data1.getData().size() > 0) {
            data2 = data1.getData();
            for (int i = 0; i < data2.size(); i++) {
                BjyyBeanYewu2 bjyyBeanYewu2 = data2.get(i);
                bjyyBeanYewu2.setId(data2.get(i).getId());
                bjyyBeanYewu2.setImg(data2.get(i).getImg());
                bjyyBeanYewu2.setName(data2.get(i).getName());
                bjyyBeanYewu2.setUrl(data2.get(i).getUrl());
                bjyyBeanYewu2.setEnable(data2.get(i).isEnable());
                data3 = data2.get(i).getData();
                for (int j = 0; j < data3.size(); j++) {
                    if (TextUtils.equals(id, data3.get(j).getId())) {
                        BjyyBeanYewu3 bjyyBeanYewu3 = data3.get(j);
                        bjyyBeanYewu3.setId(data3.get(j).getId());
                        bjyyBeanYewu3.setImg(data3.get(j).getImg());
                        bjyyBeanYewu3.setName(data3.get(j).getName());
                        bjyyBeanYewu3.setUrl(data3.get(j).getUrl());
                        bjyyBeanYewu3.setEnable(ismy);
                        bjyyBeanYewu2.setData(data3);
                        data1.setData(data2);
                        MmkvUtils.getInstance().set_common_json2("BjyyActFragment2", data1);
                        //

                    }
                }
            }
        }
        return data1;
    }

    public static List<String> choose_data_my() {
        BjyyBeanYewu1 data1 = MmkvUtils.getInstance().get_common_json("BjyyActFragment2", BjyyBeanYewu1.class);
        List<BjyyBeanYewu2> data2 = null;
        List<BjyyBeanYewu3> data3 = null;
        List<String> data = new ArrayList<>();
        if (data1 != null && data1.getData().size() > 0) {
            data2 = data1.getData();
            for (int i = 0; i < data2.size(); i++) {
                BjyyBeanYewu2 bjyyBeanYewu2 = data2.get(i);
                bjyyBeanYewu2.setId(data2.get(i).getId());
                bjyyBeanYewu2.setImg(data2.get(i).getImg());
                bjyyBeanYewu2.setName(data2.get(i).getName());
                bjyyBeanYewu2.setUrl(data2.get(i).getUrl());
                bjyyBeanYewu2.setEnable(data2.get(i).isEnable());
                data3 = data2.get(i).getData();
                for (int j = 0; j < data3.size(); j++) {
                    if (data3.get(j).isEnable()) {
                        data.add(data3.get(j).getId());
                    }
                }
            }
        }
        return data;
    }

    public static List<String> choose_data_my1() {
        BjyyActFragment251Bean2 data_id = MmkvUtils.getInstance().get_common_json("BjyyActFragment1", BjyyActFragment251Bean2.class);
        List<String> currentIds = new ArrayList<>();
        for (int i = 0; i < data_id.getData().size(); i++) {
            currentIds.add(data_id.getData().get(i).getmBean().getId());
        }
        return currentIds;
    }
}
