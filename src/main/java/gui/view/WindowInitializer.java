package gui.view;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;

/**
 * {@link calc.GUICalculator GUICalculator}から電卓ウィンドウを作成して表示するためのクラスです。
 */
public final class WindowInitializer {
	
	/**
	 * インスタンスを初期化します。
	 */
	public WindowInitializer() {}

	/**
	 * 電卓ウィンドウを作成して表示します。<br>
	 * このメソッドは電卓ウィンドウが閉じるまで呼び出し元に返りません。<br>
	 * このメソッドの実行中に他のスレッドから割り込まれた場合、表示中の電卓ウィンドウを破棄してから<br>
	 * 呼び出し元に返ります。
	 */
	public void init() {
		try {
			AtomicBoolean bool = new AtomicBoolean(true);
			try {
				SwingUtilities.invokeAndWait(() -> {
					bool.set(CalcWindowFrame.getCurrentWindow().isShowing());
				});
				while(bool.get()) {
					Thread.sleep(Duration.ofMillis(100));
					SwingUtilities.invokeAndWait(() -> {
						bool.set(CalcWindowFrame.getCurrentWindow().isShowing());
					});
				}
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getCause());
			}
		} catch (InterruptedException e) {
			try {
				SwingUtilities.invokeAndWait(() -> {
					CalcWindowFrame.getCurrentWindow().dispose();
				});
			} catch (InvocationTargetException e1) {
				throw new RuntimeException(e1.getCause());
			} catch (InterruptedException e1) {;}
		}
	}

}
