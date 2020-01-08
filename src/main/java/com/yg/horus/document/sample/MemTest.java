package com.yg.horus.document.sample;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.Loader;

/**
 * Created by jeff on 1/5/20.
 */
public class MemTest {
    public static void main(String ... v) {
        System.out.println("Active System ...");
//        Loader.load(opencv_core.class);
//        BytePointer bytePointer = new BytePointer(1200);
//
//        bytePointer.put((byte)1);
//
        FloatPointer fp = new FloatPointer(1200);
        fp.put(0.111f);

        System.out.println("Successfully completed ..");
    }

}
