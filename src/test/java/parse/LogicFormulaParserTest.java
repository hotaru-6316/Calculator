package parse;

import org.junit.jupiter.api.Test;

/**
 * LogicFormulaParserをテストするクラスです。<br>
 * ただし、すべてのテストはParserTest側に実装されています。<br>
 * このクラスはテストを開始するためのメソッドのみ提供しています。
 * 
 * @see ParserTest
 */
public class LogicFormulaParserTest extends ParserTest {

	/**
	 * LogicFormulaParserのテストを行う準備をします。
	 * 
	 * @see ParserTest#ParserTest(Parser)
	 */
    public LogicFormulaParserTest() {
        super(LogicFormulaParser.getParser());
    }

    @Override
    @Test
    void 計算テスト() {
        testParseAndCalc(-3);
    }

	@Override
	@Test void 括弧の複雑なテスト() {
		parenthesesAdvancedTest(-3326);
	}

}