package calc;

import javax.swing.JOptionPane;

import gui.view.WindowInitializer;

/**
 * GUIで動作する計算機クラスです。
 */
final class GUICalculator implements Calculator {
	
	/**
	 * インスタンスを初期化します。
	 */
	public GUICalculator() {}
	
	/**
	 * 捕捉されない例外が発生したときにJVMを終了するかどうか。<br>
	 * JUnitテスト実行中にJVMが停止しないようにするために使います。
	 */
	private static boolean exitVM = true;

	@Override
	public void display() {
		Thread.setDefaultUncaughtExceptionHandler((th, e) -> {
			JOptionPane.showMessageDialog(null, "電卓実行中に致命的なエラーが発生したため終了します。", "エラー!", JOptionPane.ERROR_MESSAGE);
			Calculator.printStackTrace(e);
			if(exitVM) System.exit(1);
		});
		new WindowInitializer().init();
	}

	@Override
	public String toString() {
		return "GUICalculator []";
	}
	

}
