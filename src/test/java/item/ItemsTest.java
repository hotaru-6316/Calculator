package item;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import annotation.Unsupported;
import util.ReflectionUtilException;
import util.ReflectionUtility;

/**
 * Itemクラス関連のテストを実装しています。
 */
public class ItemsTest {

	/**
	 * テストするItemクラスの配列。
	 */
    private final static Item<?>[] ITEMS = {
        new CalcResult(10),
        new FormulaItem("12+15")
    };
    
    /**
     * テストするItemクラスとはEqualsとはならないが同じ種類のクラス。<br>
     * テストするItemクラスの配列の順番と同じになるように設定。
     */
    private final static Item<?>[] ITEMS_NOTEQUALS = {
    	new CalcResult(0),
    	new FormulaItem("10+5")
    };

    /**
     * Equalsの実装をテストします
     * @throws InvocationTargetException コンストラクタが例外をスローした場合
     * @throws IllegalArgumentException クラスの仮引数と実引数に問題がある場合
     * @throws IllegalAccessException コンストラクタへのアクセスができない場合
     */
    @Test
    void testEquals() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (int i = 0; i < ITEMS.length; i++) {
        	Item<?> item = ITEMS[i];
            @SuppressWarnings("rawtypes")
			Class<? extends Item> clazz = item.getClass();
			Constructor<?> constructor = clazz.getConstructors()[0];
			assertEquals(ReflectionUtility.newInstance(constructor, item.get()), item);
            assertEquals(item, item);
            assertNotEquals(item, null);
            assertNotEquals(item, new Object());
            assertNotEquals(item, ITEMS_NOTEQUALS[i]);
        }
    }
    
    /**
     * コンストラクタにnullを渡すと例外が発生するかテストします。
     */
    @Test
    void testNotInstantiateNull() {
    	for (int i = 0; i < ITEMS.length; i++) {
    		Item<?> item = ITEMS[i];
    		@SuppressWarnings("rawtypes")
			Class<? extends Item> clazz = item.getClass();
			Constructor<?> constructor = clazz.getConstructors()[0];
			assertThrows(IllegalArgumentException.class, () -> {
				try {
					ReflectionUtility.newInstance(constructor, (Object) null);
				} catch (ReflectionUtilException e) {
					if (e.getCause() instanceof InvocationTargetException) {
						throw e.getCause().getCause();
					} else {
						throw e.getCause();
					}
				}
			});
    	}
    }

    /**
     * 正しく値が取得できるかテストします
     */
    @Test
    void testGet() {
        for (Item<?> item : ITEMS) {
            @SuppressWarnings("rawtypes")
            Class<? extends Item> clazz = item.getClass();
            Field dataField = clazz.getDeclaredFields()[0];
			assertEquals(ReflectionUtility.getFieldValue(dataField, item), item.get());
        }
    }

    /**
     * 正しくハッシュコードが作れているかテストします
     */
    @Test
    void testHashCode() {
        for (Item<?> item : ITEMS) {
            @SuppressWarnings("rawtypes")
            Class<? extends Item> clazz = item.getClass();
            Field dataField = clazz.getDeclaredFields()[0];
            dataField.setAccessible(true);
			assertEquals(Objects.hash(ReflectionUtility.getFieldValue(dataField, item)), item.hashCode());
        }
    }

    /**
     * 正しく値を更新できるか、そしてnullで更新しようとしたら例外がスローされるかテストします。<br>
     * ただし、Unsupportedアノテーションが指定されている場合はUnsupportedOperationExceptionがスローされるか確認します。
     * @throws NoSuchMethodException set()メソッドが存在しない場合
     */
    @Test
    void testSet() throws NoSuchMethodException {
        for (Item<?> item : ITEMS) {
            @SuppressWarnings("rawtypes")
            Class<? extends Item> clazz = item.getClass();
            Method setMethod = clazz.getMethod("set", item.get().getClass());
            if (setMethod.isAnnotationPresent(Unsupported.class)) {
                assertThrows(UnsupportedOperationException.class, () -> {
                    item.set(null);
                });
            } else {
                assertDoesNotThrow(() -> {
                    ReflectionUtility.invokeMethod(setMethod, item, Object.class, item.get());
                });
                assertThrows(IllegalArgumentException.class, () -> {
                	throw ReflectionUtility.invokeMethod(setMethod, item, IllegalArgumentException.class, item.get());
                });
            }
        }
    }

    /**
     * 文字列化が正しくできるかテストします
     */
    @Test
    void testToString() {
        for (Item<?> item : ITEMS) {
            @SuppressWarnings("rawtypes")
            Class<? extends Item> clazz = item.getClass();
            String className = clazz.getSimpleName();
            Field dataField = clazz.getDeclaredFields()[0];
            dataField.setAccessible(true);
			assertEquals(className + " [" + dataField.getName() + "=" + ReflectionUtility.getFieldValue(dataField, item) + "]", item.toString());
        }
    }
}
