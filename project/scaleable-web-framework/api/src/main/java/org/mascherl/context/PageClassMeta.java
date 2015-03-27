package org.mascherl.context;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Stores meta data about a mascherl page class.
 *
 * @author Jakob Korherr
 */
public class PageClassMeta {

    public static class Builder {

        private Class<?> pageClass;
        private String pageTemplate;
        private Map<String, Method> formIndex = new HashMap<>();
        private Map<String, Method> containerIndex = new HashMap<>();

        public void setPageClass(Class<?> pageClass) {
            this.pageClass = pageClass;
        }

        public void setPageTemplate(String pageTemplate) {
            this.pageTemplate = pageTemplate;
        }

        public void addContainer(String containerName, Method method) {
            containerIndex.put(containerName, method);
        }

        public void addForm(String formName, Method method) {
            formIndex.put(formName, method);
        }

        public PageClassMeta build() {
            return new PageClassMeta(this);
        }

    }

    private final Class<?> pageClass;
    private final String pageTemplate;
    private final ConcurrentMap<String, Method> formIndex;
    private final ConcurrentMap<String, Method> containerIndex;

    private PageClassMeta(Builder builder) {
        pageClass = builder.pageClass;
        pageTemplate = builder.pageTemplate;
        formIndex = new ConcurrentHashMap<>(builder.formIndex);
        containerIndex = new ConcurrentHashMap<>(builder.containerIndex);
    }

    public Class<?> getPageClass() {
        return pageClass;
    }

    public String getPageTemplate() {
        return pageTemplate;
    }

    public Method getFormMethod(String formName) {
        return formIndex.get(formName);
    }

    public Collection<Method> getFormMethods() {
        return formIndex.values();
    }

    public boolean containerExists(String containerName) {
        return containerIndex.containsKey(containerName);
    }

    public Method getContainerMethod(String containerName) {
        return containerIndex.get(containerName);
    }

    public Collection<Method> getContainerMethods() {
        return containerIndex.values();
    }
}
