package top.ezttf.graduation.index;

/**
 * 索引
 *
 * @author yuwen
 * @date 2019/4/27
 */
public interface IIndexAware<K, V> {

    /**
     * 通过索引key获取值
     * @param key key
     * @return value
     */
    V get(K key);

    /**
     * 添加索引
     * @param key Key
     * @param value value
     */
    void add(K key, V value);

    /**
     * 更新索引
     * @param key Key
     * @param value value
     */
    void update(K key, V value);

    /**
     * 删除索引
     * @param key key
     * @param value value
     */
    void delete(K key, V value);
}
