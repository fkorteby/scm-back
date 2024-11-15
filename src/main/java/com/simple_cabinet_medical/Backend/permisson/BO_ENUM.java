package com.simple_cabinet_medical.Backend.permisson;

public enum BO_ENUM {


    ObjectArray("Object[]"),
    Object2DArray("Object[][]"),
    Patient ("Patient");


    private String value;

    BO_ENUM(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BO_ENUM getEnum(String value) {
        if(value.equals(BO_ENUM.ObjectArray.value)){
            return BO_ENUM.ObjectArray;
        }
        for(BO_ENUM v : values())
            if(v.getValue().equalsIgnoreCase(value)) return v;

        throw new IllegalArgumentException();
    }
}
