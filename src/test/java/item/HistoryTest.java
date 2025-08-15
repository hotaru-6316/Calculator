package item;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import parse.LogicFormulaParser;
import parse.SimpleFormulaParser;

class HistoryTest {

	@Test
	void testHistory() {
		// IDを-1より小さくすることは不可
		assertThrows(IllegalArgumentException.class, () -> new History(-10, new FormulaItem("9"), new CalcResult(9), SimpleFormulaParser.getParser()));
		
		// 全ての引数はnull不可
		// intにnullはありません
		// 第二引数にnull
		assertThrows(NullPointerException.class, () -> new History(-1, null, new CalcResult(9), SimpleFormulaParser.getParser()));
		// 第三引数にnull
		assertThrows(NullPointerException.class, () -> new History(-1, new FormulaItem("9"), null, SimpleFormulaParser.getParser()));
		// 第四引数にnull
		assertThrows(NullPointerException.class, () -> new History(-1, new FormulaItem("9"), new CalcResult(9), null));
		
		// 成功
		assertDoesNotThrow(() -> new History(-1, new FormulaItem("10"), new CalcResult(10), SimpleFormulaParser.getParser()));
	}

	@Test
	void testToString() {
		assertEquals("[通常電卓] 12.2+9.6=21.8", new History(-1, new FormulaItem("12.2+9.6"), new CalcResult(21.8), SimpleFormulaParser.getParser()).toString());
		assertEquals("[四則計算電卓] 12.2+9.6=21.8", new History(-1, new FormulaItem("12.2+9.6"), new CalcResult(21.8), LogicFormulaParser.getParser()).toString());
	}

}
