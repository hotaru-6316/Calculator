package history;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import item.CalcResult;
import item.FormulaItem;
import item.History;
import parse.LogicFormulaParser;
import parse.Parser;
import parse.SimpleFormulaParser;

/**
 * 履歴エントリをデータベースに読み書きします。
 */
public class HistoryDAO {
	
	/**
	 * データベースに保存する履歴エントリの最大数
	 */
	private static final int MAX_ENTRY = 20;

	/**
	 * データベースを保管するフォルダの場所。テスト時にリフレクションで書き換えます。
	 */
	private static String dbDir = "./";
	
	/**
	 * 履歴エントリを保存するテーブルの名前。DBの名前としても利用されます。
	 */
	private static final String TABLENAME = "history";
	
	/**
	 * {@link History#id()}のデータを保存する列の名前
	 */
	private static final String ID_COL = "id";
	
	/**
	 * {@link History#formula()}のデータを保存する列の名前。このデータは文字列に変換された後にDBに保存されます。
	 */
	private static final String FORMULA_COL = "formula";

	/**
	 * {@link History#result()}のデータを保存する列の名前。このデータはdoubleに変換された後にDBに保存されます。
	 */
	private static final String RESULT_COL = "result";

	/**
	 * {@link History#parser()}のデータを保存する列の名前。このデータは{@link #PARSER_IDS}を使用して、数値に変換されます。
	 */
	private static final String PARSER_COL = "parser";

	/**
	 * DBを初期化するときに実行される、列定義の文字列の配列です。
	 */
	private static final String[] COLDEFS = {
			ID_COL + " INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT",
			FORMULA_COL + " VARCHAR(512) NOT NULL",
			RESULT_COL + " DOUBLE PRECISION NOT NULL",
			PARSER_COL + " INTEGER NOT NULL"
	};
	
	/**
	 * {@link History#parser()}のデータを数値に変換するためのIDデータ
	 */
	private static final Map<Integer, Supplier<? extends Parser>> PARSER_IDS = initParserIdsMap();
	
	/**
	 * {@link #PARSER_IDS}のデータを初期化する際に使用します。
	 * @return {@link #PARSER_IDS}のデータ
	 */
	private static Map<Integer, Supplier<? extends Parser>> initParserIdsMap() {
		Map<Integer, Supplier<? extends Parser>> map = new HashMap<>();
		map.put(0, SimpleFormulaParser::getParser); // 通常電卓
		map.put(1, LogicFormulaParser::getParser); // 四則演算電卓
		return map;
	}
	
	/**
	 * @deprecated このクラスはオブジェクト化して使用しません。
	 */
	@Deprecated
	private HistoryDAO() {
		throw new UnsupportedOperationException();
	}

	/**
	 * DBへの接続を作成します。DBが存在しない場合は、先にDBを作成します。
	 * @return DBへの接続
	 * @throws SQLException DB作成・接続に失敗した場合
	 */
	private static Connection getConnection() throws SQLException {
		if (!DBUtils.isDBCreated(dbDir + TABLENAME)) {
			DBUtils.createDatabase(dbDir + TABLENAME, TABLENAME, COLDEFS);
		}
		return DBUtils.createConnection(dbDir + TABLENAME);
	}

	/**
	 * DBに保存されている全ての履歴情報を取得します。
	 * @return DBに存在する全ての履歴情報の配列
	 * @throws SQLException DBでの操作に失敗した場合
	 */
	public static History[] getHistories() throws SQLException {
		Connection conn = getConnection();
		try {
			List<History> entries = new ArrayList<>();
			DBUtils.executeQuery((set) -> {
				while (set.next()) {
					int id = set.getInt(ID_COL);
					String formulaStr = set.getString(FORMULA_COL);
					double resultValue = set.getDouble(RESULT_COL);
					int parserId = set.getInt(PARSER_COL);
					FormulaItem item = createFormulaItem(formulaStr);
					Parser parser = getParserById(parserId);
					CalcResult result = new CalcResult(resultValue);
					History entry = new History(id, item, result, parser);
					entries.add(entry);
				}
			}, conn, "SELECT * FROM " + TABLENAME);
			return entries.toArray(new History[entries.size()]);
		} finally {
			conn.close();
		}
	}
	
