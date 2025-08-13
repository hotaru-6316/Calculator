package input;

/**
 * 入力を処理するための各種メソッドが実装している抽象クラスです
 */
abstract class AbstractInputer implements Inputer {
	
	/**
	 * クラスを初期化します
	 */
	public AbstractInputer() {
	}

    /**
	 * このオブジェクトの文字列表現を返します
	 * @return このオブジェクトの文字列表現
	 */
    @Override
    public abstract String toString();

}
