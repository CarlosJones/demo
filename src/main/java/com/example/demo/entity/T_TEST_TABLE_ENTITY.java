package com.example.demo.entity;

import java.sql.Timestamp;

public class T_TEST_TABLE_ENTITY {
    private String GKEY;
    private String TEST;
    private String CREATOR;
    private Timestamp CREATED;
    private String CHANGER;
    private Timestamp CHANGED;
    private Integer IS_DELETED;

    public String getGKEY() {
        return GKEY;
    }

    public void setGKEY(String GKEY) {
        this.GKEY = GKEY;
    }

    public String getTEST() {
        return TEST;
    }

    public void setTEST(String TEST) {
        this.TEST = TEST;
    }

    public String getCREATOR() {
        return CREATOR;
    }

    public void setCREATOR(String CREATOR) {
        this.CREATOR = CREATOR;
    }

    public Timestamp getCREATED() {
        return CREATED;
    }

    public void setCREATED(Timestamp CREATED) {
        this.CREATED = CREATED;
    }

    public String getCHANGER() {
        return CHANGER;
    }

    public void setCHANGER(String CHANGER) {
        this.CHANGER = CHANGER;
    }

    public Timestamp getCHANGED() {
        return CHANGED;
    }

    public void setCHANGED(Timestamp CHANGED) {
        this.CHANGED = CHANGED;
    }

    public Integer getIS_DELETED() {
        return IS_DELETED;
    }

    public void setIS_DELETED(Integer IS_DELETED) {
        this.IS_DELETED = IS_DELETED;
    }
}
