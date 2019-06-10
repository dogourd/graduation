package top.ezttf.graduation.dao;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yuwen
 * @date 2019/6/1
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class Demo {

    @Test
    public void test() {
        List<Integer> list = Lists.newArrayList(1, 1, 2, 3, 5, 5, 9, 11);
        list = list.stream().distinct().collect(Collectors.toList());
        list.forEach(System.out::println);
    }

    @Test
    public void test2() {
        List<String> one = Lists.newArrayList("java, python");
        List<String> two = Lists.newArrayList(one);
        List<Data> list = Lists.newArrayList(
                new Data("java", one),
                new Data("python", two)
        );
        list = list.stream().distinct().collect(Collectors.toList());
        System.out.println(list);
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    class Data {
        private String jobTitle;
        private List<String> jobDetails;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return jobDetails.equals(data.jobDetails);
        }

        @Override
        public int hashCode() {
            return Objects.hash(jobDetails);
        }
    }
}
