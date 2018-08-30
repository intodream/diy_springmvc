package dream.servlet;

import dream.annotation.DreamController;
import dream.annotation.DreamRequestMapping;
import dream.annotation.DreamRequestParam;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HandlerMapping {

    static Map<String,Object> handerMap = new HashMap<>(8);

    //获取方法参数
    public static Object[] methodParams(HttpServletRequest request, HttpServletResponse response, Method method){
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];

        int args_i = 0;
        int index = 0;
        for(Class<?> clazz : parameterTypes){
            if(ServletRequest.class.isAssignableFrom(clazz)){
                args[args_i++] = request;
            }
            if(ServletResponse.class.isAssignableFrom(clazz)){
                args[args_i++] = response;
            }
            Annotation[] annotations = method.getParameterAnnotations()[index];
            if(annotations.length > 0){
                for(Annotation annotation : annotations){
                    if(DreamRequestParam.class.isAssignableFrom(annotation.getClass())){
                        DreamRequestParam param = (DreamRequestParam) annotation;
                        args[args_i++] =  ConversionUtil.conversion(clazz, request.getParameter(param.value()));
                    }
                }
            }
            index++;
        }
        return args;
    }

    //获取所有controller下mapping的方法，存入map中
    public static void urlMapping(){
        for(Map.Entry<String,Object> entry : DispatcherServlet.beans.entrySet()){
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if(clazz.isAnnotationPresent(DreamController.class)){
                DreamRequestMapping requestMapping = clazz.getAnnotation(DreamRequestMapping.class);
                String mapping = requestMapping.value();
                Method[] methods = clazz.getMethods();
                for(Method method : methods){
                    if(method.isAnnotationPresent(DreamRequestMapping.class)){
                        DreamRequestMapping annotation = method.getAnnotation(DreamRequestMapping.class);
                        String methodPath = annotation.value();
                        if(methodPath.contains("{") && methodPath.contains("}")){
                            String[] paths = methodPath.split("/");
                            StringBuilder builder = new StringBuilder();
                            for(String path : paths){
                                if(path.contains("{") && path.contains("}")){
                                    builder.append("/*");
                                } else {
                                    if(!path.equals("")){
                                        builder.append("/" + path);
                                    }
                                }
                            }
                            DispatcherServlet.pathVariable.put(builder.toString(),mapping + methodPath);
                        }
                        handerMap.put(mapping + methodPath, method);
                    }
                }
            }
        }
    }

    //判断某个字符串是否为整数
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static String jointMapping(String path){
        String[] paths = path.split("/");
        StringBuilder builder = new StringBuilder();
        if(paths.length != 0){
            for(String s : paths){
                if(!s.equals("")){
                    if(isInteger(s)){
                        builder.append("/*");
                    } else {
                        builder.append("/" + s);
                    }
                }
            }
        } else {
            return path;
        }
        return builder.toString();
    }

}
