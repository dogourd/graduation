package top.ezttf.graduation.boot;

import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.ezttf.graduation.GraduationApplication;

/**
 * @author yuwen
 * @date 2019/3/5
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {GraduationApplication.class}
)
public class BootTest {
}
