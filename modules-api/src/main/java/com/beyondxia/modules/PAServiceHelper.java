package com.beyondxia.modules;

import android.content.Context;
import android.text.TextUtils;

import com.beyondxia.modules.fetcher.CacheServiceFetcher;
import com.beyondxia.modules.fetcher.NoCacheServiceFetcher;
import com.beyondxia.modules.fetcher.SingleInstanceServiceFetcher;
import com.beyondxia.modules.utils.ClassUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Create by ChenWei on 2018/8/23 16:10
 **/
public class PAServiceHelper {
    private static final String REGISTER_PACKAGE_NAME = "com.beyondxia.modules_interface_library";

    private static final Map<String, Class> instanceServices = new HashMap<>();

    private static final Map<String, Class> noCacheServices = new HashMap<>();

    private static final Map<String, Class> cacheServices = new HashMap<>();


    public static void init(Context context) {
        try {
            Set<String> registerClassName = ClassUtil.getFileNameByPackageName(context, REGISTER_PACKAGE_NAME);
            for (String className : registerClassName) {
                Class<?> registerClazz = Class.forName(className);
                Object registerObj = registerClazz.newInstance();
                if (registerObj instanceof IRegisterService) {
                    IRegisterService iRegisterService = (IRegisterService) registerObj;
                    iRegisterService.registerService(cacheServices);
                }
            }
            registerServices();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void register(String name, PAServiceEvn.ServiceFetcher fetcher) {
        if (!TextUtils.isEmpty(name) && fetcher != null) {
            PAServiceEvn.registerService(name, fetcher);
        }
    }

    private static void registerServices() throws ClassNotFoundException {
        for (Map.Entry<String, Class> entry : instanceServices.entrySet()) {
            register(entry.getKey(), getSingleInstanceServiceFetcher(entry.getValue()));
        }
        for (Map.Entry<String, Class> entry : noCacheServices.entrySet()) {
            register(entry.getKey(), getNoCacheServiceFetcher(entry.getValue().getName()));
        }
        for (Map.Entry<String, Class> entry : cacheServices.entrySet()) {
            register(entry.getKey(), getCacheServiceFetcher(entry.getValue()));
        }

    }

    private static PAServiceEvn.ServiceFetcher getSingleInstanceServiceFetcher(final Class<?> cls) {
        return new SingleInstanceServiceFetcher() {
            @Override
            public Object createService() {
                try {
                    return cls.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

    }

    private static PAServiceEvn.ServiceFetcher getNoCacheServiceFetcher(final String cls_name) {
        return new NoCacheServiceFetcher() {

            @Override
            public Object createService() {
                try {
                    return Class.forName(cls_name).newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    private static PAServiceEvn.ServiceFetcher getCacheServiceFetcher(final Class<?> cls) {
        return new CacheServiceFetcher() {
            @Override
            public Object createService() {
                try {
                    return cls.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }


}
