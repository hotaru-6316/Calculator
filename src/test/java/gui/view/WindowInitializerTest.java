package gui.view;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

/**
 * {@link WindowInitializer}の動作をテストします。
 */
public final class WindowInitializerTest extends GUIFrameTest {

	/**
	 * {@link WindowInitializer#init()}の処理をテストします。<br>
	 * 1秒後に表示されたウィンドウを破棄して処理を終了します。<br>
	 * ウィンドウが表示不可になっていれば成功です。
	 */
	@Test public void 割り込みを使わないinit動作テスト() {
		Thread current = Thread.currentThread();
		Thread joinThread = Thread.startVirtualThread(() -> {
			this.threadRun(current, false);
		});
		new WindowInitializer().init();
		try {
			joinThread.join();
		} catch (InterruptedException e) {;}
		runInEDT(() -> {
			assertFalse(CalcWindowFrame.getCurrentWindow().isDisplayable());
		});
	}
	
	/**
	 * {@link WindowInitializer#init()}の処理をテストします。<br>
	 * 1秒後にスレッドに割り込んで処理を終了します。<br>
	 * ウィンドウが表示不可になっていれば成功です。
	 */
	@Test public void 割り込みを使ってinit動作テスト() {
		Thread current = Thread.currentThread();
		Thread joinThread = Thread.startVirtualThread(() -> {
			this.threadRun(current, true);
		});
		new WindowInitializer().init();
		try {
			joinThread.join();
		} catch (InterruptedException e) {;}
		runInEDT(() -> {
			assertFalse(CalcWindowFrame.getCurrentWindow().isDisplayable());
		});
	}
	
	/**
	 * 1秒後にcurrentに割り込むか、表示中の電卓ウィンドウを破棄します。
	 * @param current 現在のスレッド。このスレッドに割り込みます。
	 * @param interrupt スレッドに割り込むかどうか。<code>false</code>にするとスレッド割込みではなく表示中の電卓ウィンドウ破棄で終了しようとします。
	 */
	private void threadRun(Thread current, boolean interrupt) {
		try {
			Thread.sleep(Duration.ofSeconds(1));
		} catch (InterruptedException e) { ; }
		if (interrupt) {
			current.interrupt();
		} else {
			runInEDT(() -> {
				CalcWindowFrame.getCurrentWindow().dispose();
			});
		}
	}

}
