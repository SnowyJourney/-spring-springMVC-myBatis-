package com.wang.ioc.processor;

import com.wang.annotate.Component;
import com.wang.ioc.BeanPostProcessor;

@Component
public class myProcessorOne implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("myProcessorOne-------: "+beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("myProcessorOne--------: "+beanName);
        return bean;
    }
}
