package org.example.spring;

/**
 * -03/29-16:33
 * -定义两个方法会在每个bean的初始化前和初始化后被调用。
 */
public interface BeanPostProcessor {
    /**
     * bean初始化前
     * @param bean
     * @param beanName
     * @return
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * bean初始化后
     * @param bean
     * @param beanName
     * @return
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
