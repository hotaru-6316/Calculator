package parse;

/**
 * パーサで想定されていない例外スローが発生した場合、アプリケーションを即座に停止するために使用します。<br>
 * このエラーでプログラムが停止した場合、想定と違う動作がどこかで起きたことを指します。
 */
public final class ParserError extends Error {

	/**
	 * このエラーをmessageとcauseでインスタンス化
	 * @param message エラーメッセージ
	 * @param cause エラーの原因
	 */
	ParserError(String message, Throwable cause) {
		super(message, cause);
	}

}
