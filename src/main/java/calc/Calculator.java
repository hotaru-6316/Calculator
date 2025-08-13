/**
 * 
 */
package calc;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 電卓を表すインターフェース。
 */
public interface Calculator {
	
	/**
	 * 電卓画面を表示して入力できるようにします。
	 * このメソッドは終了後呼び出し元に帰ります。
	 */
	void display();

	/**
	 * xとyを足した値を返します
	 * @param x
	 * @param y
	 * @return 計算結果
	 */
	default double plus(double x, double y) {
		return BigDecimal.valueOf(x).add(BigDecimal.valueOf(y)).doubleValue();
	}

	/**
	 * xからyを引いた値を返します
	 * @param x
	 * @param y
	 * @return 計算結果
	 */
	default double minus(double x, double y) {
		return BigDecimal.valueOf(x).subtract(BigDecimal.valueOf(y)).doubleValue();
	}

	/**
	 * xとyを掛けた値を返します
	 * @param x
	 * @param y
	 * @return 計算結果
	 */
	default double multiply(double x, double y) {
		return BigDecimal.valueOf(x).multiply(BigDecimal.valueOf(y)).doubleValue();
	}

	/**
	 * xからyを割った値を返します。
	 * 計算結果が循環小数である場合は、15桁目で丸められます。
	 * @param x
	 * @param y
	 * @throws ArithmeticException 0で割ろうとした場合
	 * @return 計算結果
	 */
	default double divide(double x, double y) {
		if (y == 0) {
			throw new ArithmeticException("0で割ることは出来ません");
		}
		return BigDecimal.valueOf(x).divide(BigDecimal.valueOf(y), 15, RoundingMode.HALF_UP).doubleValue();
	}
	
	/**
	 * 例外の情報をコンソールに表示します。
	 * @param e 例外
	 */
	public static void printStackTrace(Throwable e) {
		e.printStackTrace();
	}
	
	/**
	 * CUI用の電卓を初期化して渡します
	 * @return CUI用の電卓クラス
	 */
	public static Calculator getCUICalc() {
		return new CUICalculator();
	}
	
	/**
	 * GUI用の電卓を初期化して渡します
	 * @return GUI用の電卓クラス
	 */
	public static Calculator getGUICalc() {
		return new GUICalculator();
	}

}
