package com.quick.base.controller;

import com.alibaba.excel.EasyExcel;
import com.quick.auth.entity.User;
import com.quick.auth.service.UserService;
import com.quick.common.utils.flie.dto.WordToPdfTemplateParams;
import com.quick.common.utils.flie.WordToPdfUtil;
import com.quick.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Api(tags = "测试模块")
public class TestController {

    @Autowired
    UserService userService;

    @GetMapping("/test")
    @ApiOperation(value = "测试接口")
    public Result testPage() {
        log.info("访问测试接口");
        return Result.ok(111);
    }

    @RequestMapping(value = "/fileExp", method = RequestMethod.GET)
    @ApiOperation(value = "wenjian")
    public void fileExp(HttpServletResponse response) throws Exception {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 500000; i++) {
            User user = new User();
            user.setUsername("张三" + i);
            user.setPassword(i + "");
            user.setEmail("13152232@163.com");
            user.setLastLoginTime(new Date());
            userList.add(user);
        }
        // 设响应头response信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("测试", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        // 写入数据到Excel中
        EasyExcel.write(response.getOutputStream(), User.class).sheet("模板").doWrite(userList);
    }

    @RequestMapping(value = "/exportUserInfo", method = RequestMethod.GET)
    @ApiOperation(value = "导出用户信息Word")
    public void exportUserInfoWord(String username, HttpServletResponse response) throws Exception {
        User userInfo = userService.findUserAllInfoInfoByUsername(username);

        WordToPdfUtil.setResponseInfo(response, "用户信息.pdf");
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("name", userInfo.getUsername());
        objectHashMap.put("userId", userInfo.getUserId());
        objectHashMap.put("phone", userInfo.getPhone());
        objectHashMap.put("email", userInfo.getEmail());
        objectHashMap.put("sex", userInfo.getSex().equals(1) ? "男" : "女");
        objectHashMap.put("age", userInfo.getAge());
        objectHashMap.put("status", userInfo.getStatus().equals(1) ? "有效" : "删除");

        List<String> roleNames = userInfo.getRoles().stream().map(o -> o.getName()).collect(Collectors.toList());
        String roleName = "";
        for (String role : roleNames) {
            roleName += role + "、";
        }
        objectHashMap.put("roleName", roleName);
        Map<String, Object> imgInfo = new HashMap<>();
        imgInfo.put("src", userInfo.getHeadPortrait());
        imgInfo.put("src", "");
        imgInfo.put("w", 110);
        imgInfo.put("h", 130);
        objectHashMap.put("headPortrait", imgInfo);

        WordToPdfTemplateParams wordToPdfTemplateParams = WordToPdfTemplateParams.createTemplateParams("userInfo.docx");
        wordToPdfTemplateParams.setTextParams(objectHashMap);


        byte[] bytes = WordToPdfUtil.wordTemplateGeneratePdf(wordToPdfTemplateParams);
        response.getOutputStream().write(bytes);
    }

}
