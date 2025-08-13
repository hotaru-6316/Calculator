package calc;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.Test;

import util.ReflectionUtility;

/**
 * 計算機クラスのテストを行うための一連のメソッドが実装されています。<br>
 * このクラスは全クラスに共通の部分のみ実装しています。<br>
 * 各クラスに独自に実装した機能のテストは子クラスで実装してください。
 */
abstract public class CalculatorTest {

    /**
     * テスト対象の計算機クラス
     */
    private Calculator calc;

    /**
     * テスト準備を行うメソッドです。テストを始める前に必ずこのメソッドを呼び出してください。
     * @param calc テスト対象の計算機クラス
     * @throws java.lang.IllegalStateException 既に準備が終わっている場合
     */
    final protected void init(Calculator calc) {
        if (this.calc != null) {
            throw new IllegalStateException();
        }
        this.calc = calc;
    }
    
    /**
     * テストに使用する計算機クラスを返します。
     * @return 計算機クラス
     * 
     */
    final protected Calculator getCalc() {
    	return calc;
    }


    /**
     * 計算機クラスの文字列表現への変換の実装をテストするメソッドです。
     */
    @Test protected void 文字列表現への変換テスト() {
        Class<? extends Calculator> clazz = this.calc.getClass();
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (Field field : clazz.getDeclaredFields()) {
        	try {
				field.canAccess(this.calc);
			} catch (IllegalArgumentException e) {
				// こっちの場合はフィールドがstaticである事を表しているため、無視します
				continue;
			}
            if (!first) {
                sb.append(", ");
            }
            sb.append(field.getName() + "=" + ReflectionUtility.getFieldValue(field, this.calc));
            first = false;
        }
        assertEquals(clazz.getSimpleName() + " [" + sb.toString() + "]", this.calc.toString());
    }

    /**
     * 計算機クラスの割り算の実装をテストするメソッドです。
     */
    @Test protected void 割り算テスト() {
        assertEquals(30d / 10, this.calc.divide(30, 10));
        assertEquals(BigDecimal.valueOf(10d).divide(BigDecimal.valueOf(3d), 15, RoundingMode.HALF_UP).doubleValue(), assertDoesNotThrow(() -> this.calc.divide(10, 3)));
    }

    /**
     * 計算機クラスの引き算の実装をテストするメソッドです。
     */
    @Test protected void 引き算テスト() {
        assertEquals(21d - 11, this.calc.minus(21, 11));
    }

    /**
     * 計算機クラスの掛け算の実装をテストするメソッドです。
     */
    @Test protected void 掛け算テスト() {
        assertEquals(22 * 90d, this.calc.multiply(22, 90));
    }

    /**
     * 計算機クラスの足し算の実装をテストするメソッドです。
     */
    @Test protected void 足し算テスト() {
        assertEquals(11 + 90d, this.calc.plus(11, 90));
        assertEquals(0.3d, assertDoesNotThrow(() -> this.calc.plus(0.1, 0.2)));
    }

}
