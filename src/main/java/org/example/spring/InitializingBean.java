package org.example.spring;

/**
 * -03/29-15:35
 * -Spring初始化完成后（字段注入之后）调用（钩子方法）
 * -判断该bean是否InitializingBean接口的实现类，如果是，调用其afterPropertiesSet()方法
 */
public interface InitializingBean {

    void afterPropertiesSet();
}
