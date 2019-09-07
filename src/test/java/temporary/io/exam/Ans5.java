package temporary.io.exam;

/**
 * Created by jeff on 4/11/19.
 */

import java.io.*;

public class Ans5 {

    public static void main (String[] args) throws java.lang.Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = br.readLine();
//        System.out.println(input);
        int windows = Integer.parseInt(input);
        long[] data0 = new long[300000];
        int cnt = 0;
        for(int i=0; i<Integer.MAX_VALUE;i++) {
            String line = br.readLine() ;
            if(line == null || line.equals("") ) break ;
            cnt ++;
            data0[i] = Long.parseLong(br.readLine());
        }
        long[] data = new long[cnt];
        for(int i=0;i<cnt;i++) {
            data[i] = data0[i];
        }

        solve(data, windows);
    }

    private static void solve(long[] data, int k)
    {
        for(int i=k; i <= data.length; i++)
        {
            long max = -3000000000L;

            for(int j=i-k; j<i; j++)
            {
//                max = Math.max(max, data[j]);
                if(data[j] > max) {
                    max = data[j];
                }
            }

            System.out.println(max);
        }
    }








//    static void printMax(int arr[],int n, int k)
//    {
//        // Create a Double Ended Queue, Qi that will store indexes of array elements
//        // The queue will store indexes of useful elements in every window and it will
//        // maintain decreasing order of values from front to rear in Qi, i.e.,
//        // arr[Qi.front[]] to arr[Qi.rear()] are sorted in decreasing order
//        Deque<Integer> Qi = new LinkedList<Integer>();
//
//        /* Process first k (or first window) elements of array */
//        int i;
//        for(i = 0; i < k; ++i)
//        {
//            // For every element, the previous smaller elements are useless so
//            // remove them from Qi
//            while(!Qi.isEmpty() && arr[i] >= arr[Qi.peekLast()])
//                Qi.removeLast();   // Remove from rear
//
//            // Add new element at rear of queue
//            Qi.addLast(i);
//        }
//
//        // Process rest of the elements, i.e., from arr[k] to arr[n-1]
//        for( ;i < n; ++i)
//        {
//            // The element at the front of the queue is the largest element of
//            // previous window, so print it
//            System.out.print(arr[Qi.peek()] + " ");
//
//            // Remove the elements which are out of this window
//            while((!Qi.isEmpty()) && Qi.peek() <= i-k)
//                Qi.removeFirst();
//
//            // Remove all elements smaller than the currently
//            // being added element (remove useless elements)
//            while((!Qi.isEmpty()) && arr[i] >= arr[Qi.peekLast()])
//                Qi.removeLast();
//
//
//            // Add current element at the rear of Qi
//            Qi.addLast(i);
//
//        }
//
//        // Print the maximum element of last window
//        System.out.print(arr[Qi.peek()]);
//
//        Qi.forEach(System.out::println);
//    }

}
