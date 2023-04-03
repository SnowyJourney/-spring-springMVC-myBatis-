package com.wang.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Monster {
    private Integer id;
    private String name;
    private Integer gender;
    private Integer age;
    private Date birthday;
    private String email;
    private Double salary;
}
