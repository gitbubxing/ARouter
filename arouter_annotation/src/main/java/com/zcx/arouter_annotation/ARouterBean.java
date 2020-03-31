package com.zcx.arouter_annotation;

import javax.lang.model.element.Element;

/**
 * 路由最终要传递的实体封装类
 */
public class ARouterBean {

    public ARouterBean(Builder builder) {
        this.path= builder.path;
        this.aClass = builder.aClass;
        this.group = builder.group;
        this.beanType= builder.beanType;
        this.element =builder.element;
    }

    public enum BeanType {
        ACTIVITY
    }

    private Element element;
    private String path;
    private BeanType beanType;
    private String group;
    private Class<?> aClass;

    private ARouterBean() {

    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public BeanType getBeanType() {
        return beanType;
    }

    public void setBeanType(BeanType beanType) {
        this.beanType = beanType;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public void setaClass(Class<?> aClass) {
        this.aClass = aClass;
    }

    public static  class Builder {
        private String path;
        private BeanType beanType;
        private String group;
        private Class<?> aClass;
        private Element element;
        public  Builder addElement(Element element){
            this.element = element;
            return this;
        }
        public  Builder addPath(String path){
            this.path = path;
            return this;
        }
        public  Builder addBeanType(BeanType beanType){
            this.beanType = beanType;
            return this;
        }
        public  Builder addGroup(String group){
            this.group = group;
            return this;
        }
        public  Builder addClass(Class<?>  aClass){
            this.aClass = aClass;
            return this;
        }

    public ARouterBean build (){
            if (path == null || path .length()==0){
                throw  new IllegalArgumentException("path 不能为空，格式: /app/MainActivity");
            }

         return  new ARouterBean(this);
    }
    }
}
