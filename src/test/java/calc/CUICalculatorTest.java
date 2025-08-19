package calc;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import input.CUIInputer;
import util.ReflectionUtility;

/**
 * CUI用の計算機クラスをテストします。
 */
public class CUICalculatorTest extends CalculatorTest {
	
	private static class TestModeChanger implements Closeable {
		
		public TestModeChanger() {
			ReflectionUtility.setFieldValue(CUICalculator.class, null, "scriptMode", true);
		}

		@Override
		public void close() {
			ReflectionUtility.setFieldValue(CUICalculator.class, null, "scriptMode", false);
		}
		
	}

    /**
     * テストの準備を行います。
     * @throws NoSuchMethodException CUI用の計算機クラスでchangeParserメソッドが見つからなかった場合
     * @throws InvocationTargetException メソッドが例外をスローした場合
     */
    public CUICalculatorTest() throws NoSuchMethodException, InvocationTargetException {
        CUICalculator calc = new CUICalculator();
        Class<CUICalculator> calcClass = CUICalculator.class;
        ReflectionUtility.invokeMethod(calcClass, calc, Object.class, "changeParser", null, null);
        super.init(calc);
    }
    
    @AfterEach public void テスト後処理() {
    	setScannerIsFieldUsingReflection(System.in);
    }
    
    @Test
    void スクリプトモードでの電卓画面の表示テスト() {
    	// テスト準備
    			try (
    					TestModeChanger changer = new TestModeChanger();
    	    			SystemOutErrWrapper outWrapper = new SystemOutErrWrapper(SystemOutErrWrapper.WrapperEnum.SYSTEM_OUT);
    	    			SystemOutErrWrapper errWrapper = new SystemOutErrWrapper(SystemOutErrWrapper.WrapperEnum.SYSTEM_ERR);
    	    	) {
    				ByteArrayInputStream newIs = new ByteArrayInputStream((
    						"12+2*4" + System.lineSeparator() +
    						"change-mode" + System.lineSeparator() +
    						"12+2*4" + System.lineSeparator() +
    						"12t" + System.lineSeparator() + 
    						"60+*90" + System.lineSeparator() +
    						System.lineSeparator() +
    						"9/0" + System.lineSeparator() +
    						"exit" + System.lineSeparator()
    				).getBytes());
    	    		setScannerIsFieldUsingReflection(newIs);
    	            new CUICalculator().display();
    	            
    	            assertEquals(
    	            		"56.0" + System.lineSeparator() +
    	            		"20.0" + System.lineSeparator() +
    	            		"ERROR" + System.lineSeparator() +
    	            		"ERROR" + System.lineSeparator() +
    	            		"ERROR" + System.lineSeparator()
    	            , outWrapper.getBuffer().toString());
    	            
    	            assertEquals(
    	            		 "使用できない文字が含まれています" + System.lineSeparator() +
    	            		 "入力された計算式が不正です。" + System.lineSeparator() +
    	            		 "0で割ることは出来ません" + System.lineSeparator()
    	            , errWrapper.getBuffer().toString());
    			}
    }
    
    /**
     * 計算機クラスの電卓画面の表示メソッドをテストします。
     */
	@Test
    void 電卓画面の表示テスト() {
    	// テスト準備
		try (
    			SystemOutErrWrapper outWrapper = new SystemOutErrWrapper(SystemOutErrWrapper.WrapperEnum.SYSTEM_OUT);
    			SystemOutErrWrapper errWrapper = new SystemOutErrWrapper(SystemOutErrWrapper.WrapperEnum.SYSTEM_ERR);
    	) {
			ByteArrayInputStream newIs = new ByteArrayInputStream((
					"12+2*4" + System.lineSeparator() +
					"change-mode" + System.lineSeparator() +
					"12+2*4" + System.lineSeparator() +
					"12t" + System.lineSeparator() + 
					"60+*90" + System.lineSeparator() +
					System.lineSeparator() +
					"9/0" + System.lineSeparator() +
					"exit" + System.lineSeparator()
			).getBytes());
    		setScannerIsFieldUsingReflection(newIs);
            new CUICalculator().display();
            
            assertEquals(
            		"数字(少数も含む)とこれらの記号+-*/を入力してEnterを押すと計算します。" + System.lineSeparator() +
            		"現在通常モードで動作しています。(掛け算や割り算に関係なく左から右に計算します)" + System.lineSeparator() +
            		"モードを変更する場合は\"change-mode\"と入力してください。" + System.lineSeparator() +
            		"\"exit\"と入力すると電卓プログラムを終了します。" + System.lineSeparator() +
            		"> " + "56.0" + System.lineSeparator() +
            		"> " + "現在四則計算モードで動作しています。(掛け算や割り算を先に計算します)" + System.lineSeparator() +
            		"モードを変更する場合は\"change-mode\"と入力してください。" + System.lineSeparator() +
            		"> " + "20.0" + System.lineSeparator() +
            		"> " +
            		"> " +
            		"> " +
            		"> " +
            		"> " + "電卓を終了します。" + System.lineSeparator()
            , outWrapper.getBuffer().toString());
            
            assertEquals(
            		 "計算中にエラーが発生しました: 使用できない文字が含まれています" + System.lineSeparator() +
            		 "計算中にエラーが発生しました: 入力された計算式が不正です。" + System.lineSeparator() +
            		 "計算中にエラーが発生しました: 0で割ることは出来ません" + System.lineSeparator()
            , errWrapper.getBuffer().toString());
		}
    }
	
	/**
	 * {@link CUICalculator#display() display()}メソッド内で定義するUncaughtExceptionHandlerを動作させます。<br>
	 * このテスト中、JVMが終了しないよう、{@link CUICalculator#exitVM exitVM}変数をtrueに書き換えます。<br>
	 * 例外が発生した場合、NoSuchFieldException等、CUICalculator内のソースコードに原因があると予想される場合、AssertionFailedErrorが発生します。<br>
	 * InaccessibleObjectException等、原因が予想できなかったり、テストコード内に原因があると予想される場合、RuntimeExceptionが発生します。
	 * @throws RuntimeException 発生した例外の原因が予想できなかったり、テストコード内に問題があると予想される場合
	 */
	@Test
	void UncaughtExceptionHandlerテスト() {
		ByteArrayInputStream newIs = new ByteArrayInputStream((
				"exit" + System.lineSeparator()
		).getBytes());
		setScannerIsFieldUsingReflection(newIs);
		new CUICalculator().display();
    
		ReflectionUtility.setFieldValue(CUICalculator.class, null, "exitVM", false);
		Thread.startVirtualThread(() -> {
			throw new RuntimeException();
		});
		try {
			Thread.sleep(Duration.ofSeconds(1));
		} catch (InterruptedException e) {;}
	}

	private void setScannerIsFieldUsingReflection(InputStream newIs) {
		ReflectionUtility.setFieldValue(CUIInputer.class, null, "scannerIs", newIs);
	}

}
