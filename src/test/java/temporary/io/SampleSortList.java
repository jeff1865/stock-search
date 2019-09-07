package temporary.io;

import java.util.ArrayList;

/**
 * Created by 1002000 on 2019. 4. 11..
 * Compare Interface : https://beginnersbook.com/2017/08/comparator-interface-in-java/
 * https://www.baeldung.com/java-8-sort-lambda
 */
public class SampleSortList {

    public static void main(String... v) {
        ArrayList<Integer> integerList = new ArrayList<Integer>();
        ArrayList<String> stringList = new ArrayList<String>(); // 입력
        for (int i = 9; i >= 0; i--) {
            integerList.add(i);
        }

        stringList.add("ZZZ");
        stringList.add("YYY");
        stringList.add("XXX");
        stringList.add("WWW");
        stringList.add("VVV");

        integerList.sort((a1, a2) -> a1 - a2);

//        Collections.sort(integerList, new Comparator<Integer>() {
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                return o1 - o2;
//            }
//        });

        System.out.println(integerList);

        stringList.sort((a1, a2) -> a1.compareTo(a2));
        System.out.println(stringList);
    }
}
