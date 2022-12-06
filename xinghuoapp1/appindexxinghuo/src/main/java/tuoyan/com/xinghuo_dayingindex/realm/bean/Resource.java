package tuoyan.com.xinghuo_dayingindex.realm.bean;

import org.parceler.Parcel;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.ResourceRealmProxy;

@Parcel(implementations = {ResourceRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Resource.class})
public class Resource extends RealmObject implements Serializable {
    public String key;//TODO added when Version = 2
    public String name;
    public String url;
    public String path;
    public String type;/** 3:video / 7 audio **/ 
    public String from;/** 图书、网课 **/
    public String size; //文件大小
    public String duration;//时长
    public String liveType;//0 录播  1 直播
    public String liveKey;//直播回放id
    public String liveToken;//直播回放token
    public String lrcUrls;//音频字幕的url，3个字幕逗号隔开，不存在的 用 none 占位
    public String liveSource;//直播课类型1：欢拓，2：CC
}
