package top.ezttf.graduation.withoutspring;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuwen
 * @date 2019/3/19
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class One {

    private List<Integer> list = new ArrayList<>();

    @Before
    public void setup() {
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
    }

    @Test
    public void test() {
        StringBuilder builder = new StringBuilder();
        list.forEach(e -> {
            builder.append(e.toString()).append(": ").append("\n");
        });
        log.debug(builder.toString());
    }

}
