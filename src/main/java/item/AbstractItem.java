package item;

/**
 * 各種データを格納するクラスのメソッドを実装しています。
 * 
 * @param <T> 格納するデータの型
 */
public abstract class AbstractItem<T> implements Item<T> {
	
	/**
	 * クラスを初期化します
	 */
	public AbstractItem() {
	}

	/**
	 * このオブジェクトと等値かどうかを評価します
	 * 
	 * @param obj 評価するオブジェクト
	 * @return 評価結果
	 */
	@Override
	public abstract boolean equals(Object obj);

	/**
	 * このオブジェクトのハッシュ値を計算します。
	 * 
	 * @return このオブジェクトのハッシュ値
	 */
	@Override
	public abstract int hashCode();

	/**
	 * このオブジェクトの文字列表現を返します
	 * 
	 * @return このオブジェクトの文字列表現
	 */
	@Override
	public abstract String toString();
	
	/**
	 * objがnullではないことを確認します。<br>
	 * データを格納する前に必ずこのメソッドを呼び出してください。
	 * @param obj 確認するオブジェクト
	 * @throws IllegalArgumentException objがnullの場合
	 */
	protected void checkNull(Object obj) {
		if (obj == null) {
			throw new IllegalArgumentException();
		}
	}

}
