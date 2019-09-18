package com.pyg.pojo;

import java.io.Serializable;

public class TbBrand implements Serializable {
    //私有属性
    private Long id;

    private String name;

    private String firstChar;


    public TbBrand() {  //空参
    }

    public TbBrand(Long id, String name, String firstChar) { //全参数构造
        this.id = id;
        this.name = name;
        this.firstChar = firstChar;
    }


    //成员方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getFirstChar() {
        return firstChar;
    }

    public void setFirstChar(String firstChar) {
        this.firstChar = firstChar == null ? null : firstChar.trim();
    }
}