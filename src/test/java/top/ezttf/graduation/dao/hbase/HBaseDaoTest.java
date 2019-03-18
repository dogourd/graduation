package top.ezttf.graduation.dao.hbase;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * @author yuwen
 * @date 2019/3/7
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class HBaseDaoTest {

    @Autowired
    private HBaseDao hBaseDao;

    @Test
    public void testCreateTable() {
        hBaseDao.createTable("test", Arrays.asList("c1", "c2", "c3"));
    }
}
