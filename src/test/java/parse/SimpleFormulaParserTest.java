package parse;

import org.junit.jupiter.api.Test;

/**
 * SimpleFormulaParserをテストするクラスです。<br>
 * ただし、ほとんどすべてのテストはParserTest側に定義、実装されています。<br>
 * 詳細はParserTestを確認してください。
 * @see ParserTest
 */
final public class SimpleFormulaParserTest extends ParserTest {

	/**
	 * SimpleFormulaParserのテストを行う準備をします。
	 * 
	 * @see ParserTest#ParserTest(Parser)
	 */
    public SimpleFormulaParserTest() {
        super(SimpleFormulaParser.getParser());
    }

    @Override
    @Test
    void 計算テスト() {
        testParseAndCalc(38);
    }

	@Override
	@Test void 括弧の複雑なテスト() {
		parenthesesAdvancedTest(-4770);
	}

}
