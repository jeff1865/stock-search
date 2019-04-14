package temporary.io.exam;

/**
 * Created by jeff on 4/11/19.
 */

import java.io.*;

public class Ans3 {
    public static void main(String[] args) throws java.lang.Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = br.readLine();

        String[] strValues = input.split(" ");
        int[] values = new int[strValues.length] ;

        for(int i=0;i<values.length;i++) {
            values[i] = Integer.parseInt(strValues[i]) ;
        }

        int target = Integer.parseInt(br.readLine()) ;

        boolean detected = false ;
        for(int i=0;i<values.length -1;i++) {
            for(int j=i+1;j<values.length;j++) {
                if(values[i] + values[j] == target) {
                    System.out.println(values[i] + " " + values[j]);
                    detected = true ;
                    break ;
                }
            }

            if(detected) break ;
        }

        if(!detected) {
            System.out.println(-1);
        }

        br.close();
    }





}
