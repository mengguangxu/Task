package com.ssm.controller;

import com.ssm.model.User;
import com.ssm.service.UserService;
import com.ssm.utils.DesUtil;
import com.ssm.utils.UploadContextUtil;
import com.ssm.utils.UploadToOSSUtil;
import com.ssm.utils.UploadToQiniuUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Enzo Cotter on 18/4/2.
 */
@Controller
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UploadContextUtil uploadContextUtil;

    //跳转到上传页面
    @RequestMapping(value = "/u/photo",method = RequestMethod.GET)
    public String toUpload() {
        return "upload";
    }

    //跳转到添加页面
    //上传图片
    @RequestMapping(value = "/u/user", method = RequestMethod.POST)
    public String toAdd(HttpServletRequest request,@RequestParam("photoPath") CommonsMultipartFile photoPath) throws Exception {
        //获取原始文件名
        String originalFilename = photoPath.getOriginalFilename();
        logger.info("originalFilename:"+originalFilename);
        //截取后缀名
        String[] arrayFile = originalFilename.split("\\.");
        String extendName = arrayFile[1];
        //生成上传文件名
        String fileName = String.valueOf(System.currentTimeMillis()) + "." + extendName;
        logger.info("上传文件名："+fileName);

        InputStream inputStream = photoPath.getInputStream();

        //策略模式
        uploadContextUtil.executeStrategy(fileName,inputStream);

        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("token")) {
                String decrypted = DesUtil.decrypt(c.getValue());
                String[] strKey = decrypted.split("\\.");
                redisTemplate.opsForValue().set(strKey[0],fileName,60*30, TimeUnit.SECONDS);
            }
        }
        return "addUser";
    }

    //添加用户
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public String addUser(User user,HttpServletRequest request) throws Exception {
        logger.info("/users POST {}" + user.toString());

        String pictureLink = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("token")) {
                String decrypted = DesUtil.decrypt(c.getValue());
                String[] strKey = decrypted.split("\\.");
                pictureLink= (String) redisTemplate.opsForValue().get(strKey[0]);
            }
        }
        logger.info("pictureLink::"+pictureLink);
        user.setPicture(pictureLink);
        user.setCreate_at(System.currentTimeMillis());
        user.setUpdate_at(System.currentTimeMillis());
        userService.addUser(user);
        return "redirect:/u/users";
    }

    //删除用户
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public String deleteUser(@PathVariable("id") Integer id) {
        logger.info("/users/{id} DELETE the incoming ID is:{}",id);
        userService.deleteUser(id);
        return "redirect:/u/users";
    }

    //修改用户
    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public String updateUser(User user) {
        logger.info("/users PUT the user's parameters are " +user.toString());
        user.setUpdate_at(System.currentTimeMillis());
        userService.updateUser(user);
        return "redirect:/u/users";
    }


    //查询单个用户并跳转到编辑页面
    @RequestMapping(value = "/u/users/{id}", method = RequestMethod.GET)
    public String getUser(@PathVariable("id") Integer id, Model model) {
        logger.info("/users/{id} GET the information is:{}",userService.getById(id));
        User user=userService.getById(id);
        model.addAttribute("user",user);
        return "editUser";
    }

    //查询所有用户
    @RequestMapping(value = "/u/users", method = RequestMethod.GET)
    public String getAllUser(Model model)  {
        List<User> pageInfo=userService.getAll();
        model.addAttribute("pageInfo", pageInfo);
        return "allUsers";
    }

    //首页
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home()  {
        return "index";
    }
}
