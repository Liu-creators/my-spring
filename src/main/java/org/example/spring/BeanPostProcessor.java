package org.example.spring;

/**
 * -03/29-16:33
 * -定义两个方法会在每个bean的初始化前和初始化后被调用。
 */
public interface BeanPostProcessor {
    /**
     * bean初始化前
     * @param bean bean对象
     * @param beanName bean名称
     * @return bean对象
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * bean初始化后
     * @param bean bean对象
     * @param beanName bean名称
     * @return bean对象
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
