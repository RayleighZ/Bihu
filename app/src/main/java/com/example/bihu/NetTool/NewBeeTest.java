package com.example.bihu.NetTool;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public class NewBeeTest {

    public static String getImageStr(File imageFile) {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(imageFile));
            byte[] data = new byte[in.available()];
            in.read(data);
            in.close();
            String imageStr = new String(Base64.getEncoder().encode(data));
            return imageStr;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    static public byte[] fileConvertToByteArray(File file) {
        byte[] data = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            data = baos.toByteArray();

            fis.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static File getImageFile(String imageStr) {
        File file = new File("D:\\" + System.currentTimeMillis() + ".png");
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] data = Base64.getDecoder().decode(imageStr);
            out.write(data);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
