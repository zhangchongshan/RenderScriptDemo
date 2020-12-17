package com.sonoptek.renderscriptdemo;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Util {

    public static String findLibrary1(Context context, String libName) {
        String result = null;
        ClassLoader classLoader = (context.getClassLoader());
        if (classLoader != null) {
            try {
                Method findLibraryMethod = classLoader.getClass().getMethod("findLibrary", new Class<?>[] { String.class });
                if (findLibraryMethod != null) {
                    Object objPath = findLibraryMethod.invoke(classLoader, new Object[] { libName });
                    if (objPath != null && objPath instanceof String) {
                        result = (String) objPath;
                    }
                }
            } catch (NoSuchMethodException e) {
                Log.e("findLibrary1", e.toString());
            } catch (IllegalAccessException e) {
                Log.e("findLibrary1", e.toString());
            } catch (IllegalArgumentException e) {
                Log.e("findLibrary1", e.toString());
            } catch (InvocationTargetException e) {
                Log.e("findLibrary1", e.toString());
            } catch (Exception e) {
                Log.e("findLibrary1", e.toString());
            }
        }

        return result;
    }

}
