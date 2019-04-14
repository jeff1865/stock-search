package temporary.io.exam;

/**
 * Created by jeff on 4/11/19.
 */
import java.io.*;

public class Ans2 {

    public static void main (String[] args) throws java.lang.Exception
    {
        class FifoBuffer {
            private String[] data ;
            private int takePointer = 0, offerPointer = 0, size = 0 ;

            public FifoBuffer(int bufSize) {
                data = new String[bufSize];
            }

            public boolean offer(String token) {
                if(data.length > size) {
                    data[offerPointer] = token ;
                    size++;
                    offerPointer = (offerPointer + 1) % data.length ;
                    return true ;
                } else {
                    return false ;
                }

            }

            public String take() {
                if(size > 0) {
                    String takeValue = data[takePointer];
                    size --;
                    takePointer = (takePointer + 1) % data.length;

                    return takeValue;
                }

                return null ;
            }

            public int size() {
                return size ;
            }
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = br.readLine();

        String[] tokens = input.split(" ");
        int cntCmd = Integer.parseInt(tokens[0]);
        int sizeOfBuf = Integer.parseInt(tokens[1]);

        String[] inputLines = new String[cntCmd] ;
        for(int i=0;i<inputLines.length;i++) {
            inputLines[i] = br.readLine() ;
        }

        FifoBuffer fifoBuf = new FifoBuffer(sizeOfBuf);

        for(int i=0;i<inputLines.length;i++) {
            input = inputLines[i];
            if(input.startsWith("OFFER")) {
                String[] cmdTokens = input.split(" ");
                if(cmdTokens.length == 2) {
                    System.out.println(fifoBuf.offer(cmdTokens[1]));
                } else {
                    System.out.println(false);
                }
            } else {
                if(input.equals("TAKE")) {
                    String token = fifoBuf.take();
                    if(token != null) {
                        System.out.println(token);
                    }
                }else if(input.equals("SIZE")) {
                    System.out.println(fifoBuf.size());
                }
            }
        }

        br.close();
    }
}
