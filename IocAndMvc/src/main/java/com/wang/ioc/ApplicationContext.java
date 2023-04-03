package com.wang.ioc;

import com.wang.annotate.*;
import com.wang.springMvc.handler.Handler;
import com.wang.springMvc.xml.XmlPaser;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {
    //需要扫描的xml文件
    private String springXml;
    //存储容器中bean的信息
    private ConcurrentHashMap<String,BeanDefine> beanDefineMap = new ConcurrentHashMap<>();
    //存储bean实例化对象，但针对单列
    private ConcurrentHashMap<String,Object> singleton = new ConcurrentHashMap<>();
    //存储后置处理器
    private ArrayList<String> processorDefineList = new ArrayList<>();
    //存储路径映射
    private ConcurrentHashMap<String, Handler> handlerMap = new ConcurrentHashMap<>();

    public ApplicationContext(String springXml) {
        this.springXml = springXml;
        initBeanDefineMap(springXml);
        initSingleton();
    }

    public ConcurrentHashMap<String, Handler> getHandlerMap() {
        return handlerMap;
    }

    //初始化beanDefineMap，将需要注入容器中bean信息存到里面
    private void initBeanDefineMap(String springXml){
        //获取到对应spring配置文件需要扫描的包
        List<String> packages = XmlPaser.getPackages(springXml);
        for (String aPackage : packages) {
            packageScan(aPackage);
        }
    }

    //1.扫描包下的文件信息，并封装成beanDefine对象保存到map中
    //2.发现对应的processor就将名字封装到processorDefineList
    private void packageScan(String aPackage){
        //获取到对应包的工作路径，这里工作路径就是编译产生class文件保存的位置
        ClassLoader classLoader = ApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(aPackage.replace(".","/"));
        //获取到该路径下的类，如果是包就递归扫描
        File file = new File(resource.getFile());
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File f : files) {
                if(f.isDirectory())
                    packageScan(aPackage+"."+f.getName());
                else {
                    String fAbsolutePath = f.getAbsolutePath();
                    //过滤掉不是.class的文件
                    if(!fAbsolutePath.endsWith(".class"))
                        continue;
                    //获取到类名
                    String className = fAbsolutePath.substring(fAbsolutePath.lastIndexOf("\\") + 1,
                            fAbsolutePath.indexOf(".class"));
                    try {
                        //加载得到类信息
                        Class<?> clazz = classLoader.loadClass(aPackage + "." + className);
                        //将带有组件注解的类信息保存到beanDefineMap中
                        if(clazz.isAnnotationPresent(Component.class)||
                                clazz.isAnnotationPresent(Repository.class)||
                                        clazz.isAnnotationPresent(Controller.class)||
                                            clazz.isAnnotationPresent(Service.class)){
                            //如果组件注解上有别名，那就有组件注解的别名,名字默认类名首字母小写
                            String beanName = StringUtils.uncapitalize(className);
                            String annotateValue = getAnnotateValue(clazz);
                            if(!annotateValue.equals(""))
                                beanName=annotateValue;
                            //判断这个类是否是单列或者多列,没有标记默认是单列
                            String scope="singleton";
                            if(clazz.isAnnotationPresent(Scope.class)){
                                Scope scopeType= clazz.getAnnotation(Scope.class);
                                if(scopeType.value().equals("protoType"))
                                    scope="protoType";
                            }
                            //将bean信息存入到beanDefineMap中
                            BeanDefine beanDefine = new BeanDefine(scope, clazz);
                            beanDefineMap.put(beanName,beanDefine);
                            //如果这个组件还是一个后置处理器哪么就加入到后置处理器注册表中processorDefineList
                            Class<?>[] interfaces = clazz.getInterfaces();
                            for (Class<?> anInterface : interfaces) {
                                String interfaceName = anInterface.getName();
                                interfaceName = interfaceName.substring(interfaceName.lastIndexOf(".") + 1);
                                if(interfaceName.equals("BeanPostProcessor")){
                                    processorDefineList.add(beanName);
                                    break;
                                }
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //获取到对应class文件上组件注解的的value值
    private String getAnnotateValue(Class clazz) {
        if (clazz.isAnnotationPresent(Component.class)) {
            Component annotation = (Component) clazz.getAnnotation(Component.class);
            return annotation.value();
        } else if (clazz.isAnnotationPresent(Controller.class)) {
            Controller annotation = (Controller) clazz.getAnnotation(Controller.class);
            return annotation.value();
        } else if (clazz.isAnnotationPresent(Service.class)) {
            Service annotation = (Service) clazz.getAnnotation(Service.class);
            return annotation.value();
        } else if (clazz.isAnnotationPresent(Repository.class)) {
            Repository annotation = (Repository) clazz.getAnnotation(Repository.class);
            return annotation.value();
        }
        return "";
    }

    //初始化singleton,将beanDefineMap中的bean对象创建好放入单例池中
    private void initSingleton() {
        for (Map.Entry<String, BeanDefine> entry : beanDefineMap.entrySet()) {
            BeanDefine beanDefine = entry.getValue();
            String className = entry.getKey();
            if(singleton.get(className)!=null)
                continue;
            if(beanDefine.getScope().equals("singleton")){
                Object bean = createBean(beanDefine.getClazz(),className);
                if(bean==null){
                    throw new RuntimeException("实例化单例对象异常");
                }
                singleton.put(className,bean);
            }
        }
    }

    /*
        1.根据class通过反射创建对象
        2.根据类上字段完成依赖注入
        3.对有标注的完成初始化操作
        4.执行所有后置处理器
        5.将方法上带有RequestMapping的url,类，名字，保存在handlerMap中
     */
    private Object createBean(Class beanClass,String className){
        Object bean=null;
        try {
            bean = beanClass.getDeclaredConstructor().newInstance();
            //完成依赖注入，这里并没有完全解决依赖注入全部问题
            Field[] declaredFields = beanClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if(declaredField.isAnnotationPresent(Autowired.class)){
                    //得到类名
                    String simpleName = declaredField.getType().getSimpleName();
                    String filedClassName = StringUtils.uncapitalize(simpleName);
                    Autowired autowired = declaredField.getAnnotation(Autowired.class);
                    String value = autowired.value();
                    if(!value.equals(""))
                        filedClassName=value;

                    Object bean1 = getBean(filedClassName);
                    declaredField.setAccessible(true);
                    declaredField.set(bean,bean1);
                }
            }

            if(bean instanceof BeanPostProcessor)
                return bean;

            //后置处理器before
            for (int i = 0; i < processorDefineList.size(); i++) {
                String processorName = processorDefineList.get(i);
                BeanPostProcessor processor = (BeanPostProcessor) getBean(processorName);
                Object result = processor.postProcessBeforeInitialization(bean, className);
            }

            //完成初始化操做作
            Method[] methods = beanClass.getMethods();
            //如果有requestMapping就将注解路径映射到handlerMap中
            String url="";
            if(beanClass.isAnnotationPresent(RequestMapping.class)){
                RequestMapping requestMapping = (RequestMapping) beanClass.getAnnotation(RequestMapping.class);
                String value = requestMapping.value();
                url=value;
            }
            for (Method method : methods) {
                if(method.isAnnotationPresent(PostConstruct.class))
                    method.invoke(bean);
                 if(method.isAnnotationPresent(RequestMapping.class)){
                     RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
                     String value = methodRequestMapping.value();
                     value=url+value;
                     Handler handler = new Handler(value, bean, method);
                     handlerMap.put(value,handler);
                 }
            }

            //后置处理器after
            for (int i = processorDefineList.size()-1; i >= 0; i--) {
                String processorName = processorDefineList.get(i);
                BeanPostProcessor processor = (BeanPostProcessor) getBean(processorName);
                Object result = processor.postProcessAfterInitialization(bean, className);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    //从容器中获取bean对象
    public Object getBean(String className){
        BeanDefine beanDefine = beanDefineMap.get(className);
        if(beanDefine==null)
            throw new NullPointerException("没有对应bean");
        if(beanDefine.getScope().equals("singleton")){
            Object bean = singleton.get(className);
            if(bean==null) {
                Object bean1 = createBean(beanDefine.getClazz(),className);
                bean=bean1;
//                //将其加入到单例池中
                singleton.put(className,bean1);
            }
            return bean;
        }else{
            Object bean=createBean(beanDefine.getClazz(),className);
            if(bean==null)
                throw new RuntimeException("实例化多列对象异常");
            return bean;
        }
    }
}
