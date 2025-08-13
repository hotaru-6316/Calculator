package gui;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

/**
 * GUI関連のテストを行うクラスに便利な機能を実装しています。
 */
public abstract class GUITest {

	/**
	 * AWTのEDTスレッドなど他のスレッドで発生した捕捉されなかった例外を補足する準備をします。
	 */
	final protected void prepare() {
		Thread.setDefaultUncaughtExceptionHandler(TestExceptionHandler.getHandler());
	}
	
	/**
	 * 他のスレッドで発生した例外を再度スローし直します。
	 * @throws Error Errorが発生していた場合、そのままスローし直します。
	 * @throws RuntimeException エラー以外(Exception等)の例外が発生した場合、RuntimeExceptionにラップしてからスローし直します。
	 */
	final protected void finish() {
		assertDoesNotThrow(() -> {
			TestExceptionHandler.getHandler().checkFail();
		});
	}
	
	/**
	 * runをEDTスレッド上で実行します。<br>
	 * 実行中に発生した例外は、それがエラー(Error)の場合はそのままスローし直します。<br>
	 * それ以外の場合(Exception等)、fail()でテスト失敗になります。<br>
	 * このメソッドは、内部で{@link SwingUtilities#invokeAndWait(Runnable)}を使用します。<br>
	 * ですが、現在がEDTスレッドの場合、そのままrunを実行するだけなので、<br>
	 * EDTから呼び出しても安全です。
	 * @param run EDTスレッド上で実行するRunnable
	 */
	final protected void runInEDT(Runnable run) {
		if (SwingUtilities.isEventDispatchThread()) {
			run.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(run);
			} catch (InvocationTargetException e) {
				if(e.getCause() instanceof Error) {
					throw ((Error) e.getCause());
				} else {
					fail(e.getCause());
				}
			} catch (InterruptedException e) {
				fail("別のスレッドから割り込まれました");
			}
		}
	}

}
