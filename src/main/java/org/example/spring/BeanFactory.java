package org.example.spring;

import org.example.spring.exception.NoSuchBeanException;

/**
 * -03/28-23:27
 * -spring容器的顶级接口,获取bean的方法
 */
public interface BeanFactory<T> {

    Object getBean(String beanName);

    T getBean(Class<T> type) throws NoSuchBeanException;

    T getBean(String beanName, Class<T> type);
}


