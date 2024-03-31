package org.example.spring.model;

import org.example.spring.BeanDefinition;
import org.example.spring.BeanPostProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * -03/31-17:20
 * -
 */
public class ResourceModel {
    /**
     * bean对象池
     */
    public final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    /**
     * 单例对象池
     */
    public final Map<String, Object> singletonObjects = new HashMap<>();

    /**
     * 创建Spring容器时指定的配置类
     */
    public Class<?> configClass;

    /**
     * List<BeanPostProcessor> listList<BeanPostProcessor> list
     */
    public final List<BeanPostProcessor> list = new ArrayList<>();
}
