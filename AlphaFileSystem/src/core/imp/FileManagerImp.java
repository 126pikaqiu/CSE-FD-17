package core.imp;

import Error.ErrorCode;
import core.inter.File;
import core.inter.FileManager;
import core.inter.Id;

public class FileManagerImp implements FileManager {

    public void setId(Id id) {
        this.id = id;
    }

    private Id id;

    @Override
    public File getFile(Id fileId) {
        return new FileImp(fileId, id);
    }

    @Override
    public File newFile(Id fileId) {
        File file = new FileImp(fileId, id);
        if(!file.createFile()) {
            // 创建文件失败返回异常，文件已经存在
            throw new ErrorCode(ErrorCode.NEW_FILE_ERROR);
        }
        return file;
    }

    @Override
    public Id getId() {
        return id;
    }

    public FileManagerImp(Id id) {
        this.id = id;
    }
}
