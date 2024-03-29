package org.example.spring;

import org.example.spring.exception.NoSuchBeanException;
import org.example.spring.exception.TooMuchBeanException;
import org.example.spring.utils.CreateBeanUtils;
import org.example.spring.utils.ScanBeanUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * -03/28-23:30
 * -ApplicationContext接口的实现类, Spring容器
 */
public class AnnotationConfigApplicationContext<T> implements ApplicationContext<T>{

    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    /**
     * 单例对象池
     */
    private final Map<String, Object> singletonObjects = new HashMap<>();

    /**
     * 创建Spring容器时指定的配置类
     */
    public final Class<T> configClass;

    public AnnotationConfigApplicationContext(Class<T> configClass) throws ClassNotFoundException {
        this.configClass = configClass;
        // 扫描组件
        ScanBeanUtils.scan(configClass,configClass, beanDefinitionMap);

        // 把组件中非懒加载的单例bean保存到单例池
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            BeanDefinition beanDefinition = entry.getValue();

            if(CreateBeanUtils.isSingleton(beanDefinition.getScope()) && !beanDefinition.isLazy()) {
                Object bean = CreateBeanUtils.createBean(beanDefinition);
                String beanName = entry.getKey();

                singletonObjects.put(beanName, bean);
            }
        }
    }

    @Override
    public Object getBean(String beanName) {
        if (!beanDefinitionMap.containsKey(beanName)) {
            return null;
        }

        return getBean(beanName, beanDefinitionMap.get(beanName));
    }

    /**
     * 使用默认构造器
     * @param type
     * @return T
     */
    @Override
    public T getBean(Class<T> type) {
        if (type == null) {
            throw new IllegalStateException("bean类型不能为空！");
        }

        // 保存指定类型的bean的个数
        AtomicInteger count = new AtomicInteger();
        // 保存同一类型的bean
        Map<String, BeanDefinition> objectMap = new HashMap<>();

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            BeanDefinition beanDefinition = entry.getValue();
            Class beanType = beanDefinition.getType();
            String beanName = entry.getKey();

            if (beanType.equals(type)) {
                count.addAndGet(1);
                objectMap.put(beanName, beanDefinition);
            }
        }

        if (count.get() == 0) {
            throw new NoSuchBeanException();
        } else if (count.get() > 1) {
            throw new TooMuchBeanException();
        } else {
            return (T) getBean((String) objectMap.keySet().toArray()[0], (BeanDefinition) objectMap.values().toArray()[0]);
        }
    }

    @Override
    public T getBean(String beanName, Class<T> type) {
        if (type == null) {
            throw new IllegalStateException("bean 类型不能为空！ ");
        }
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (type.equals(beanDefinition.getType())) {
                return (T) getBean(beanName, beanDefinition);
            }
        }
        throw new NoSuchBeanException();
    }


    /**
     * 统一获取bean的方法
     * @param beanName bean名称
     * @param beanDefinition BeanDefinition
     * @return Object 符合条件的bean对象
     */
    private Object getBean(String beanName, BeanDefinition beanDefinition) {
        String scope = beanDefinition.getScope();

        // bean的作用域是单例
        if (CreateBeanUtils.isSingleton(scope)) {
            Object object = singletonObjects.get(beanName);

            // 懒加载的单例bean
            if (object == null) {
                Object bean = CreateBeanUtils.createBean(beanDefinition);

                singletonObjects.put(beanName, bean);
            }

            return singletonObjects.get(beanName);
        }

        return CreateBeanUtils.createBean(beanDefinition);
    }
}
