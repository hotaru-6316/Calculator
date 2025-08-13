package gui;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 別スレッドで発生した例外を処理するために使用するクラスです。<br>
 * 例外やエラーが別スレッド上で発生したが、どのクラスでもキャッチしなかった場合にmainスレッド上でそれらの例外やエラーを投げなおすことで<br>
 * JUnitの結果を「エラー」や「失敗」にするためのクラスです。
 */
public final class TestExceptionHandler implements UncaughtExceptionHandler {
	
	/**
	 * 別のスレッドで発生したエラーや例外
	 */
	private Throwable throwable = null;
	
	/**
	 * そのエラーや例外を発生させたスレッド
	 */
	private Thread th = null;
	
	/**
	 * synchronized処理に使用するオブジェクト
	 */
	final private Object SYNC_OBJ = new Object();
	
	/**
	 * このクラスのインスタンス
	 */
	final static private TestExceptionHandler HANDLER = new TestExceptionHandler();
	
	/**
	 * このクラスのインスタンスを返します。<br>
	 * このクラスのインスタンスは他のすべてのクラスと共有されます。
	 * @return このクラスのインスタンス
	 */
	public static TestExceptionHandler getHandler() {
		return HANDLER;
	}
	
	/**
	 * このクラスのインスタンスを作成します。
	 */
	private TestExceptionHandler() {}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		this.setThrowable(t, e);
	}
	
	/**
	 * 例外を記録します。
	 * @param t 発生したスレッド
	 * @param e 発生した例外
	 */
	private void setThrowable(Thread t, Throwable e) {
		synchronized (SYNC_OBJ) {
			this.throwable = e;
			this.th = t;
		}
	}
	
	/**
	 * 他のスレッド上で例外やエラーが発生していないか確認します。
	 * @throws RuntimeException 他のスレッド上で例外が発生している場合、RuntimeExceptionでラップして再スローします
	 * @throws Error 他のスレッド上でエラーが発生している場合、そのまま(ラップせずに)再スローします
	 */
	public void checkFail() {
		synchronized (SYNC_OBJ) {
			if (this.th != null) {
				if (this.throwable instanceof Error) {
					throw ((Error) this.throwable);
				}
				throw new RuntimeException("スレッド \"" + th.getName() + "\" で例外が発生しました", throwable);
			}
		}
	}

}
