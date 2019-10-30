package core.imp;

import core.inter.Id;

public class StringId implements Id {

    private String id;

    public StringId(String id) {
        this.id = id;
    }

    public static StringId newId(String id) {
        return new StringId(id);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof StringId &&
                (((StringId ) o).id == this.id || ((StringId ) o).id.equals(this.id));
        //null is allowed.
    }

    public String toString() {
        return id;
    }

}
