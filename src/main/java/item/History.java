package item;

import java.util.Objects;

import parse.LogicFormulaParser;
import parse.Parser;
import parse.SimpleFormulaParser;

/**
 * 履歴情報の1エントリを保存するクラスです。
 * @param id エントリID(-1は新規エントリ)
 * @param formula　数式
 * @param result 結果
 * @param parser 計算に使用したParser
 */
public record History(int id, FormulaItem formula, CalcResult result, Parser parser) {
	
	/**
	 * 指定のオブジェクトが全てnullでないかを確認します。
	 * @param objs 1つもnullがないかの判定を行うオブジェクトの配列
	 * @throws NullPointerException 指定のオブジェクトの1つ以上がnullである場合
	 */
	private static void requireNotNull(Object... objs) {
		for (Object obj : objs) {
			Objects.requireNonNull(obj);
		}
	}
	
	/**
	 * 新しい履歴エントリを作成します。
	 * @throws IllegalArgumentException idが1より小さい場合
	 * @throws NullPointerException 引数が1つ以上nullである場合
	 * @param id エントリID(-1は新規エントリ)
	 * @param formula　数式
	 * @param result 結果
	 * @param parser 計算に使用したParser
	 */
	public History {
		if (id < -1) {
			throw new IllegalArgumentException("idを-1より小さくすることはできません");
		}
		requireNotNull(formula, result, parser);
	}

	@Override
	public String toString() {
		String parserName;
		if (parser instanceof SimpleFormulaParser) {
			parserName = "通常電卓";
		} else if (parser instanceof LogicFormulaParser) {
			parserName = "四則計算電卓";
		} else {
			parserName = "不明";
		}
		return "[" + parserName + "] " + formula.get() + result.get();
	}

}
