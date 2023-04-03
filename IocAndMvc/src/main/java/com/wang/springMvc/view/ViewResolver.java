package com.wang.springMvc.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wang.annotate.Component;
import com.wang.annotate.ResponseBody;
import com.wang.bean.Car;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

@Component
public class ViewResolver {

    public void viewResolver(HttpServletRequest request, HttpServletResponse response, Object view, Method method) throws ServletException, IOException {
        if(view instanceof String){
            String pagePath = (String) view;
            if(pagePath.contains(":")){
                String pageType = pagePath.split(":")[0];
                String page = pagePath.split(":")[1];
                if(pageType.equals("forward")){
                    request.getRequestDispatcher(page).forward(request,response);
                }else if(pageType.equals("redirect")){
                    String contextPath = request.getContextPath();
                    response.sendRedirect(contextPath+page);
                }
            }else{
                request.getRequestDispatcher(pagePath.split(":")[1]).forward(request,response);
            }
        }else if(view instanceof Car){
            if(!method.isAnnotationPresent(ResponseBody.class))
                return;
            Car car = (Car) view;
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(car);
            response.setContentType("text/html;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
            writer.close();
        }
    }
}
