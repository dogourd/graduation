package top.ezttf.graduation.withoutspring;

import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
        System.out.println(1);
        System.out.println(2 & 1);
    }

    @Test
    public void distinct() {
        List<String> list = Arrays.asList("A", "A", "B", "A", "C", "C", "D", "A");
        List<String> newList = new ArrayList<>(list);
        String last = "";
        Iterator<String> iterator = newList.iterator();
        for (;iterator.hasNext();) {
            String next = iterator.next();
            if (StringUtils.equals(next, last)) {
                iterator.remove();
            } else {
                last = next;
            }
        }
        newList.forEach(e -> System.out.print(e + " "));
        System.out.println();

        List<Pair<String, String>> deepList = new ArrayList<>();
        for (int i = 0; i < newList.size() - 1; i++) {
            Pair<String, String> pair = new Pair<>(newList.get(i), newList.get(i + 1));
            deepList.add(pair);
        }
    }

    @Test
    public void subString() {
        String rowKey = "mac1-sjdklfj-sdlfk";
        rowKey = StringUtils.substring(rowKey, 0, rowKey.indexOf("-"));
        System.out.println(rowKey);
    }

}
