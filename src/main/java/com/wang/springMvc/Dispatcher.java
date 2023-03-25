package com.wang.springMvc;

import com.wang.annotate.RequestParam;
import com.wang.springMvc.handler.Handler;
import com.wang.springMvc.handler.HandlerMapping;
import com.wang.ioc.ApplicationContext;
import com.wang.springMvc.view.ViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 继承原生的HttpServlet,实现框架结构
 */
public class Dispatcher extends HttpServlet {

    private HandlerMapping handlerMapping;

    private String contextPath;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("------doGet-----");
        executeDispatch(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //获取到spring的配置文件
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        String springXml = contextConfigLocation.split(":")[1];
        //初始化容器
        ApplicationContext ioc = new ApplicationContext(springXml);
        //获取路径映射器
        handlerMapping = new HandlerMapping(ioc);
        //获取工程路径
        contextPath = config.getServletContext().getContextPath();

    }
    public void executeDispatch(HttpServletRequest request,HttpServletResponse response) throws IOException {
        Handler handler = handlerMapping.getHandler(request);
        if(handler==null){
            //映射器中没有对应的handler就返回404
            response.getWriter().print("<h1>404 NOtFound<h1>");
        }
        Method method = handler.getMethod();
        try {
            //获取方法上参数类型
            Class<?>[] parameterTypes = method.getParameterTypes();
            //参数数组
            Object[] params= new Object[parameterTypes.length];
            
            //添加请求没有携带，tomcat自动装配的参数
            for (int i = 0; i < parameterTypes.length; i++) {
                if(parameterTypes[i].getSimpleName().equals("HttpServletRequest"))
                    params[i]=request;
                else if(parameterTypes[i].getSimpleName().equals("HttpServletResponse"))
                    params[i]=response;
            }

            //获取请求携带的参数
            Map<String,String[]> parameterMap = request.getParameterMap();
            //获取方法上的参数
            Parameter[] methodParams = method.getParameters();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String value = entry.getValue()[0];
                String name = entry.getKey();
                //存入request作用域
                request.setAttribute(name,value);
                for (int i = 0; i < methodParams.length; i++) {
                    if(methodParams[i].isAnnotationPresent(RequestParam.class)){
                        RequestParam requestParam = methodParams[i].getAnnotation(RequestParam.class);
                        String requestParamValue = requestParam.value();
                        if(requestParamValue.equals(name)) {
                            params[i] = value;
                            break;
                        }
                    }else{
                        String methodParamName = methodParams[i].getName();
                        if(methodParamName.equals(name)) {
                            params[i] = value;
                            break;
                        }
                    }
                }
            }
            Object invoke = method.invoke(handler.getObject(), params);
            ViewResolver viewResolver = new ViewResolver();
            viewResolver.viewResolver(request,response,invoke,method);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
