import API.AlphaFileSystermInterface;
import Error.ErrorCode;
import core.inter.File;

public class Main {
    public static void main(String[] args) {

//        testCat2();
//        testWrite1();
//        testWrite2();
//        testWrite3();
//        testHex();
//        testCat1();
//        testCopyMethod1();
//        testCopyMethod2();

    }

    public static void testWrite1() {
        //从文件当前指针处写入
        char[] digletter = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','\n'};
        byte[] test = new byte[1100];
        for(int i = 0; i < test.length; i++) {
            test[i] = (byte)digletter[i%17];
        }
        try {
            AlphaFileSystermInterface.write("fm-1,test.txt",test,0, File.MOVE_CURR);
        } catch (ErrorCode errorCode) {
            System.out.println(errorCode.getMessage());
            errorCode.printStackTrace();
        }
    }
    public static void testWrite2() {
        //从文件头部写入
        char[] digletter = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','\n'};
        byte[] test = new byte[100];
        for(int i = 0; i < test.length; i++) {
            test[i] = (byte)digletter[0];
        }
        try {
            AlphaFileSystermInterface.write("fm-1,test.txt",test,0, File.MOVE_HEAD);
        } catch (ErrorCode errorCode) {
            System.out.println(errorCode.getMessage());
            errorCode.printStackTrace();
        }
    }
    public static void testWrite3() {
        //从文件尾部写入
        char[] digletter = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','\n'};
        byte[] test = new byte[10];
        for(int i = 0; i < test.length; i++) {
            test[i] = (byte)digletter[15];
        }
        try {
            AlphaFileSystermInterface.write("fm-1,test.txt",test,1024, File.MOVE_TAIL);
        } catch (ErrorCode errorCode) {
            System.out.println(errorCode.getMessage());
            errorCode.printStackTrace();
        }
    }
    public static void testHex(){
        AlphaFileSystermInterface.hex("bm-2,191019000924");
    }
    public static void testCat1(){
        AlphaFileSystermInterface.cat("fm-1,test.txt");
    }
    public static void testCat2(){
        AlphaFileSystermInterface.cat("fm-2,test2.txt");
    }
    public static void testCopyMethod1(){
        AlphaFileSystermInterface.copy("fm-1,test.txt","fm-2,test2.txt",1);
    }
    public static void testCopyMethod2(){
        AlphaFileSystermInterface.copy("fm-1,test.txt","fm-2,test2.txt",2);
    }
}
