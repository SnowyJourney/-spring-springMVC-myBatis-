package com.wang.mapper;

import com.wang.entity.Monster;

public interface MonsterMapper {

    public Monster queryOne(Integer id);

    public void insertOne(Monster monster);

}
