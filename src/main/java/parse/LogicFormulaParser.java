package parse;

import calc.Calculator;
import item.CalcResult;
import item.FormulaItem;

/**
 * 左から右に計算の優先順位を基に数式を解析し、順次計算するクラスです。<br />
 * 注意：このクラスでは、掛け算や割り算が含まれていると、それを先に計算します。<br />
 * 単純に左から右に計算する場合はSimpleFormulaParserが早いです。<br />
 */
public class LogicFormulaParser extends AbstractParser {
	
	/**
	 * LogicFormulaParserインスタンス
	 */
	private static LogicFormulaParser parser = new LogicFormulaParser();
	
	/**
	 * LogicFormulaParserインスタンスを取得します。
	 * @return LogicFormulaParserインスタンス
	 */
	public static LogicFormulaParser getParser() {
		return parser;
	}
	
	/**
	 * クラスを初期化します
	 */
	protected LogicFormulaParser() {}

	@Override
	public CalcResult parseAndCalc(FormulaItem item, Calculator calc) throws ParseException {
		item = this.parseAndCalc(item, calc, ParseMode.PARENTHESES);
		item = this.parseAndCalc(item, calc, ParseMode.MULTIPLY, ParseMode.DIVIDE);
		item = this.parseAndCalc(item, calc, ParseMode.PLUS, ParseMode.MINUS);
		try {
			return new CalcResult(Double.parseDouble(item.get().replace('=', (char)0)));
		} catch (NullPointerException | NumberFormatException e) {
			throw new ParserError("CalcResult生成中にエラーが発生しました。itemの値が不正である可能性があります: " + item, e);
		}
	}

	@Override
	public String toString() {
		return "LogicFormulaParser []";
	}

}
