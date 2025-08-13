package parse;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import calc.Calculator;
import item.CalcResult;
import item.FormulaItem;

/**
 * パーサに関する一連のテストを実装したクラスです。
 * テストの都合上AbstractParserを継承していますが、パーサとしては使用しないでください。
 */
abstract public class ParserTest extends AbstractParser{

	/**
	 * AbstractParserを継承、テストするために実装していますが、使わないでください。<br>
	 * このメソッドでは、呼び出しをそのままテスト対象のパーサに転送します。
	 */
    @Override
    public CalcResult parseAndCalc(FormulaItem item, Calculator calc) throws ParseException {
        return PARSER.parseAndCalc(item, calc);
    }

    /**
	 * AbstractParserを継承するために実装していますが、使わないでください。
	 * @throws UnsupportedOperationException 常に
	 */
    @Override
    public String toString() {
        throw new UnsupportedOperationException();
    }

    /**
     * テストに使用するパーサクラス
     */
    final private Parser PARSER;
    
    /**
     * 括弧の計算テストに使用するパーサを返すサプライヤ。テストに使用するパーサをオーバーライドした匿名クラスを使用しています。
     */
    final private Supplier<Parser> PARENTHESES_PARSER_SUPPLIER;
    
    /**
     * 括弧のテストにパーサを使うかどうか。<br>
     * <code>true</code>にすると括弧のテストではテストに使用するパーサを使います。<br>
     * <code>false</code>にすると括弧のテストでは括弧の計算を行った後、テストに使用するパーサへ処理を引き継ぐ匿名クラスを作成します。
     */
    final private static boolean USE_PARSER_PARENTHESES = false;

    /**
     * 指定されたパーサクラスを使用したテストを実施する準備を行います
     * @param parser テストに使用するパーサ
     */
    protected ParserTest(Parser parser) {
        PARSER = parser;
        if (PARSER instanceof ParserTest) {
            throw new IllegalArgumentException();
        }
        PARENTHESES_PARSER_SUPPLIER = () -> {
        	if (USE_PARSER_PARENTHESES) {
    			return PARSER;
    		} else {
    			if (PARSER.getClass().equals(SimpleFormulaParser.class)) {
    				return new SimpleFormulaParser() {
        				
        				@Override
        				public CalcResult parseAndCalc(FormulaItem item, Calculator calc) throws ParseException {
        					item = this.parseAndCalc(item, calc, ParseMode.PARENTHESES);
        					return super.parseAndCalc(item, calc);
        				}

        				@Override
        				public String toString() {
        					return "AnonymousClass extends SimpleFormulaParser []";
        				}
        				
        			};
    			} else if (PARSER.getClass().equals(LogicFormulaParser.class)) {
					return new LogicFormulaParser() {
						
						@Override
        				public CalcResult parseAndCalc(FormulaItem item, Calculator calc) throws ParseException {
        					item = this.parseAndCalc(item, calc, ParseMode.PARENTHESES);
        					return super.parseAndCalc(item, calc);
        				}
						
						@Override
        				public String toString() {
        					return "AnonymousClass extends LogicFormulaParser []";
        				}
						
					};
				} else {
					throw new IllegalStateException("PARSERはSimpleFormulaParserまたはLogicFormulaParserのどちらでもありません");
				}
    		}
        };
    }
    
    /**
     * 正しい数式(30+11-22*4/2)で計算を試行するテストです。<br>
     * 答えはテストクラスによって変わります。
     */
    @Test abstract void 計算テスト();

    /**
     * 正しい数式(30+11-22*4/2)で計算を試行するテストです。<br>
     * 計算方式によっては正しい答えが違うため、パラメータで正しい答えを指定します。
     * @param expected 正しい答え
     */
    final protected void testParseAndCalc(double expected) {
        FormulaItem item = new FormulaItem("30+11-22*4/2=");
        assertDoesNotThrow(() -> {
            CalcResult result = PARSER.parseAndCalc(item, Calculator.getCUICalc());
            assertEquals(new CalcResult(expected), result);
        });
    }
    
    /**
     * 負の数を含む正しい数式(-21*15/-3)で計算を試行するテストです。
     */
    @Test void 負の数の計算テスト() {
    	FormulaItem item = new FormulaItem("-21*15/-3=");
    	assertDoesNotThrow(() -> {
    		CalcResult result = PARSER.parseAndCalc(item, Calculator.getCUICalc());
            assertEquals(new CalcResult(105), result);
    	});
    }
    
