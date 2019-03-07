package top.ezttf.graduation.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.ezttf.graduation.entity.MySqlRowData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuwen
 * @date 2019/3/6
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class RowDataRepositoryTest {

    @Autowired
    private RowDataRepository rowDataRepository;

    @Test
    public void testQuery() throws InterruptedException {
        List<MySqlRowData> rowDatas = rowDataRepository.findAll();
        rowDatas.stream().collect(
                Collectors.groupingBy(MySqlRowData::getMac, Collectors.toList())
        ).forEach((mac, mySqlRowDatas) -> mySqlRowDatas.forEach(rowData -> {
            log.debug(rowData.getMac() + "------" + rowData.getDeviceId());
        }));
        Thread.sleep(Long.MAX_VALUE);
    }
}
