package com.wt2024.base.context;


import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Shade.Yang on 2017/4/28.
 */
public class ServiceContext {

    public static final String head = "wt.";

    private static final ThreadLocal<ServiceContext> context = new InheritableThreadLocal() {
        protected ServiceContext childValue(ServiceContext parentValue) {
            ServiceContext context = new ServiceContext();
            if (parentValue != null) {
                context.initBy(parentValue);
            }

            return context;
        }

        @Override
        protected ServiceContext initialValue() {
            return new ServiceContext();
        }
    };
    private Map<String, String> contextVar = new ConcurrentHashMap();

    public ServiceContext() {
    }

    public static ServiceContext getContext() {
        return (ServiceContext) context.get();
    }

    public static ServiceContext getContext(String prefix) {
        return (ServiceContext) context.get();
    }

    public static String genUniqueId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public Map<String, String> getAttachments() {
        HashMap cloneContextVar = new HashMap();
        Iterator var2 = this.contextVar.entrySet().iterator();

        while (var2.hasNext()) {
            Map.Entry entry = (Map.Entry) var2.next();
            cloneContextVar.put(head + (String) entry.getKey(), entry.getValue());
        }

        return cloneContextVar;
    }

    public void setAttachments(Map<String, String> attachments) {
        Iterator var2 = attachments.entrySet().iterator();

        while (var2.hasNext()) {
            Map.Entry entry = (Map.Entry) var2.next();
            if (((String) entry.getKey()).startsWith(head)) {
                this.contextVar.put(((String) entry.getKey()).substring(head.length()), entry.getValue() + "");
            }else{
                this.contextVar.put(entry.getKey()+"", entry.getValue() + "");
            }
        }

    }

    public void clearContextVar() {
        this.contextVar.clear();
    }

    public void initBy(ServiceContext serviceContext) {
        if (serviceContext != null && serviceContext != this) {
            this.contextVar.clear();
            this.contextVar.putAll(serviceContext.getContextVar());
        }
    }

    public void removeContextVar(String key) {
        if (this.contextVar.containsKey(key)) {
            this.contextVar.remove(key);
        }

    }

    public Map<String, String> getContextVar() {
        return this.contextVar;
    }

    public String getContextVar(String key) {
        return (String) this.contextVar.get(key);
    }

    public void addContextVar(String key, String value) {
        if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
            this.contextVar.put(key, value);
        }
    }

    public void addContextVar(Map<String, String> vars) {
        if (vars != null && vars.keySet().size() > 0) {
            this.contextVar.putAll(vars);
        }

    }
}
