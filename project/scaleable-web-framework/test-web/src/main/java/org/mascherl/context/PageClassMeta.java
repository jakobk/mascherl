package org.mascherl.context;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores meta data about a mascherl page class.
 *
 * @author Jakob Korherr
 */
public class PageClassMeta {

    private final Class<?> pageClass;
    private final Map<String, Method> formIndex = new HashMap<>();
    private final Map<String, Method> containerIndex = new HashMap<>();

    public PageClassMeta(Class<?> pageClass) {
        this.pageClass = pageClass;
    }

    public Class<?> getPageClass() {
        return pageClass;
    }

    public void addForm(String formName, Method method) {
        formIndex.put(formName, method);
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

    public void addContainer(String containerName, Method method) {
        containerIndex.put(containerName, method);
    }

    public Method getContainerMethod(String containerName) {
        return containerIndex.get(containerName);
    }

    public Collection<Method> getContainerMethods() {
        return containerIndex.values();
    }
}
