package com.spartan.dc.core.util.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bean tool class
 * 
 * @author xingjie
 */
public class BeanUtils extends org.springframework.beans.BeanUtils
{

    private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

    /** Subscript starting with the property name in the bean method */
    private static final int BEAN_METHOD_PROP_INDEX = 3;

    /** * Regular expression that matches the getter method */
    private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)");

    /** * Regular expression that matches the setter method */
    private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)");

    /**
     * Bean property copying tool method.
     * 
     * @param dest Target object
     * @param src Source object
     */
    public static void copyBeanProp(Object dest, Object src)
    {
        try
        {
            copyProperties(src, dest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Get the setter method of the object.
     * 
     * @param obj Object
     * @return The object's setter method list.
     */
    public static List<Method> getSetterMethods(Object obj)
    {
        // Setter method list
        List<Method> setterMethods = new ArrayList<Method>();

        // Get all methods
        Method[] methods = obj.getClass().getMethods();

        // Find setter methods

        for (Method method : methods)
        {
            Matcher m = SET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 1))
            {
                setterMethods.add(method);
            }
        }
        // Return the list of setter methods
        return setterMethods;
    }

    /**
     * Get the object's getter methods.
     * 
     * @param obj object
     * @return List of getter methods of the object
     */

    public static List<Method> getGetterMethods(Object obj)
    {
        // List of getter methods
        List<Method> getterMethods = new ArrayList<Method>();
        // Get all methods
        Method[] methods = obj.getClass().getMethods();
        // Find getter methods
        for (Method method : methods)
        {
            Matcher m = GET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 0))
            {
                getterMethods.add(method);
            }
        }
        // Return the list of getter methods
        return getterMethods;
    }


    /**
     * Convert bean object
     */
    public static <T> T convert(Object source, Class<T> tClass) {
        T t = null;
        try {
            t = tClass.newInstance();
            if (source == null) {
//				return t;
                return null;
            }
            BeanUtils.copyProperties(source, t);
        } catch (Exception e) {
            logger.error("tClass convert error ", e);
        }
        return t;
    }

    /**
     * Convert bean object, the source is null, the returned target is new target object
     */
    public static <T> T convertNew(Object source, Class<T> tClass) {
        T t = null;
        try {
            t = tClass.newInstance();
            if (source == null) {
				return t;
            }
            BeanUtils.copyProperties(source, t);
        } catch (Exception e) {
            logger.error("tClass convert error ", e);
        }
        return t;
    }

    /**
     * Convert list object
     */
    public static <T> List<T> convert(List<?> source, Class<T> tClass) {
        List<T> result = new ArrayList<T>();
        try {
            for (Object o : source) {
                if (o != null) {
                    T t = tClass.newInstance();
                    BeanUtils.copyProperties(o, t);
                    result.add(t);
                }
            }
        } catch (Exception e) {
            logger.error("List convert error ", e);
        }
        return result;
    }

    /**
     * Check if the property names in the bean method names are equal。<br>
     * If getName() and setName() share the same property name, then getName() and setAge() must have different property names.
     * 
     * @param m1 Method name1
     * @param m2 Method name2
     * @return Returns true if the property names are the same, otherwise returns false.
     */

    public static boolean isMethodPropEquals(String m1, String m2)
    {
        return m1.substring(BEAN_METHOD_PROP_INDEX).equals(m2.substring(BEAN_METHOD_PROP_INDEX));
    }
}
