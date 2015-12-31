package com.example.ceo.lrucache.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by roy on 2015/12/31.
 */
public class CloseUtils {

    public static void closeQuietly(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
