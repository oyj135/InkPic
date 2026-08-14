package com.yj.inkpic.mybaitsplus;

import com.yj.inkpic.mapper.UserMapper;
import com.yj.inkpic.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/14
 */
@SpringBootTest()
public class MybatisPlusTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        // selectList() 方法，不填写就是没有任何条件
        List<User> userList = userMapper.selectList(null);
        Assert.isTrue(5 == userList.size(), "查询结果错误");
        userList.forEach(System.out::println);

    }
}
