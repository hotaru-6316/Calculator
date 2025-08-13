package gui.view;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import gui.GUITest;
import util.ReflectionUtility;

/**
 * GUIのテストの中で、フレームの部品に関するテストを行うクラス用に<br>
 * テストの準備とテストの後片付けを行います。内容は各メソッドを参照してください。<br>
 * このクラスはフレームの部品用のクラスです。フレームそのもののテストには使えません。
 */
abstract class GUIFrameTest extends GUITest {
	
	/**
	 * {@link GUITest#prepare()}を実行し、テスト用に電卓ウィンドウを作成します。
	 */
	@BeforeEach
	private void prepareFrameTest() {
		runInEDT(() -> {
			if (CalcWindowFrame.getCurrentWindow() != null) {
				disposeWindowFrameForTesting();
			}
		});
		super.prepare();
		createWindowFrameForTesting();
	}
	
	/**
	 * テスト用の電卓ウィンドウを閉じ、{@link GUITest#finish()}を実行します。
	 * @throws Error Errorが発生していた場合、そのままスローし直します。
	 * @throws RuntimeException エラー以外(Exception等)の例外が発生した場合、RuntimeExceptionにラップしてからスローし直します。
	 */
	@AfterEach
	private void finishFrameTest() {
		disposeWindowFrameForTesting();
		super.finish();
	}

	/**
	 * テスト用の電卓ウィンドウを作成し、表示します。<br>
	 * このウィンドウにはタイトルの先頭に"JUnitでテスト中 - "が追記されています。
	 */
	private void createWindowFrameForTesting() {
		runInEDT(() -> {
			CalcWindowFrame frame = CalcWindowFrame.getCurrentWindow();
			frame.setTitle("JUnitでテスト中 - " + frame.getTitle());
		});
	}

	/**
	 * テスト用の電卓ウィンドウを破棄します。
	 */
	private void disposeWindowFrameForTesting() {
		runInEDT(() -> {
			CalcWindowFrame.getCurrentWindow().dispose();
		});
		assertDoesNotThrow(() -> {
			ReflectionUtility.setFieldValue(CalcWindowFrame.class, null, "window", null);
		});
	}
	
}
