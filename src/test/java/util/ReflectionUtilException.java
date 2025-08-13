package util;

/**
 * リフレクション中に発生したチェック例外等を再スローするクラスです。<br>
 * このクラスは非チェック例外のため、それらの例外の処理を書く必要がなくなります。
 */
public class ReflectionUtilException extends RuntimeException {

	public ReflectionUtilException(Throwable cause) {
		super(cause);
	}

}
