package com.onestore.sample.data.dao;

import com.onestore.sample.data.bo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by a1000074 on 06/11/2019.
 */
@Repository
@Mapper
public interface SampleDbMapper {
    @Insert("insert into myuser(name, age)" +
            "value(#{name}, #{age})")
    void insertUser(User user) ;

    @Select("select * from myuser where name = #{name}")
    List<User> selectUser(@Param("name") String name) ;
}
