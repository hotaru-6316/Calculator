package item;

import java.util.Objects;

import parse.Parser;

/**
 * 履歴情報の1エントリを保存するクラスです。
 */
public record History(int id, FormulaItem formula, CalcResult result, Parser parser) {
	
	private static void requireNotNull(Object... objs) {
		for (Object obj : objs) {
			Objects.requireNonNull(obj);
		}
	}
	
	/**
	 * 新しい履歴エントリを作成します。
	 * @param id エントリID(-1は新規エントリ)
	 * @param formula　数式
	 * @param result 結果
	 * @param parser 計算に使用したParser
	 * @throws IllegalArgumentException idが1より小さい場合
	 * @throws NullPointerException 引数が1つ以上nullである場合
	 */
	public History {
		if (id < -1) {
			throw new IllegalArgumentException("idを-1より小さくすることはできません");
		}
		requireNotNull(formula, result, parser);
	}

}
