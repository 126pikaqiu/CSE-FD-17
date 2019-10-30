package core.imp;

import core.inter.Id;
import utils.BlockHandleUtil;

public class IntegerId implements Id {

    private static long cursor =0;
    static {
        cursor = BlockHandleUtil.uuid();
    }
    private long id;

    public static IntegerId newId() {
        return new IntegerId(cursor++);
    }

    public static IntegerId newId(long id) {
        return new IntegerId(id);
    }

    private IntegerId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IntegerId && ((IntegerId) o).id==this.id;
    }

    public String toString() {
        return Long.toString(id);
    }
}
