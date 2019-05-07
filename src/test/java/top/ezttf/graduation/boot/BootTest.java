package top.ezttf.graduation.boot;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.ezttf.graduation.GraduationApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

    @Test
    public void testTime() {
        String pattern = "EEE MMM  dd HH:mm:ss yyyy";
        String time = "Fri Mar  15 21:29:37 2019";
        LocalDateTime dateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern(pattern, Locale.US));
        log.info("{}", dateTime);
    }
}
