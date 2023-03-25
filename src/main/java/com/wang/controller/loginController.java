package com.wang.controller;

import com.wang.annotate.*;
import com.wang.bean.Car;
import com.wang.service.ServiceImpl.CarServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestMapping(value = "/user")
@Controller
public class loginController {

    @Autowired
    private CarServiceImpl carService;

    @RequestMapping(value = "/login")
    public String login(HttpServletRequest request, HttpServletResponse response,String name,String pwd){
        System.out.println(name + " "+pwd);
        if(name.equals("wang")&&pwd.equals("1234")){
            return "forward:/login_success.jsp";
        }else{
            return "redirect:/login_error.jsp";
        }
    }

    @RequestMapping(value = "/getName")
    @ResponseBody
    public Car getName(){
        carService.getCarName();
        Car car = new Car("big", "small", 1234);
        return car;
    }

    @RequestMapping(value = "/getPwd")
    public void getPwd(String name,@RequestParam(value = "pwd")String password){
        System.out.println(name+" "+password);
    }

}
