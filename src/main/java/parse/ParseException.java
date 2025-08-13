package parse;

/**
 * 計算処理中にエラーが発生した場合にスローされます。<br>
 * このクラスは、計算処理中に例外が発生した場合、それらをラップして再スローするときに使用します。<br>
 * このクラスは、チェック例外のため、計算処理中に非チェック例外が発生しても電卓アプリが終了しません。
 */
public class ParseException extends Exception {

    /**
     * 例外クラスを初期化します
     * @param message エラーメッセージ
     * @param cause 原因となった例外クラス
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
