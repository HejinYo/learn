package cn.hejinyo.learn.base;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/18 23:01
 */
public class MapTest {
    public static void main(String[] args) {
        HashMap<String,Object> hashMap = new HashMap<>(16);
        hashMap.put(null,null);

        Hashtable<String,Object> hashtable = new Hashtable();
        hashtable.put("key","23");

        LinkedHashMap<String,Object> linkedHashMap=new LinkedHashMap<>();
        linkedHashMap.put(null,null);


    }
}
