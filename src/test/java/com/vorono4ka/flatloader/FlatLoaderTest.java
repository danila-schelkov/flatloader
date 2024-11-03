package com.vorono4ka.flatloader;

import com.vorono4ka.flatloader.swf.SupercellSWF;

import java.io.IOException;
import java.io.InputStream;

public class FlatLoaderTest {
    public static void main(String[] args) {
        try (InputStream inputStream = FlatLoaderTest.class.getResourceAsStream("/test.sc2")) {
            byte[] bytes = inputStream.readAllBytes();

            FlatLoader supercellSWFFlatLoader = new FlatLoader(new FlatByteStream(bytes));
            SupercellSWF swf = supercellSWFFlatLoader.deserializeClass(SupercellSWF.class);
            System.out.println(swf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
