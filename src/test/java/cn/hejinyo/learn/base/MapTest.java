package cn.hejinyo.learn.base;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/18 23:01
 */
public class MapTest {
    final static int x = 1;
    public static void main(String[] args) {
        HashMap<String,Object> hashMap = new HashMap<>(16);
        hashMap.put(null,null);

        Hashtable<String,Object> hashtable = new Hashtable();
        hashtable.put("key","23");

        LinkedHashMap<String,Object> linkedHashMap=new LinkedHashMap<>();
        linkedHashMap.put(null,null);
        System.out.println("key1".hashCode()>>>16);
        System.out.println("key1".hashCode());
        System.out.println("key".hashCode()>>>16);

        System.out.println(hash("1235555"));
        System.out.println(hash("1235555")&15);
        System.out.println(hash("1235555")%16);

        try{

        }catch (Exception e){

        }finally{

        }
    }

    static int hash(Object key) {   //jdk1.8 & jdk1.7
         int h;
         // h = key.hashCode() 为第一步 取hashCode值
         // h ^ (h >>> 16)  为第二步 高位参与运算
         return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
}
