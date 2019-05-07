package top.ezttf.graduation.withoutspring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import top.ezttf.graduation.pojo.Device;

import java.util.Date;
import java.util.List;

/**
 * @author yuwen
 * @date 2019/3/19
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class One {

    @Test
    public void test() throws JsonProcessingException {
        List<Device> list = Lists.newArrayList(
                new Device(7L, "mMac7", "呼和浩特", new Date(), new Date()),
                new Device(5L, "mMac5", "乌鲁木齐", new Date(), new Date())
        );
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(list));
    }

}
