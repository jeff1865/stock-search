package temporary.io;

import java.util.*;

/**
 * Created by 1002000 on 2019. 4. 11..
 * Compare Interface : https://beginnersbook.com/2017/08/comparator-interface-in-java/
 * Sort Map : https://javarevisited.blogspot.com/2012/12/how-to-sort-hashmap-java-by-key-and-value.html
 *
 */
public class SampleSortTable {

    public static void main(String ... v) {
        sortIntKey();
        sortStringKey();

        sortByStringValue();
        sortByIntValue();
    }

    public static void sortByStringValue() {
        System.out.println("-----------------<SortByStringValue>-----------------") ;

        Hashtable<String, String> ht = new Hashtable<>() ;

        ht.put("KimChi","AAAA");
        ht.put("KalChi", "AAA");
        ht.put("Asura", "ZZ");
        ht.put("Zookeeper", "Yes");
        ht.put("Balbalta", "no") ;

        List<Map.Entry<String,String>> entries = new LinkedList<Map.Entry<String,String>>(ht.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<String, String>>() {

            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                System.out.println(o1.getValue() + " vs" + o2.getValue());
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        entries.forEach(System.out::println);

//        Map<String, Integer> sortedTable = sortByValues(ht);
//        sortedTable.forEach((k, v) -> {
//            System.out.println("Sorted -> " + k + " = " + v);
//        });
    }

    public static void sortByIntValue() {
        System.out.println("-----------------<SortByIntValue>-----------------") ;

        Hashtable<String, Integer> ht = new Hashtable<>() ;

        ht.put("KimChi",1);
        ht.put("KalChi", 111);
        ht.put("Asura", 3);
        ht.put("Zookeeper", 4);
        ht.put("Balbalta", 20) ;

        Map<String, Integer> sortedTable = sortByValues(ht);
        sortedTable.forEach((k, v) -> {
            System.out.println("Sorted -> " + k + " = " + v);
        });

    }

    public static <K extends Comparable,V extends Comparable> Map<K,V> sortByKeys(Map<K,V> map){
        List<K> keys = new LinkedList<K>(map.keySet());
        Collections.sort(keys);

        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
        for(K key: keys){
            sortedMap.put(key, map.get(key));
        }

        return sortedMap;
    }

    public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {

            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();

        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static void sortStringKey() {
        System.out.println("-----------------<SortStringKey>-----------------") ;
        Hashtable<String, Integer> ht = new Hashtable<>() ;

        TreeMap<String, Integer> tm = new TreeMap<>() ;
        tm.put("KimChi",1);
        tm.put("KalChi", 111);
        tm.put("Asura", 3);
        tm.put("Zookeeper", 4);
        tm.put("Balbalta",20) ;

        tm.forEach((key, value) -> {
            System.out.println(key + " -> " + value );
        });


        //IMPORTANT!! Convert Iterator to List
        List<String> lstKeys = new ArrayList<>();
        tm.keySet().iterator().forEachRemaining(lstKeys::add);

        System.out.println("Listed ->" + lstKeys);
    }

    public static void sortIntKey() {
        TreeMap<Integer, String> tm= new TreeMap<Integer, String>();
        tm.put(10, "Chaitanya");
        tm.put(1, "Ajeet");
        tm.put(11, "Test");
        tm.put(9, "Demo");
        tm.put(3, "Anuj");
//        // Get a set of the entries
//        Set set = tm.entrySet();
//        // Get an iterator
//        Iterator i = set.iterator();
//        // Display elements
//        while(i.hasNext()) {
//            Map.Entry me = (Map.Entry) i.next();
//            System.out.print(me.getKey() + ": ");
//            System.out.println(me.getValue());
//        }

        tm.forEach((key, value) -> {
            System.out.println(key + " -> " + value );
        });
    }
}
