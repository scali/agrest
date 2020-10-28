package io.agrest.cayenne.cayenne.main.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.exp.property.ListProperty;
import org.apache.cayenne.exp.property.PropertyFactory;
import org.apache.cayenne.exp.property.StringProperty;

import io.agrest.cayenne.cayenne.main.E3;

/**
 * Class _E2 was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _E2 extends BaseDataObject {

    private static final long serialVersionUID = 1L;

    public static final String ID__PK_COLUMN = "id_";

    public static final StringProperty<String> ADDRESS = PropertyFactory.createString("address", String.class);
    public static final StringProperty<String> NAME = PropertyFactory.createString("name", String.class);
    public static final ListProperty<E3> E3S = PropertyFactory.createList("e3s", E3.class);

    protected String address;
    protected String name;

    protected Object e3s;

    public void setAddress(String address) {
        beforePropertyWrite("address", this.address, address);
        this.address = address;
    }

    public String getAddress() {
        beforePropertyRead("address");
        return this.address;
    }

    public void setName(String name) {
        beforePropertyWrite("name", this.name, name);
        this.name = name;
    }

    public String getName() {
        beforePropertyRead("name");
        return this.name;
    }

    public void addToE3s(E3 obj) {
        addToManyTarget("e3s", obj, true);
    }

    public void removeFromE3s(E3 obj) {
        removeToManyTarget("e3s", obj, true);
    }

    @SuppressWarnings("unchecked")
    public List<E3> getE3s() {
        return (List<E3>)readProperty("e3s");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "address":
                return this.address;
            case "name":
                return this.name;
            case "e3s":
                return this.e3s;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "address":
                this.address = (String)val;
                break;
            case "name":
                this.name = (String)val;
                break;
            case "e3s":
                this.e3s = val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeObject(this.address);
        out.writeObject(this.name);
        out.writeObject(this.e3s);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.address = (String)in.readObject();
        this.name = (String)in.readObject();
        this.e3s = in.readObject();
    }

}
