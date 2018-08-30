package dream.service;

import dream.annotation.DreamService;

@DreamService("iHDreamImpl")
public class IHDreamImpl implements HDreamService {

    @Override
    public String queryStr(String name, String age) {
        return "name = " + name + "age = " + age;
    }

    @Override
    public String queryUser(String name, Integer age, Long sex) {
        return "name = " + name + "age = " + age + "sex = " + sex;
    }
}
