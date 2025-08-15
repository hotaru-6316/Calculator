package util;

/**
 * リフレクション中に発生した例外の中で、想定されていない例外が発生した場合にスローされるクラスです。<br>
 * このクラスがスローされた場合、(「アクセス制御を無効化できなかった」や、「アクセス制御を無効化しているのにアクセスエラーが発生した」等)予期せぬ状況が発生したことを表します。
 */
public class ReflectionUtilUnexpectedException extends RuntimeException {

	/**
	 * このクラスをインスタンス化します。
	 * @param message エラーの詳細メッセージ
	 * @param cause そのエラーの原因となったエラーまたは例外
	 */
	public ReflectionUtilUnexpectedException(String message, Throwable cause) {
		super(message, cause);
	}

}
