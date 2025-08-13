package gui.view;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import gui.GUITest;
import gui.view.CalcWindowFrame.CalcMode;
import parse.LogicFormulaParser;
import parse.Parser;
import parse.SimpleFormulaParser;

/**
 * ウィンドウフレームの動作テストを行うクラスです。
 */
public final class CalcWindowFrameTest extends GUITest {
	
	/**
	 * {@link CalcMode}の正しい定数名とタイトルとパーサを保管するクラスです。
	 */
	private static enum CalcModeMap {
		
		/**
		 * {@link CalcMode#Simple}をテストするときに使う定数です。
		 */
		Simple("通常電卓", SimpleFormulaParser.getParser()),
		
		/**
		 * {@link CalcMode#Logic}をテストするときに使う定数です。
		 */
		Logic("四則計算電卓", LogicFormulaParser.getParser());
		
		/**
		 * テストに使用するタイトル
		 */
		final private String TITLE;
		
		/**
		 * テストに使用するパーサ
		 */
		final private Parser PARSER;

		/**
		 * このクラスをインスタンス化します。
		 * @param title タイトル
		 * @param parser パーサ
		 */
		private CalcModeMap(String title, Parser parser) {
			TITLE = title;
			PARSER = parser;
		}

		/**
		 * テストに使用するタイトルを返します。
		 * @return タイトル
		 */
		public String getTitle() {
			return TITLE;
		}

		/**
		 * テストに使用するパーサを返します。
		 * @return パーサ
		 */
		public Parser getParser() {
			return PARSER;
		}
		
	}
	
	/**
	 * 電卓ウィンドウに使用するデフォルトの動作モード
	 */
	final private static CalcMode CALC_FRAME_DEF_MODE = CalcMode.Simple;
	
	/**
	 * デフォルトの動作モードの電卓ウィンドウが取得できるか確認します。
	 */
	@Test public void デフォルトモードの電卓ウィンドウ取得テスト() {
		runInEDT(() -> {
			assertEquals(CALC_FRAME_DEF_MODE, CalcWindowFrame.getCurrentWindow().getCalcMode());
		});
	}
	
	/**
	 * {@link CalcModeMap}を使って{@link CalcMode}のすべての定数が正しく定義されているか確認します。<br>
	 * CalcModeMapとCalcModeの定数の数があっていない場合も失敗します。
	 */
	@Test public void CalcMode全定数の定義チェック() {
		if (CalcModeMap.values().length != CalcMode.values().length) {
			fail("CalcModeMapのテスト数とCalcModeの実装数が合っていません");
		}
		for(CalcModeMap map : CalcModeMap.values()) {
			try {
				CalcMode mode = CalcMode.valueOf(map.name());
				assertEquals(map.getTitle(), mode.getTitle());
				assertEquals(map.getParser().getClass(), mode.getParser().getClass());
			} catch (IllegalArgumentException e) {
				fail(map.name() + "がCalcMode内に見つかりませんでした");
			}
		}
	}
	
	/**
	 * 電卓ウィンドウの動作モードを変更する動作をテストします。<br>
	 * {@link CalcWindowFrame#getWindow(CalcMode)}で、反対の動作モードに切り替わるように実行します。
	 */
	@Test public void 動作モード切替テスト() {
		runInEDT(() -> {
			CalcMode mode = switch (CalcWindowFrame.getCurrentWindow().getCalcMode()) {
				case CalcMode.Simple -> CalcMode.Logic;
				case CalcMode.Logic -> CalcMode.Simple;
			};
			CalcWindowFrame.getWindow(mode);
			assertEquals(mode, CalcWindowFrame.getCurrentWindow().getCalcMode());
		});
	}
	
	/**
	 * 電卓ウィンドウの{@link CalcWindowFrame#getWindow(CalcMode)}をEDT外で実行し、例外が発生するかテストします。
	 */
	@Test public void EDT外で電卓ウィンドウ取得テスト() {
		IllegalCallerException e = assertThrows(IllegalCallerException.class, () -> {
			CalcWindowFrame.getWindow(CalcMode.Simple);
		});
		System.out.println("### testGetWindowFromNotEDT - 例外の情報 ###");
		printThrowable(e);
		System.out.println("### ここまで ###");
	}
	
	/**
	 * 電卓ウィンドウの{@link CalcWindowFrame#getCurrentWindow()}をEDT外で実行し、例外が発生するかテストします。
	 */
	@Test public void EDT外で表示中のウィンドウ取得テスト() {
		runInEDT(() -> {
			CalcWindowFrame.getWindow(CalcMode.Simple);
		});
		IllegalCallerException e = assertThrows(IllegalCallerException.class, () -> {
			CalcWindowFrame.getCurrentWindow();
		});
		System.out.println("### testGetCurrentWindowFromNotEDT - 例外の情報 ###");
		printThrowable(e);
		System.out.println("### ここまで ###");
	}
	
	/**
	 * {@link #EDT外で電卓ウィンドウ取得テスト()}と{@link #EDT外で表示中のウィンドウ取得テスト()}で<br>
	 * 発生した例外情報を<code>System.out</code>に出力します。<br>
	 * 出力される例外のスタックトレースは、情報を整理し、見やすくするために、<br>
	 * スタックトレースにこのクラスの情報が出現するより前のスタックトレースは省略されます。
	 * @param th 発生した例外情報
	 */
	private void printThrowable(Throwable th) {
		System.out.println(th.toString());
		List<StackTraceElement> stes = new ArrayList<>(Arrays.asList(th.getStackTrace())).reversed();
		boolean out = false;
		List<StackTraceElement> steOuts = new ArrayList<>();
		int skipCount = 0;
		for (StackTraceElement ste : stes) {
			if (ste.getClassName().equals(this.getClass().getName())) {
				out = true;
			}
			if (out) {
				steOuts.add(ste);
			} else {
				skipCount++;
			}
		}
		for (StackTraceElement steOut : steOuts.reversed()) {
			System.out.println("	at " + steOut.toString());
		}
		if (skipCount >= 1) {
			System.out.println("	... 以下、 " + skipCount + " 個のスタックトレースがあります");
		}
	}

}
