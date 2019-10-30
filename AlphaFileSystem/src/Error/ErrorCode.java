package Error;

import java.util.HashMap;
import java.util.Map;

public class ErrorCode extends RuntimeException {

    public static final int IO_EXCEPTION = 1;
    public static final int CHECKSUM_CHECK_FAILED = 2;
    public static final int UNKNOWN = 1000;
    public static final int CANNOT_FIND_FILE = 3;
    public static final int CANNOT_FIND_BLOCK = 4;
    public static final int ID_TYPE_ERROR = 5;
    public static final int LOGIST_BLOCK_UNAVAILABLE = 6; // 某个逻辑块不可用,具体指的是这个
    // 逻辑块对应的所有物理块副本不可用（不存在或者验证失败）
    public static final int FILE_CURSOR_MOVE_ERROR = 7;
    public static final int WRITE_NULL_ERROR = 8;
    public static final int FILE_DAMAGED = 9;
    public static final int NEW_FILE_ERROR = 10;
    public static final int NEW_BLOCK_ERROR = 11;

    private static final Map<Integer, String> ErrorCodeMap = new HashMap<>();

    static {
        ErrorCodeMap.put(IO_EXCEPTION, "IO exception");
        ErrorCodeMap.put(CHECKSUM_CHECK_FAILED, "block checksum check failed");
        ErrorCodeMap.put(CANNOT_FIND_FILE, "cannot find file");
        ErrorCodeMap.put(CANNOT_FIND_BLOCK, "cannot find block");
        ErrorCodeMap.put(ID_TYPE_ERROR, "id type error");
        ErrorCodeMap.put(LOGIST_BLOCK_UNAVAILABLE, "logistic block unavailable");
        ErrorCodeMap.put(FILE_CURSOR_MOVE_ERROR, "file cursor move error");
        ErrorCodeMap.put(WRITE_NULL_ERROR, "write null error");
        ErrorCodeMap.put(FILE_DAMAGED, "file damaged");
        ErrorCodeMap.put(NEW_FILE_ERROR, "new file error, the file already exists");
        ErrorCodeMap.put(NEW_BLOCK_ERROR, "new block error, the block already exists");
        ErrorCodeMap.put(UNKNOWN, "unknown");
    }

    public static String getErrorText(int errorCode) {
        return ErrorCodeMap.getOrDefault(errorCode, "invalid");
    }

    private int errorCode;

    public ErrorCode(int errorCode){
        super(String.format("error code '%d' \"%s\"", errorCode, getErrorText(errorCode)));
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
