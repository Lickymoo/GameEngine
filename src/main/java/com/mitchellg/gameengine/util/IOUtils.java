package com.mitchellg.gameengine.util;

import org.lwjgl.BufferUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class IOUtils {

    public static String getFileIgnoreExtension(String filepath, String fileName){
        File folder = new File(filepath);
        File[] files = folder.listFiles();

        try {
            for (File file : files) {
                if (!file.isFile()) continue;

                String[] filename = file.getName().split("\\.(?=[^\\.]+$)");
                if (filename[0].equals(fileName)) {
                    return file.getName();
                }
            }
        }catch (NullPointerException e){
            return  null;
        }
        return null;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    private static Map<String,byte[]> cacheMap = new HashMap<>();

    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) {
        if(cacheMap.containsKey(resource)){
            byte[] arr = cacheMap.get(resource);

            ByteBuffer buffer = BufferUtils.createByteBuffer(arr.length);
            buffer.put(arr);
            return buffer.flip();
        }

        try {
            ByteBuffer buffer = null;
            URL url = IOUtils.class.getResource(resource);
            if (url == null)
                throw new IOException("Classpath resource not found: " + resource);
            File file = new File(url.getFile());
            if (file.isFile()) {
                FileInputStream fis = new FileInputStream(file);
                FileChannel fc = fis.getChannel();
                buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                
                fc.close();
                fis.close();
            }

            byte[] arr = new byte[buffer.remaining()];
            buffer.get(arr);
            cacheMap.put(resource, arr);

            return buffer;
        }catch (IOException e){
            return null;
        }
    }
}
