package com.ihblu.common.bean;

/**
 * @Description:
 * @Author: wy1in
 * @Date: 2022/3/31
 */
public class UserBean {
    private String username;
    private int age;

    public UserBean(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
