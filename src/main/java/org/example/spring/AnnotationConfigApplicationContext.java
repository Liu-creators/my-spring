package org.example.spring;

import org.example.spring.utils.CreateBeanUtils;
import org.example.spring.utils.GetBeanUtils;
import org.example.spring.utils.ScanBeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * List<BeanPostProcessor> listList<BeanPostProcessor> list
     */
    public final  List<BeanPostProcessor> list = new ArrayList<>();

    public AnnotationConfigApplicationContext(Class<T> configClass) throws ClassNotFoundException {
        this.configClass = configClass;
        // 扫描组件
        ScanBeanUtils.scan(configClass, beanDefinitionMap, singletonObjects, list);

        // 把组件中非懒加载的单例bean保存到单例池
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            BeanDefinition beanDefinition = entry.getValue();
            // 如果是单例并且不是懒加载就创建Bean实例，并加入单例对象池中
            if(CreateBeanUtils.isSingleton(beanDefinition.getScope()) && !beanDefinition.isLazy()) {
                Object bean = CreateBeanUtils.createBean(beanDefinition, beanDefinitionMap, singletonObjects, list);
                String beanName = entry.getKey();

                singletonObjects.put(beanName, bean);
            }
        }
    }

    /**
     * 通过beanName查找
     * @param beanName bean名字
     * @return 返回bean实例
     */
    @Override
    public Object getBean(String beanName) {
        return GetBeanUtils.getBean(beanName,beanDefinitionMap,singletonObjects, list);
    }

    /**
     * 使用默认构造器
     * @param type bean类型
     * @return T
     */
    @Override
    public T getBean(Class<T> type) {
        return GetBeanUtils.getBean(type, beanDefinitionMap, singletonObjects, list);
    }

    /**
     * 通过类型和beanName获取对应的bean对象
     * @param beanName bean名字
     * @param type bean的类型
     * @return 需要的bean类型
     */
    @Override
    public T getBean(String beanName, Class<T> type) {
        return GetBeanUtils.getBean(beanName,type, beanDefinitionMap, singletonObjects,list);
    }
}
