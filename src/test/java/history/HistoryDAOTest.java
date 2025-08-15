package history;

import static org.junit.jupiter.api.Assertions.*;
import static util.ReflectionUtility.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import item.CalcResult;
import item.FormulaItem;
import item.History;
import parse.SimpleFormulaParser;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HistoryDAOTest {
	
	private static final History[] TEST_DATA = {
			new History(1, new FormulaItem("12+6="), new CalcResult(18), SimpleFormulaParser.getParser()),
			new History(2, new FormulaItem("6+7="), new CalcResult(13), SimpleFormulaParser.getParser()),
			new History(3, new FormulaItem("9+2="), new CalcResult(11), SimpleFormulaParser.getParser())
	};
	
	@BeforeAll
	static void setHistoryDBDir() {
		setFieldValue(HistoryDAO.class, null, "dbDir", "./target/");
	}
	
	@Test @Order(1)
	void testMaxHistory() throws SQLException {
		int maxEntry = (int) getFieldValue(HistoryDAO.class, null, "MAX_ENTRY");
		for (int i = 0; i < 30; i++) {
			assertDoesNotThrow(() -> HistoryDAO.saveHistory(new History(-1, new FormulaItem("12+1="), new CalcResult(13), SimpleFormulaParser.getParser())));
		}
		assertEquals(maxEntry, HistoryDAO.getHistories().length);
	}
	
	@Test @Order(2)
	void testSaveHistory() throws IOException, SQLException {
		Files.deleteIfExists(Path.of((String) getFieldValue(HistoryDAO.class, null, "dbDir"), ((String) getFieldValue(HistoryDAO.class, null, "TABLENAME")) + ".mv.db"));
		for (History history : TEST_DATA) {
			assertDoesNotThrow(() -> HistoryDAO.saveHistory(new History(-1, new FormulaItem("12+1="), new CalcResult(13), SimpleFormulaParser.getParser())));
			assertDoesNotThrow(() -> HistoryDAO.saveHistory(history));
			assertTrue(Arrays.asList(HistoryDAO.getHistories()).contains(history));
		}
	}

	@Test @Order(3)
	void testGetHistories() {
		History[] histories = assertDoesNotThrow(HistoryDAO::getHistories);
		assertEquals(TEST_DATA.length, histories.length);
		for (History history : histories) {
			int num = history.id() - 1;
			History expected = TEST_DATA[num];
			assertEquals(expected, history);
		}
	}
	
	@Test @Order(4)
	void testRemoveHistory() throws SQLException {
		History history = Stream.of(HistoryDAO.getHistories()).findAny().get();
		assertDoesNotThrow(() -> HistoryDAO.removeHistory(history));
		assertFalse(Arrays.asList(HistoryDAO.getHistories()).contains(history));
	}

}
