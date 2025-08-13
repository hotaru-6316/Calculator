package calc;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

import gui.TestExceptionHandler;
import gui.view.CalcWindowFrame;
import gui.view.CalcWindowFrame.CalcMode;
import util.ReflectionUtility;

/**
 * {@link GUICalculator}をテストするクラスです。
 */
public final class GUICalculatorTest extends CalculatorTest {
	
	/**
	 * このクラスのインスタンスを作成します。
	 */
	public GUICalculatorTest() {
		super.init(new GUICalculator());
	}
	
	/**
	 * {@link GUICalculator#display() display()}メソッドの動作をテストします。<br>
	 * このメソッドでは{@link GUICalculator#display() display()}メソッドを3秒後に終了します。<br>
	 * 一切例外がスローされなければ成功です。
	 */
	@Test
	void 電卓表示画面の表示テスト() {
		startDisplayAutoClose(3, true);
		TestExceptionHandler.getHandler().checkFail();
	}
	
	/**
	 * {@link GUICalculator#display() display()}メソッド内で定義するUncaughtExceptionHandlerをテストします。<br>
	 * このテスト中、JVMが終了しないよう、{@link GUICalculator#exitVM exitVM}変数をtrueに書き換えます。<br>
	 * 例外が発生した場合、NoSuchFieldException等、GUICalculator内のソースコードに原因があると予想される場合、AssertionFailedErrorが発生します。<br>
	 * InaccessibleObjectException等、原因が予想できなかったり、テストコード内に原因があると予想される場合、RuntimeExceptionが発生します。
	 * @throws RuntimeException 発生した例外の原因が予想できなかったり、テストコード内に問題があると予想される場合
	 */
	@Test
	void UncaughtExceptionHandlerテスト() {
		startDisplayAutoClose(3, false);
		setExitVMUsingReflection(false);
		Thread.startVirtualThread(() -> {
			throw new RuntimeException();
		});
		try {
			Thread.sleep(Duration.ofSeconds(1));
		} catch (InterruptedException e) {;}
	}

	private void setExitVMUsingReflection(boolean value) {
		ReflectionUtility.setFieldValue(GUICalculator.class, null, "exitVM", value);
	}

	/**
	 * {@link GUICalculator#display() display()}メソッドの動作を指定の秒数経過した後に終了させるメソッドです。
	 * @param waitSecs 待機時間(秒)
	 * @param doOverrideExHandler 捕捉されない例外を処理するUncaughtExceptionHandlerを上書きするかどうか
	 */
	private void startDisplayAutoClose(long waitSecs, boolean doOverrideExHandler) {
		Thread vThread = Thread.startVirtualThread(() -> {
			try {
				Thread.sleep(Duration.ofSeconds(waitSecs));
				SwingUtilities.invokeAndWait(() -> {
					CalcWindowFrame.getWindow(CalcMode.Simple).dispose();
				});
			} catch (InterruptedException e) {;
			} catch (InvocationTargetException e) {
				fail(e.getCause());
			}
		});
		if(doOverrideExHandler) Thread.setDefaultUncaughtExceptionHandler(TestExceptionHandler.getHandler());
		new GUICalculator().display();
		try {
			vThread.join();
		} catch (InterruptedException e) {;}
	}

}
