package com.wang.service.ServiceImpl;

import com.wang.annotate.Service;
import com.wang.service.Car;

@Service
public class CarServiceImpl implements Car {

    @Override
    public void getCarName() {
        System.out.println("CarServiceImpl: ");
    }
}
