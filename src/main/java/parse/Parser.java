package parse;

import calc.Calculator;
import item.CalcResult;
import item.FormulaItem;

/**
 * 入力された数式を解析し、計算するクラスであることを表しています
 */
public interface Parser {

	/**
	 * 入力された数式を解析し、計算します
	 * @param item 解析する数式
	 * @param calc 計算に使用する計算機
	 * @return 計算結果
	 * @throws ParseException 処理中にエラーが発生した場合
	 */
	CalcResult parseAndCalc(FormulaItem item, Calculator calc) throws ParseException;

}
