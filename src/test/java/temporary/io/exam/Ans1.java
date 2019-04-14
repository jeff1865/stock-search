package temporary.io.exam;

/**
 * Created by jeff on 4/11/19.
 */

import java.io.*;
import java.util.Scanner;

public class Ans1 {

    public static int getArea(int width, int height){
        //TODO Implements
        return 0;
    }

    public static void main (String[] args) throws java.lang.Exception
    {
        Scanner scanner = new Scanner(System.in).useRadix(10);
        int width = scanner.nextInt();
        int height = scanner.nextInt();

        System.out.println(getArea(width, height));
    }
}
