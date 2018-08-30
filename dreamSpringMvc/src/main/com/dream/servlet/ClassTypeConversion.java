package dream.servlet;

import dream.annotation.DreamAuwowired;
import dream.annotation.DreamService;

public class ClassTypeConversion implements TypeConversion {

    @Override
    public Double StringToDouble(String param) {
        return Double.valueOf(param);
    }

    @Override
    public Integer StringToInteger(String param) {
        return Integer.valueOf(param);
    }

    @Override
    public Long StringToLong(String param) {
        return Long.valueOf(param);
    }

    @Override
    public Float StringToFloat(String param) {
        return Float.valueOf(param);
    }
}
