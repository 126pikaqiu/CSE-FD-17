package utils;
import core.inter.BlockManager;
import core.LogicBlock;
import core.imp.BlockManagerImp;
import core.imp.StringId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BlockHandleUtil {

    public static long checkSum(byte[] bs) {
        long h = bs.length;
        for(byte b: bs) {
            long lb = (long) b;
            h = ((h << 1) | (h >>> 63) |
                    ((lb & 0xc3) << 41) | ((lb & 0xa7) << 12)) +
                    lb * 91871341 + 182134919;
        }
        return h;
    }
    private static ArrayList<BlockManager> blockManagers = new ArrayList<>();
    static {
        //产生10个blockManager
        for(int i = 0; i < 10; i++) {
            blockManagers.add(new BlockManagerImp(StringId.newId("bm-" + i)));
        }
    }

    public static BlockManager randomBlockManager() {
        return blockManagers.get((int)(Math.random() * 10));
    }

    public static ArrayList<LogicBlock> newLogicBlocks(int duplication, byte[] bs) {
        ArrayList<LogicBlock> logicBlocks = new ArrayList<>();
        for(int i = 0; i < duplication; i++) {
            logicBlocks.add(randomBlockManager().newBlock(bs).toLogicBlock());
        }
        return logicBlocks;
    }
    public static <E> void stretch(ArrayList<E> logicBlocks, int size ) {
        for(int i = 0; i < size; i++) {
            logicBlocks.add(null);
        }
    }
    public static String byte2hex(int b){
        char[] digletter = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        String ret = "";
        return ret + digletter[b/16] + digletter[b%16];
    }
    public static Long uuid() {
        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
        String time = df.format(new Date());
        return Long.parseLong(time);
    }
}
