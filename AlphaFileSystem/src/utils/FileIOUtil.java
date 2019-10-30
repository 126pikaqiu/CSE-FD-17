package utils;

import Error.ErrorCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileIOUtil {

    private static String pathRoot;

    private static String sep = "/";
    // for different OS
    static {
        pathRoot = "src" + sep +
                "AlphaFileSystem" + sep + "data" + sep;
    }

    public static void write2file(String fileName, byte[] b) {
//        System.out.println(pathRoot + fileName);
        File file = new File(pathRoot + fileName);
        try{
            if (!file.exists()) {
                mkdir(pathRoot + fileName);
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(b);
            fileOutputStream.close();
        } catch (IOException e) {
            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
        }
    }

    public static byte[] readFromfile(String fileName) {
//        System.out.println(pathRoot);
        File file = new File(pathRoot + fileName);
        byte[] ret;
        try{
            if (!file.exists()) {
                throw new ErrorCode(ErrorCode.CANNOT_FIND_FILE);
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            ret = fileInputStream.readAllBytes();
            fileInputStream.close();
        } catch (IOException e) {
            throw new ErrorCode(ErrorCode.IO_EXCEPTION);
        }
        return ret;
    }

    public static void write2file(String medium, String fileName, byte[] b) {
        write2file(medium + sep + fileName, b);
    }

    public static byte[] readFromfile(String medium, String fileName) {
        return readFromfile(medium + sep + fileName);
    }

    public static boolean fileExists(String medium, String fileName) {
        return new File(pathRoot + medium + sep + fileName).exists();
    }
    private static void mkdir(String path) {
        File file = new File(path.substring(0,path.lastIndexOf("/")));
        if(!file.exists())
            file.mkdirs();
    }
}

