package org.example.spring;

/**
 * -03/28-23:39
 * -Bean 定义
 */
public class BeanDefinition {

    /**
     * bean的类型
     */
    private Class type;

    /**
     * bean的作用域
     */
    private String scope;

    /**
     * 是否懒加载
     */
    private boolean lazy;

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

}
