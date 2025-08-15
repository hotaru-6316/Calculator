package history;

import static history.DBUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import util.ReflectionUtility;

class DBUtilsTest {
	
	private static final String TABLE = "test";
	
	private static final String DB = "./target/" + TABLE;
	
	private static final String DRIVER_URL = (String) ReflectionUtility.getFieldValue(DBUtils.class, null, "DRIVER_URL");
	
	private static final String DRIVER_NAME = (String) ReflectionUtility.getFieldValue(DBUtils.class, null, "DRIVER_NAME");
	
	private static final String COLS = "CREATE TABLE " + TABLE + "(" +
			"num INTEGER PRIMARY KEY," +
			"test VARCHAR(100)," +
			"doubleNum DOUBLE PRECISION," +
			"date DATETIME"
		+ ")";
	
	@BeforeEach
	void clearDB() {
		deleteDB();
		createDB();
	}
	
	@AfterAll
	static void cleanUpDB() {
		deleteDB();
	}
	
	/**
	 * DBUtilsを使わずにDBを作成します。
	 */
	private static void createDB() {
		try {
			Class.forName(DRIVER_NAME);
			try (Connection conn = DriverManager.getConnection(DRIVER_URL + DB)) {
				conn.createStatement().execute(COLS);
			}
		} catch (SQLException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 作成したDBを削除します。
	 */
	private static void deleteDB() {
		try {
			Files.deleteIfExists(Path.of(DB + ".mv.db"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Test
	void testCreateConnection() throws SQLException {
		assertDoesNotThrow(() -> createConnection(DB)).close(); // DB存在 => 成功
		deleteDB(); // DB削除
		assertThrows(IllegalStateException.class, () -> createConnection(DB)); // DBなし => 失敗
	}

	@Test
	void testIsDBCreated() {
		assertTrue(isDBCreated(DB)); // DB存在 => true
		deleteDB(); // DB削除
		assertFalse(isDBCreated(DB)); // DBなし => false
	}

	@Test
	void testExecuteUpdate() throws SQLException {
		try (Connection conn = createConnection(DB)) {
			int expectedNum = 10;
			String expectedTest = "Hello world!!";
			double expectedDoubleNum = 194.216;
			LocalDateTime expectedDate = LocalDateTime.of(2025, 8, 15, 21, 35);
			assertDoesNotThrow(() -> executeUpdate(conn, "INSERT INTO " + TABLE + " VALUES (?, ?, ?, ?)",
					expectedNum, expectedTest, expectedDoubleNum, expectedDate));
			executeQuery((resultSet) -> {
				resultSet.next();
				int num = resultSet.getInt("num");
				String test = resultSet.getString("test");
				double doubleNum = resultSet.getDouble("doubleNum");
				LocalDateTime date = resultSet.getObject("date", LocalDateTime.class);
				assertEquals(expectedNum, num);
				assertEquals(expectedTest, test);
				assertEquals(expectedDoubleNum, doubleNum);
				assertEquals(expectedDate, date);
			}, conn, "SELECT * FROM " + TABLE);
		}
	}

	@Test
	void testExecuteQuery() throws SQLException {
		try (Connection conn = createConnection(DB)) {
			int expectedNum = 10;
			String expectedTest = "Hello world!!";
			double expectedDoubleNum = 194.216;
			LocalDateTime expectedDate = LocalDateTime.of(2025, 8, 15, 21, 35);
			executeUpdate(conn, "INSERT INTO " + TABLE + " VALUES (?,?,?,?)",
					expectedNum, expectedTest, expectedDoubleNum, expectedDate);
			int[] actualNum = {0};
			String[] actualTest = {null};
			double[] actualDoubleNum = {0};
			LocalDateTime[] actualDate = {null};
			assertDoesNotThrow(() -> executeQuery((resultSet) -> {
				resultSet.next();
				actualNum[0] = resultSet.getInt("num");
				actualTest[0] = resultSet.getString("test");
				actualDoubleNum[0] = resultSet.getDouble("doubleNum");
				actualDate[0] = resultSet.getObject("date", LocalDateTime.class);
			}, conn, "SELECT * FROM " + TABLE));
			assertEquals(expectedNum, actualNum[0]);
			assertEquals(expectedTest, actualTest[0]);
			assertEquals(expectedDoubleNum, actualDoubleNum[0]);
			assertEquals(expectedDate, actualDate[0]);
		}
	}

	@Test
	void testCreateDatabase() {
		assertThrows(IllegalStateException.class, () -> createDatabase(DB, TABLE, new String[] {
				"num INTEGER PRIMARY KEY",
				"test VARCHAR(100)",
				"doubleNum DOUBLE PRECISION",
				"date DATETIME"
			})); // DB存在 => 失敗
		deleteDB();
		assertDoesNotThrow(() -> createDatabase(DB, TABLE, new String[] {
				"num INTEGER PRIMARY KEY",
				"test VARCHAR(100)",
				"doubleNum DOUBLE PRECISION",
				"date DATETIME"
			})); // DBなし => 成功
		assertTrue(isDBCreated(DB));
		deleteDB();
		assertThrows(SQLException.class, () -> createDatabase(DB, TABLE, new String[] {
				"num INTEGER sql_invalid_text_abcdefghijklmn" // 無効な制約
		})); // SQLが無効 => 失敗
		assertFalse(isDBCreated(DB)); // createDatabaseが失敗した場合、DBは作成されていない
	}

}
