package dream.servlet;

import dream.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispatcherServlet extends HttpServlet {

    private List<String> classNames = new ArrayList<>();

    static Map<String,Object> beans = new HashMap<>();

    static Map<String,String> pathVariable = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        if(!"/favicon.ico".equals(uri)){
            String contextPath = req.getContextPath();
            String path = uri.replace(contextPath, "");
            Object install = beans.get("/" + path.split("/")[1]);
            Method method = (Method) HandlerMapping.handerMap.get(HandlerMapping.jointMapping(path));
            Object[] hand = HandlerMapping.methodParams(req, resp, method);
            try {
                method.invoke(install,hand);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //获取顶级目录
        String packagePath = this.getClass().getPackage().getName();
        String[] paths = packagePath.split("\\.");
        //扫描
        doScanClass(paths[0]);
        //生成对象
        doInstance();
        //注入到controller
        doAutowired();
        //映射mapping方法
        HandlerMapping.urlMapping();
    }



    //获取当前包下所有.class文件并且存入List中
    public void doScanClass(String basePackage){
        URL resource = this.getClass().getClassLoader().getResource("/" + basePackage.replaceAll("\\.", "/"));
        String filestr = resource.getFile();
        File file = new File(filestr);

        String[] files = file.list();
        for(String s : files){
            File filePath = new File(filestr + s);

            if(filePath.isDirectory()){
                doScanClass(basePackage + "." + s);
            } else {
                classNames.add(basePackage + "." + filePath.getName());
            }
        }
    }


    //注册Controller 和 Service
    public void doInstance(){
        for(String s : classNames){
            String cn = s.replace(".class", "");
            try {
                Class<?> clazz = Class.forName(cn);
                if(clazz.isAnnotationPresent(DreamController.class)){
                    Object instance = clazz.newInstance();
                    DreamRequestMapping mapping = clazz.getAnnotation(DreamRequestMapping.class);
                    String key = mapping.value();
                    beans.put(key, instance);
                } else if(clazz.isAnnotationPresent(DreamService.class)){
                    Object instance = clazz.newInstance();
                    DreamService service = clazz.getAnnotation(DreamService.class);
                    String key = service.value();
                    beans.put(key, instance);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    //实现自动注入
    public void doAutowired(){
        for(Map.Entry<String,Object> entry : beans.entrySet()){
            Object value = entry.getValue();
            Class<?> clazz = value.getClass();
            if(clazz.isAnnotationPresent(DreamController.class) || clazz.isAnnotationPresent(DreamService.class)){
                Field[] fields = clazz.getDeclaredFields();
                for(Field field : fields){
                    if(field.isAnnotationPresent(DreamAuwowired.class)){
                        DreamAuwowired auto = field.getAnnotation(DreamAuwowired.class);
                        String key = auto.value();
                        field.setAccessible(true);
                        try {
                            field.set(value, beans.get(key));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
