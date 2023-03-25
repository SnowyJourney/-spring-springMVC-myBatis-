package com.wang.controller;

import com.wang.annotate.*;
import com.wang.service.ServiceImpl.CarServiceImpl;

@Controller(value = "busController")
@Scope(value = "protoType")
public class CarController {

    @Autowired
    private CarServiceImpl carService;

    @RequestMapping(value = "/getCar")
    public void getCarService(){
        System.out.print("CarController: ");
        carService.getCarName();
    }

    @PostConstruct
    public void init(){
        System.out.println("CarController initIng");
    }
}
