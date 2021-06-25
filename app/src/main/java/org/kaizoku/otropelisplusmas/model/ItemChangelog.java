package org.kaizoku.otropelisplusmas.model;

import java.util.ArrayList;
import java.util.List;

public class ItemChangelog {
    public static final int TYPE_FIX = 1;
    public static final int TYPE_CHANGE = 2;
    public static final int TYPE_NEW = 3;
    public int code;
    public String name;
    public List<Change> changes=new ArrayList<>();

    public ItemChangelog() { }

    public ItemChangelog(int code, String name, List<Change> changes) {
        this.code = code;
        this.name = name;
        this.changes = changes;
    }

    public static String getType(int type){
        switch (type){
            case 1:return "FIX";
            case 2:return "CHANGE";
            case 3:return "NEW";
        }
        return null;
    }

}