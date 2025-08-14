package history;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class DBUtils {
	
	@FunctionalInterface
	public static interface ThrowableRunnable<P, T extends Throwable> {
		
		void run(P param) throws T;
		
	}
	
	private static final String DRIVER_NAME = "org.h2.Driver";
	
	private static final String DRIVER_URL = "jdbc:h2:file:";
	
	private static final Map<String, Connection> CONNECTIONS = new HashMap<>();
	
	@Deprecated
	private DBUtils() {}
	
	public static Connection createConnection(String dbname) throws SQLException {
		return createConnection(dbname, false);
	}

	private static Connection createConnection(String dbname, boolean createDB) throws SQLException {
		try {
			if (createDB) {
				if (isDBCreated(dbname)) {
					throw new IllegalStateException(dbname + "は既に初期化されています");
				}
			} else {
				if (!isDBCreated(dbname)) {
					throw new IllegalStateException(dbname + "は初期化されていません");
				}
			}
			if (CONNECTIONS.containsKey(dbname)) {
				Connection conn = CONNECTIONS.get(dbname);
				if (!conn.isClosed()) {
					return conn;
				}
			}
			Class.forName(DRIVER_NAME);
			Connection conn = DriverManager.getConnection(DRIVER_URL + dbname);
			CONNECTIONS.put(dbname, conn);
			return conn;
		} catch (ClassNotFoundException e) {
			Error error = new NoClassDefFoundError();
			error.initCause(e);
			throw error;
		}
	}
	
	public static void closeDatabase(Connection conn) throws SQLException {
		if (!conn.isClosed()) {
			conn.close();
		}
		for (String dbname : new HashSet<>(CONNECTIONS.keySet())) {
			if (CONNECTIONS.get(dbname) == conn) {
				CONNECTIONS.remove(dbname);
				break;
			}
		}
	}

	public static boolean isDBCreated(String dbname) {
		return Files.exists(Path.of(dbname + ".mv.db"));
	}
	
	/**
	 * パラメータ付きSQL文のパラメータをsqlParamsで埋めた後、実行します。<br>
	 * この関数で実行するSQL文は、DML文か、DDL文等何も返さない文でなければなりません。
	 * @param conn SQL実行に使用するデータベース接続
	 * @param sql 実行するSQL(パラメータ付き)
	 * @param sqlParams SQLのパラメータに使用するオブジェクトの配列
	 * @throws SQLException SQL文の実行に失敗した場合
	 */
	public static void executeUpdate(Connection conn, String sql, Object... sqlParams) throws SQLException {
		PreparedStatement statement = conn.prepareStatement(sql);
		try {
			prepareExecute(statement, sqlParams);
			statement.executeUpdate();
		} finally {
			statement.close();
		}
	}
	
	/**
	 * パラメータ付きSQL文のパラメータをsqlParamsで埋めた後、実行します。
	 * この関数で実行するSQL文はSELECT等、結果をResultSetで返す文で使用します。
	 * @param runnable ResultSetを処理するThrowableRunnable(ResultSetは関数終了時に自動で閉じられます)
	 * @param conn SQLを実行するデータベース接続
	 * @param sql 実行するSQL(パラメータ付き)
	 * @param sqlParams SQLのパラメータに使用するオブジェクトの配列
	 * @throws SQLException SQL文の実行に失敗した場合
	 */
	public static void executeQuery(
			ThrowableRunnable<? super ResultSet, ? extends SQLException> runnable,
			Connection conn, String sql, Object... sqlParams) throws SQLException {
		PreparedStatement statement = conn.prepareStatement(sql);
		try {
			prepareExecute(statement, sqlParams);
			ResultSet set = statement.executeQuery();
			runnable.run(set);
		} finally {
			statement.close();
		}
	}

	private static void prepareExecute(PreparedStatement statement, Object... params) throws SQLException {
		int paramId = 1;
		for (Object param : params) {
			Objects.requireNonNull(param);
			if (param instanceof Double doubleParam) {
				statement.setDouble(paramId, doubleParam);
			} else if (param instanceof Integer intParam) {
				statement.setInt(paramId, intParam);
			} else if (param instanceof String strParam) {
				statement.setString(paramId, strParam);
			} else {
				statement.setObject(paramId, param);
			}
			paramId++;
		}
	}
	
	/**
	 * データベースを作成し、指定のテーブルで初期化します。<br>
	 * この関数の引数の内容はそのままSQLの一部として実行されるため、この関数に渡すデータには注意が必要です。<br>
	 * @param dbname データベース名
	 * @param tableName テーブル名
	 * @param colDefStrs 列定義文字列の配列(例: <code>new String[] {"id INTEGER NOT NULL","name VARCHAR(200)"}</code>)
	 * @throws SQLException データベースの作成、初期化に失敗した場合
	 */
	public static void createDatabase(String dbname, String tableName, String[] colDefStrs) throws SQLException {
		String colDef = String.join(",", colDefStrs);
		Connection conn = createConnection(dbname, true);
		try {
			Statement statement = conn.createStatement();
			try {
				statement.executeUpdate("CREATE TABLE " + tableName + "(" + colDef + ")");
			} finally {
				statement.close();
			}
		} catch (SQLException e) {
			conn.close();
			try {
				Files.delete(Path.of(dbname + ".mv.db"));
			} catch (IOException e1) {
				e.addSuppressed(e1);
			}
			throw e;
		} finally {
			closeDatabase(conn);
		}
	}

}