    /**
     * 数式(30+11-22*4/2)の計算を10万回行い、掛かった時間を測定します。<br>
     * このテストは、正しく動作するかのテストではなく、所要時間を把握するためのテストです。
     */
    @Test void 計算所要時間測定テスト() {
    	System.err.println("testCalctime(): " + PARSER.getClass().getSimpleName() + "の計算を開始します。");
    	Instant startTime = Instant.now();
    	FormulaItem item = new FormulaItem("30+11-22*4/2=");
    	try {
    		for(int i = 0; i < 100000; i++) {
    			PARSER.parseAndCalc(item, Calculator.getCUICalc());
    		}
		} catch (ParseException e) {
			fail(e);
		}
    	Instant endTime = Instant.now();
    	System.err.println("testCalctime(): " + PARSER.getClass().getSimpleName() + "の計算を終了します。");
    	Duration elapsedTime = Duration.between(startTime, endTime);
    	System.err.println("testCalctime(): " + PARSER.getClass().getSimpleName() + "の計算所要時間: " + elapsedTime.getSeconds() + "." + String.format("%09d", elapsedTime.getNano()) + "s");
    }

    /**
     * 不正な数式で計算を試行するテストです。
     */
    @Test void 不正な数式での計算テスト() {
        assertThrows(ParseException.class, () -> {
            FormulaItem item = new FormulaItem("30+11-22a*4/2=");
            PARSER.parseAndCalc(item, Calculator.getCUICalc());
        });
    }

    /**
     * かなり長い数式で計算を施行するテストです。<br>
     * 例外がスローされず、尚且つ結果が正しければテストは成功です。
     */
    @Test void 長い数式での計算テスト() {
        assertDoesNotThrow(() -> {
            assertEquals(new CalcResult(19363472856348d+3274687654386d), PARSER.parseAndCalc(new FormulaItem("19363472856348+3274687654386"), Calculator.getCUICalc()));
        });
    }

    /**
     * AbstractParserの計算を実際に行う実装部分を直接テストします。<br>
     * このテストでは、AbstractParserを直接テストするのでパーサは使用されません。
     * @throws ParseException 計算エラーの場合
     */
    @Test void 内部計算メソッドの直接テスト() throws ParseException {
        FormulaItem item = new FormulaItem("30+11-22*4/2=");
        if (!(PARSER instanceof AbstractParser)) {
            throw new IllegalArgumentException();
        }
        FormulaItem item2 = super.parseAndCalc(item, Calculator.getCUICalc(), ParseMode.MULTIPLY, ParseMode.DIVIDE);
        assertEquals(new FormulaItem("30.0+11.0-44.0="), item2);
        FormulaItem item3 = super.parseAndCalc(item2, Calculator.getCUICalc(), ParseMode.PLUS, ParseMode.MINUS);
        assertEquals(new FormulaItem("-3.0="), item3);
    }

    /**
     * パーサの文字列化をテストします。
     */
    @Test void 文字列表現への変換テスト() {
        Class<?> clazz = PARSER.getClass();
        String className = clazz.getSimpleName();
        assertEquals(className + " []", PARSER.toString());
    }
    
    /**
     * 括弧を使用した単純な正しい数式(1+2+(3*4)+5)で計算テストを行います。
     * @throws ParseException
     */
    @Test void 括弧の通常解析テスト() {
    	CalcResult result = assertDoesNotThrow(() -> {
    		return PARENTHESES_PARSER_SUPPLIER.get().parseAndCalc(new FormulaItem("1+2+(3*4)+5"), Calculator.getCUICalc());
    	});
    	assertEquals(1+2+(3*4)+5, result.get());
    }
    
    /**
     * 括弧を使用した、掛け算の発生する単純な正しい数式(6+3+9(3*9)+2)で計算テストを行います。
     */
    @Test void 括弧の数字掛け算ありテスト() {
    	CalcResult result = assertDoesNotThrow(() -> {
    		return PARENTHESES_PARSER_SUPPLIER.get().parseAndCalc(new FormulaItem("6+3+9(3*9)+2"), Calculator.getCUICalc());
    	});
    	assertEquals(6+3+9*27+2, result.get());
    }
    
    @Test void 括弧の計算処理例外チェック() {
    	assertThrows(IllegalArgumentException.class, () -> {
    		this.parseAndCalc(new FormulaItem("1"), Calculator.getCUICalc(), ParseMode.DIVIDE, ParseMode.PARENTHESES);
    	});
    }
    
    abstract @Test void 括弧の複雑なテスト();
    
    protected void parenthesesAdvancedTest(double expected) {
    	CalcResult result = assertDoesNotThrow(() -> {
    		return PARENTHESES_PARSER_SUPPLIER.get().parseAndCalc(new FormulaItem("6+9+(7+9(65-90)-9)-21"), Calculator.getCUICalc());
    	});
    	assertEquals(-233, result.get());
    	result = assertDoesNotThrow(() -> {
    		return PARENTHESES_PARSER_SUPPLIER.get().parseAndCalc(new FormulaItem("9+(4+9*(8*5(+3-8)/5)*9)-99"), Calculator.getCUICalc());
    	});
    	assertEquals(expected, result.get());
    }
    
    

}
