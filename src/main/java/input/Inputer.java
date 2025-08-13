package input;

/**
 * ユーザーからの入力を処理するクラスであることを表すインターフェースです
 */
public interface Inputer {
	
	/**
	 * ユーザーからデータを入力してもらい、入力されたデータを返します
	 * @return 入力されたデータ
	 */
	String getLine();

}
