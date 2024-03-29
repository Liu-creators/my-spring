package org.example.spring;

import org.example.spring.exception.NoSuchBeanException;

/**
 * -03/28-23:29
 * -扩展自BeanFactory接口
 */
public interface ApplicationContext<T> extends BeanFactory<T> {
    Object getBean(String beanName);

    T getBean(Class<T> type) throws NoSuchBeanException;

    T getBean(String beanName, Class<T> type);
}
