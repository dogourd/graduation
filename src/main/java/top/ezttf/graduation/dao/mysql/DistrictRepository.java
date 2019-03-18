package top.ezttf.graduation.dao.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import top.ezttf.graduation.entity.District;

/**
 * @author yuwen
 * @date 2019/3/6
 */
public interface DistrictRepository extends JpaRepository<District, Long> {
}
