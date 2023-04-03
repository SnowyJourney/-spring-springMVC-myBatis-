package com.wang.dao;

import com.wang.annotate.Repository;
import com.wang.bean.Car;

@Repository
public class CarDao {

    public void getCarName(){
        Car car = new Car("HF", "snow", 1234);
        System.out.println(car.getName());
    }
}
