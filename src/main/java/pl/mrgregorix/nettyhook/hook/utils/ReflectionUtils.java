package pl.mrgregorix.nettyhook.hook.utils;

import com.google.common.collect.Maps;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public final class ReflectionUtils {
    private ReflectionUtils() {}

    private static       String BUKKIT_PACKAGE                           = null;
    private static final Map<String, Class<?>>      cachedBukkitClasses  = Maps.newHashMap();
    private static final Map<String, Class<?>>      cachedNMSClasses     = Maps.newHashMap();
    private static final Map<String, Field>         cachedFields         = Maps.newHashMap();
    private static final Map<String, Method>        cachedMethods        = Maps.newHashMap();
    private static final Map<String, Constructor>   cachecCtors          = Maps.newHashMap();

    public static String getBukkitVersion()
    {
        final String packageName = getBukkitPackage();

        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }


    public static String getBukkitPackage()
    {
        if(BUKKIT_PACKAGE != null)
            return BUKKIT_PACKAGE;

        return Bukkit.getServer() == null ? null :(BUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName());
    }

    public static String getNMSPackage()
    {
        return "net.minecraft.server." + getBukkitVersion();
    }


    public static Class<?> getBukkitClass(final String className)
    {
        Validate.notNull(className, "Class name can't be null");

        try
        {
            if(cachedBukkitClasses.containsKey(className))
                return cachedBukkitClasses.get(className);

            final Class<?> clazz = Class.forName(getBukkitPackage() + "." + className);
            cachedBukkitClasses.put(className, clazz);

            return clazz;
        }
        catch (ClassNotFoundException e)
        {
            return null;
        }
    }


    public static Class<?> getNMSClass(final String className)
    {
        Validate.notNull(className, "Class name can't be null");
        try
        {
            if(cachedNMSClasses.containsKey(className))
                return cachedNMSClasses.get(className);

            final Class<?> clazz = Class.forName(getNMSPackage() + "." + className);
            cachedNMSClasses.put(className, clazz);

            return clazz;
        }
        catch (ClassNotFoundException e)
        {
            return null;
        }
    }

    public static void setPrivateField(final Class<?> clazz, final String fieldName, final Object target, final Object value) {
        try
        {
            Field field;

            if(cachedFields.containsKey(clazz.getName() + ":" + fieldName))
            {
                field = cachedFields.get(clazz.getName() + ":" + fieldName);
            }
            else
            {
                try
                {
                    field = clazz.getDeclaredField(fieldName);
                }
                catch (NoSuchFieldException e)
                {
                    field = clazz.getField(fieldName);
                }
                cachedFields.put(clazz.getName() + ":" + fieldName, field);
                field.setAccessible(true);
            }
            field.set(target, value);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static Object getPrivateField(final Class<?> clazz, final String fieldName, final Object target) {
        try
        {
            Field field;

            if(cachedFields.containsKey(clazz.getName() + ":" + fieldName))
            {
                field = cachedFields.get(clazz.getName() + ":" + fieldName);
            }
            else
            {
                try {
                    field = clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    field = clazz.getField(fieldName);
                }
                field.setAccessible(true);
                cachedFields.put(clazz.getName() + ":" + fieldName, field);
            }

            return field.get(target);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    public static Object invokePrivateMethod(final Class<?> clazz, final String methodName, final Object target, final Class[] paramTypes, final Object... parameters) {
        try
        {
            Method method;
            if(cachedMethods.containsKey(clazz.getName() + ":" + methodName + paramsToString(paramTypes)))
            {
                method = cachedMethods.get(clazz.getName() + ":" + methodName + paramsToString(paramTypes));
            }
            else
            {
                try
                {
                    method = clazz.getDeclaredMethod(methodName, paramTypes);
                }
                catch (NoSuchMethodException e)
                {
                    method = clazz.getMethod(methodName, paramTypes);
                }
                method.setAccessible(true);
                cachedMethods.put(clazz.getName() + ":" + methodName + paramsToString(paramTypes), method);
            }
            return method.invoke(target, parameters);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    public static <T> T createInstance(final Class<T> clazz, final Class[] ctorTypes, final Object... ctorParameters) {
        try
        {
            Constructor ctor;

            if(cachecCtors.containsKey(clazz.getName() + paramsToString(ctorTypes)))
            {
                ctor = cachecCtors.get(clazz.getName() + paramsToString(ctorTypes));
            }
            else
            {
                try
                {
                    ctor = clazz.getDeclaredConstructor(ctorTypes);
                }
                catch (NoSuchMethodException e)
                {
                    ctor = clazz.getConstructor(ctorTypes);
                }
                ctor.setAccessible(true);
                cachecCtors.put(clazz.getName() + paramsToString(ctorTypes), ctor);
            }

            ctor = clazz.getDeclaredConstructor(ctorTypes);
            ctor.setAccessible(true);

            return (T)ctor.newInstance(ctorParameters);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static Field byClass(Class clazz, Class paramType)
    {
        return byClass(clazz, paramType, 0);
    }

    public static Field byClass(Class clazz, Class paramType, int skip)
    {
        for(Field field : clazz.getDeclaredFields())
        {
            if (field.getType().isAssignableFrom(paramType))
            {
                if(skip-- > 0)
                    continue;

                field.setAccessible(true);
                return field;
            }
        }

        return null;
    }

    public static Object getHandle(Object object)
    {
        return invokePrivateMethod(object.getClass(), "getHandle", object, new Class[0]);
    }

    public static Object getStaticField(Class<?> clazz, String fieldName)
    {
        return getPrivateField(clazz, fieldName, null);
    }

    private static String paramsToString(Class[] classes)
    {
        StringBuilder b = new StringBuilder();
        for(Class clazz : classes)
            b.append(clazz.getName()).append(":");
        return b.toString();
    }
}