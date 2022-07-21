package com.ihblu.common.factory.model;

/**
 * @Description:基础用户接口
 * @Author: wy1in
 * @Date: 2022/7/20
 */
public interface Author {
    String getId();
    void setId(String id);
    String getName();
    void setName(String name);
    String getPortrait();
    void setPortrait(String portrait);
}
