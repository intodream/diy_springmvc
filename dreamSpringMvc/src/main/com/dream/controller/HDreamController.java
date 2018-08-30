package dream.controller;

import dream.annotation.*;
import dream.service.HDreamService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@DreamController
@DreamRequestMapping("/dream")
public class HDreamController {

    @DreamAuwowired("iHDreamImpl")
    private HDreamService dreamService;


    @DreamRequestMapping("/user")
    public void queryUserInfo(HttpServletRequest request, HttpServletResponse response,
                              @DreamRequestParam("name") String name, @DreamRequestParam("age") String age){
        String userInfo = dreamService.queryStr(name, age);
        PrintWriter writer = null;
        try{
            writer = response.getWriter();
            writer.write(userInfo);
        } catch (Exception e){
            writer.close();
        }
    }

    @DreamRequestMapping("/user/v2")
    public void queryUserInfoV2(HttpServletResponse response, @DreamRequestParam("name") String name,
                                @DreamRequestParam("age") Integer age, @DreamRequestParam("sex") Long sex){
        String userInfo = dreamService.queryUser(name, age, sex);
        PrintWriter writer = null;
        try{
            writer = response.getWriter();
            writer.write(userInfo);
        } catch (Exception e){
            writer.close();
        }
    }

//    @DreamRequestMapping("/user/{name}/{age}/{sex}")
//    public void queryUserInfoByPathParams(HttpServletResponse response, @DreamPathVariable("name") String name,
//                                          @DreamPathVariable("age") Integer age, @DreamPathVariable Long sex){
//        String userInfo = dreamService.queryUser(name, age, sex);
//        PrintWriter writer = null;
//        try{
//            writer = response.getWriter();
//            writer.write(userInfo);
//        } catch (Exception e){
//            writer.close();
//        }
//    }
}