	/**
	 * 履歴エントリを挿入または更新します。履歴エントリのidが-1ではない場合は、指定のidのエントリを更新します。idが-1の場合は、新しくデータを挿入します。
	 * @param entry 更新・挿入する履歴エントリ
	 * @throws SQLException DBでの操作に失敗した場合
	 */
	public static void saveHistory(History entry) throws SQLException {
		Connection conn = getConnection();
		try {
			int id = entry.id();
			String formulaStr = entry.formula().get();
			double resultValue = entry.result().get();
			int parserId = getParserId(entry.parser());
			if (id == -1) {
				DBUtils.executeUpdate(conn, ""
						+ "INSERT INTO " + TABLENAME + "(" + FORMULA_COL + ", " + RESULT_COL + ", " + PARSER_COL + ") VALUES (?, ?, ?)",
						formulaStr, resultValue, parserId);
			} else {
				DBUtils.executeUpdate(conn, 
						"UPDATE " + TABLENAME + " SET "
							+ FORMULA_COL + "=?, "
							+ RESULT_COL + "=?, "
							+ PARSER_COL + "=? "
						+ "WHERE " + ID_COL + "=?",
						formulaStr, resultValue, parserId, id);
			}
		} finally {
			conn.close();
		}
		cleanHistory();
	}
	
	/**
	 * DBの履歴エントリ数が{@link #MAX_ENTRY}に収まるように整理します。
	 * @throws SQLException 整理に失敗した場合
	 */
	private static void cleanHistory() throws SQLException {
		List<History> histories = new ArrayList<>(Arrays.asList(getHistories())).reversed();
		int count = 0;
		for (History history : histories) {
			count++;
			if (count > MAX_ENTRY) {
				removeHistory(history);
			}
		}
	}
	
	/**
	 * 履歴エントリをDBから削除します。
	 * @param entry 削除する履歴エントリ
	 * @throws SQLException DB操作に失敗した場合
	 */
	public static void removeHistory(History entry) throws SQLException {
		removeHistory(entry.id());
	}
	
	/**
	 * 履歴エントリをDBから削除します。
	 * @param id 削除する履歴エントリのid
	 * @throws SQLException DB操作に失敗した場合
	 */
	public static void removeHistory(int id) throws SQLException {
		Connection conn = getConnection();
		try {
			DBUtils.executeUpdate(conn, "DELETE FROM " + TABLENAME + " WHERE " + ID_COL + "=?", id);
		} finally {
			conn.close();
		}
	}
	
	/**
	 * ParserID(数値)から{@link Parser}を取得します。
	 * @param id ParserID
	 * @return {@link Parser}
	 */
	private static Parser getParserById(int id) {
		if (!PARSER_IDS.containsKey(id)) {
			throw new IllegalArgumentException("ParserIDが\"" + id + "\"であるParserはありません。");
		}
		return PARSER_IDS.get(id).get();
	}
	
	/**
	 * {@link Parser}からParserID(数値)を取得します。
	 * @param parser {@link Parser}
	 * @return ParserID
	 */
	private static int getParserId(Parser parser) {
		for (int id : PARSER_IDS.keySet()) {
			if (PARSER_IDS.get(id).get().equals(parser)) {
				return id;
			}
		}
		throw new IllegalArgumentException("Parser\"" + PARSER_COL.getClass().getSimpleName() + "\"のIDは取得できません。");
	}
	
	/**
	 * 文字列から{@link FormulaItem}を作成します。
	 * @param formulaStr 数式
	 * @return {@link FormulaItem}
	 */
	private static FormulaItem createFormulaItem(String formulaStr) {
		return new FormulaItem(formulaStr);
	}

}
