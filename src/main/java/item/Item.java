package item;

/**
 * 数式などの単一データを保有するクラスであることを表します
 * @param <T> 扱うデータ型
 */
public interface Item<T> {

    /**
     * 保存されたデータを取得します
     * @return 保存されたデータ
     */
    T get();

    /**
     * 指定されたデータを保存します
     * @param t 指定のデータ
     * @throws IllegalArgumentException 指定のデータがnullの時
     * @throws UnsupportedOperationException データの読み込みのみ可能な場合
     */
    void set(T t);
}
