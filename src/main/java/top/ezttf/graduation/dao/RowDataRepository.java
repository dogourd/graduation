package top.ezttf.graduation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.ezttf.graduation.entity.MySqlRowData;

/**
 * @author yuwen
 * @date 2019/3/6
 */
public interface RowDataRepository extends JpaRepository<MySqlRowData, Long> {
}
