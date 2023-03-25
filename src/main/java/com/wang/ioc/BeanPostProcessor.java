package com.wang.ioc;

public interface BeanPostProcessor {
    default Object postProcessBeforeInitialization(Object bean,String beanName){
        return null;
    }
    default Object postProcessAfterInitialization(Object bean,String beanName){
        return null;
    }
}