package cn.hejinyo.learn.base;

import java.util.*;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/18 17:40
 */
public class ListTest {
    public static void main(String[] args) {
        List<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            arrayList.add("arrayList_" + i);
        }
        System.out.println(arrayList.size());
        Iterator<String> listIterator = arrayList.listIterator();
        while (listIterator.hasNext()) {
            System.out.println(listIterator.next());
        }

        for (Iterator i = arrayList.iterator(); i.hasNext(); ) {
            System.out.println(i.next());
        }


        List<String> linkedList = new LinkedList<>();
        for (int i = 10; i < 20; i++) {
            linkedList.add("linkedList" + i);
        }
        System.out.println(linkedList.size());

        Map<String, Object> map = new HashMap<>();
        map.put("test1", "1");
        map.put("test2", "2");
        map.put("test3", "3");
        Iterator<Map.Entry<String, Object>> mapIterator = map.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<String, Object> entry = mapIterator.next();
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }

        Vector<String> vector = new Vector<>();
        vector.add("123");
        Iterator vectorIterater = vector.iterator();
        while (vectorIterater.hasNext()) {
            System.out.println(vectorIterater.next());
        }

        HashSet<String> set = new HashSet<>();
        set.add("1");
        Iterator<String> setIterator = set.iterator();
    }
}
