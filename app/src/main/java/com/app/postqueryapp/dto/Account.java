package com.app.postqueryapp.dto;

import org.litepal.crud.DataSupport;

public class Account extends DataSupport {
    /**
     * 用户id
     */
    private int id;

    /**
     * 用户名
     */
    private String author;

    /**
     * 用户密码
     */
    private String passWord;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
