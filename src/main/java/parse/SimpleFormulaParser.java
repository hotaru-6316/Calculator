package parse;

import calc.Calculator;
import item.CalcResult;
import item.FormulaItem;

/**
 * 左から右に数式を解析し、順次計算するクラスです。<br />
 * 注意：このクラスでは、掛け算や割り算が含まれていても左から右に解釈します。<br />
 * 掛け算や割り算の計算方法を考慮する場合はLogicFormulaParserを使用してください。
 */
public class SimpleFormulaParser extends AbstractParser {
	
	/**
	 * SimpleFormulaParserインスタンス
	 */
	private static SimpleFormulaParser parser = new SimpleFormulaParser();
	
	/**
	 * SimpleFormulaParserインスタンスを取得します。
	 * @return SimpleFormulaParserインスタンス
	 */
	public static SimpleFormulaParser getParser() {
		return parser;
	}
	
	/**
	 * クラスを初期化します
	 */
	protected SimpleFormulaParser() {}

    @Override
    public CalcResult parseAndCalc(FormulaItem item, Calculator calc) throws ParseException {
    	item = this.parseAndCalc(item, calc, ParseMode.PARENTHESES);
        item = this.parseAndCalc(item, calc, ParseMode.PLUS, ParseMode.MINUS, ParseMode.MULTIPLY, ParseMode.DIVIDE);
		try {
			return new CalcResult(Double.parseDouble(item.get().replace('=', (char)0)));
		} catch (NullPointerException | NumberFormatException e) {
			throw new ParserError("解析中にエラーが発生しました", e);
		}
    }

	@Override
	public String toString() {
		return "SimpleFormulaParser []";
	}
    
}
