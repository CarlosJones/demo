package com.example.demo.mapper;

import com.example.demo.entity.T_TEST_TABLE_ENTITY;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface T_TEST_MAPPER {
    @Select({"select * from T_TEST_TABLE" })
    List<T_TEST_TABLE_ENTITY> getTestTable();
}
