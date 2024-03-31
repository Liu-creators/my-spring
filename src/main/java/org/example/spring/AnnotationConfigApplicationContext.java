package org.example.spring;

import org.example.spring.model.ResourceModel;
import org.example.spring.utils.CreateBeanUtils;
import org.example.spring.utils.GetBeanUtils;
import org.example.spring.utils.ScanBeanUtils;

import java.util.Map;

/**
 * -03/28-23:30
 * -ApplicationContext接口的实现类, Spring容器
 */
public class AnnotationConfigApplicationContext implements ApplicationContext{

    private final ResourceModel resourceModel;

    public <T> AnnotationConfigApplicationContext(Class<T> configClass) throws ClassNotFoundException {
        resourceModel = new ResourceModel();
        resourceModel.configClass = configClass;
        // 扫描组件
        ScanBeanUtils.scan(configClass, resourceModel);

        // 把组件中非懒加载的单例bean保存到单例池
        for (Map.Entry<String, BeanDefinition> entry : resourceModel.beanDefinitionMap.entrySet()) {
            BeanDefinition beanDefinition = entry.getValue();
            // 如果是单例并且不是懒加载就创建Bean实例，并加入单例对象池中
            if(CreateBeanUtils.isSingleton(beanDefinition.getScope()) && !beanDefinition.isLazy()) {
                Object bean = CreateBeanUtils.createBean(beanDefinition, resourceModel);
                String beanName = entry.getKey();

                resourceModel.singletonObjects.put(beanName, bean);
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
        return GetBeanUtils.getBean(beanName,resourceModel);
    }

    /**
     * 使用默认构造器
     * @param type bean类型
     * @return T
     */
    @Override
    public <T> T getBean(Class<T> type) {
        return GetBeanUtils.getBean(type, resourceModel);
    }

    /**
     * 通过类型和beanName获取对应的bean对象
     * @param beanName bean名字
     * @param type bean的类型
     * @return 需要的bean类型
     */
    @Override
    public <T> T getBean(String beanName, Class<T> type) {
        return GetBeanUtils.getBean(beanName,type, resourceModel);
    }
}
