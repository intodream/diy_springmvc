package dream.servlet;

public class ConversionUtil {

    private static TypeConversion typeConversion = new ClassTypeConversion();

    public static <T> T conversion(Class<?> clazz, String param){
        T result = null;
        if(Double.class.isAssignableFrom(clazz)){
            result = (T) typeConversion.StringToDouble(param);
        } else if(Integer.class.isAssignableFrom(clazz)){
            result = (T) typeConversion.StringToInteger(param);
        } else if(Long.class.isAssignableFrom(clazz)){
            result = (T) typeConversion.StringToLong(param);
        } else if(Float.class.isAssignableFrom(clazz)){
            result = (T) typeConversion.StringToFloat(param);
        } else if(String.class.isAssignableFrom(clazz)){
            result = (T) param;
        }
        return result;
    }
}
