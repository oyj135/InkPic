package com.yj.inkpic.enumTest;

import com.yj.inkpic.model.enums.UserRoleEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/14
 */
@SpringBootTest
public class EnumTest {

    @Test
    public void userRoleEnum(){
        UserRoleEnum admin = UserRoleEnum.getEnumByValue("admin");
        System.out.println(admin);
    }
}
