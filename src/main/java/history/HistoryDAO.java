package history;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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

public class HistoryDAO {
	
	private static String dbDir = "./";
	
	private static final String TABLENAME = "history";
	
	private static final String ID_COL = "id";
	
	private static final String FORMULA_COL = "formula";
	
	private static final String RESULT_COL = "result";
	
	private static final String PARSER_COL = "parser";
	
	private static final String[] COLDEFS = {
			ID_COL + " INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT",
			FORMULA_COL + " VARCHAR(512) NOT NULL",
			RESULT_COL + " DOUBLE PRECISION NOT NULL",
			PARSER_COL + " INTEGER NOT NULL"
	};
	
	private static final Map<Integer, Supplier<? extends Parser>> PARSER_IDS = initParserIdsMap();
	
	private static Map<Integer, Supplier<? extends Parser>> initParserIdsMap() {
		Map<Integer, Supplier<? extends Parser>> map = new HashMap<>();
		map.put(0, SimpleFormulaParser::getParser); // 通常電卓
		map.put(1, LogicFormulaParser::getParser); // 四則演算電卓
		return map;
	}
	
	@Deprecated
	private HistoryDAO() {
		throw new UnsupportedOperationException();
	}

	private static Connection getConnection() throws SQLException {
		if (!DBUtils.isDBCreated(dbDir + TABLENAME)) {
			DBUtils.createDatabase(dbDir + TABLENAME, TABLENAME, COLDEFS);
		}
		return DBUtils.createConnection(dbDir + TABLENAME);
	}

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
	}
	
	public static void removeHistory(History entry) throws SQLException {
		removeHistory(entry.id());
	}
	
	public static void removeHistory(int id) throws SQLException {
		Connection conn = getConnection();
		try {
			DBUtils.executeUpdate(conn, "DELETE FROM " + TABLENAME + " WHERE " + ID_COL + "=?", id);
		} finally {
			conn.close();
		}
	}
	
	private static Parser getParserById(int id) {
		if (!PARSER_IDS.containsKey(id)) {
			throw new IllegalArgumentException("ParserIDが\"" + id + "\"であるParserはありません。");
		}
		return PARSER_IDS.get(id).get();
	}
	
	private static int getParserId(Parser parser) {
		for (int id : PARSER_IDS.keySet()) {
			if (PARSER_IDS.get(id).get().equals(parser)) {
				return id;
			}
		}
		throw new IllegalArgumentException("Parser\"" + PARSER_COL.getClass().getSimpleName() + "\"のIDは取得できません。");
	}
	
	private static FormulaItem createFormulaItem(String formulaStr) {
		return new FormulaItem(formulaStr);
	}

}
