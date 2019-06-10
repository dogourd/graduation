package top.ezttf.graduation.noweb;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yuwen
 * @date 2019/3/19
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MapTest {

    @Test
    public void testConcurrentHashMap() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.computeIfAbsent("AaAa", key -> map.put("BBBB", "value"));

        List<String> list = Lists.newArrayList("1", "1", "2", "1", "3", "5", "3");
        list = list.stream().distinct().collect(Collectors.toList());
        System.out.println(list);




    }









     






}
