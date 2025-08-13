package util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * テストに使用する、リフレクションを使用したメソッドを実装しています。<br>
 * このメソッドは内部で各種例外処理を行うため、呼び出し元で複雑な例外処理を書く必要はありません。<br>
 * また、スローする全ての例外が非チェック例外のため、例外の場合はエラーで処理を終了させたい場合にも使用できます。<br>
 * ただし、チェック例外の場合は{@link ReflectionUtilException}(非チェック例外)でラップしていますが、非チェック例外の場合はラップしません。<br>
 * ラップした例外の詳細は各メソッドを確認してください。
 */
public final class ReflectionUtility {
	
	/**
	 * このクラスはインスタンス化して使うものではありません
	 */
	private ReflectionUtility() {}
	
	/**
	 * クラスの名前からClassオブジェクトを取得します。<br>
	 * 失敗した場合は、ReflectionUtilExceptionをスローします。
	 * @param name クラス名
	 * @throws ClassNotFoundException ({@link ReflectionUtilException}でラップ)クラスが存在しない場合
	 * @return nameで指定したクラスのClassインスタンス
	 * @see Class#forName(String)
	 */
	public static Class<?> getClassForName(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new ReflectionUtilException(e);
		}
	}
	
	/**
	 * クラスのフィールドの値を返します。<br>
	 * そのクラスのフィールドはどのアクセス修飾子でも問題ありません。
	 * @param <T> そのフィールドを持つクラスの型
	 * @param clazz そのフィールドを持つクラスのClassオブジェクト
	 * @param obj そのフィールドを持つクラスのインスタンス
	 * @param fieldName フィールド名
	 * @return フィールドの値
	 * @see #getFieldValue(Field, Object)
	 */
	public static <T> Object getFieldValue(Class<T> clazz, T obj, String fieldName) throws IllegalArgumentException, NullPointerException {
		Field field = getField(clazz, fieldName, false, false);
		return getFieldValue(field, obj);
	}

	/**
	 * フィールドの値を返します。<br>
	 * フィールドはどのアクセス修飾子でも問題ありません。
	 * @param field 読み取るフィールド
	 * @param obj フィールドを持つクラスのインスタンス
	 * @throws IllegalArgumentException 渡されたObjectとフィールドを持つObjectが一致しない場合
	 * @return フィールドの値
	 * @see Field#get(Object)
	 */
	public static Object getFieldValue(Field field, Object obj) throws IllegalArgumentException {
		try {
			field.setAccessible(true);
			return field.get(obj);
		} catch (InaccessibleObjectException e) {
			throw new ReflectionUtilUnexpectedException("フィールドのアクセス制御の無効化に失敗しました。", e);
		} catch (IllegalAccessException e) {
			throw new ReflectionUtilUnexpectedException("フィールドのアクセスエラーが発生しました。", e);
		}
	}
	
	/**
	 * クラスのフィールドの値を設定します。<br>
	 * そのクラスのフィールドはどのアクセス修飾子でも問題ありません。
	 * @param <T> そのフィールドを持つクラスの型
	 * @param clazz そのフィールドを持つクラスのClassインスタンス
	 * @param obj そのフィールドを持つクラスのインスタンス
	 * @param fieldName フィールド名
	 * @param fieldValue フィールドの値
	 * @throws IllegalArgumentException 渡されたObjectの型とフィールドの型が一致しない場合
	 * @throws NullPointerException インスタンスフィールドへ静的フィールドとしてアクセスしようとした場合
	 * @see Field#set(Object, Object)
	 */
	public static <T> void setFieldValue(Class<T> clazz, T obj, String fieldName, Object fieldValue) throws IllegalArgumentException, NullPointerException {
		Field field = getField(clazz, fieldName, false, false);
		setFieldValue(field, obj, fieldValue);
	}

	/**
	 * クラスのフィールドの値を設定します。<br>
	 * そのクラスのフィールドはどのアクセス修飾子でも問題ありません。<br>
	 * このメソッドは、指定されたClassオブジェクトが表すクラスが指定のフィールドをもっていない場合、<br>
	 * 親クラスのClassオブジェクトを取得して探し続けます。Objectクラスまで遡って、もっていない場合に<br>
	 * {@link NoSuchFieldException}(をラップした{@link ReflectionUtilException})をスローします。
	 * @param <T> そのフィールドを持つクラスの型
	 * @param clazz そのフィールドを持つクラスのClassインスタンス
	 * @param obj そのフィールドを持つクラスのインスタンス
	 * @param fieldName フィールド名
	 * @param fieldValue フィールドの値
	 * @throws IllegalArgumentException 渡されたObjectの型とフィールドの型が一致しない場合
	 * @throws NullPointerException インスタンスフィールドへ静的フィールドとしてアクセスしようとした場合
	 * @see Field#set(Object, Object)
	 */
	public static <T> void setFieldValueClassLoop(Class<T> clazz, T obj, String fieldName, Object fieldValue) {
		Field field = getField(clazz, fieldName, true, false);
		setFieldValue(field, obj, fieldValue);
	}

	/**
	 * 指定されたフィールドの値を設定します。<br>
	 * フィールドはどのアクセス修飾子でも問題ありません。
	 * @param field 値を設定するフィールド
	 * @param obj フィールドを持っているObject
	 * @param value フィールドの値
	 * @throws IllegalArgumentException フィールドを持つObjectの型と渡されたobjの型が一致しない、または渡されたvalueの型とフィールドの型が一致しない場合
	 * @throws IllegalAccessException 静的な定数フィールドに書き込もうとした場合
	 */
	public static void setFieldValue(Field field, Object obj, Object value)
			throws IllegalArgumentException {
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} catch (InaccessibleObjectException e) {
			throw new ReflectionUtilUnexpectedException("フィールドのアクセス制御の無効化に失敗しました。", e);
		} catch (IllegalAccessException e) {
			throw new ReflectionUtilException(e);
		}
	}

	/**
	 * フィールドインスタンスを取得します。
	 * @param <T> そのフィールドを持つクラスの型
	 * @param clazz そのフィールドを持つクラスのClassインスタンス
	 * @param obj そのフィールドを持つクラスのインスタンス
	 * @param fieldName フィールド名
	 * @param loopSearch 見つからなかった場合に親クラスのClassオブジェクトから探すかどうか
	 * @param suppressThrows 例外を抑制するかどうか
	 * @throws NoSuchFieldException ({@link ReflectionUtilException}でラップ)フィールドが存在しない場合
	 * @return そのクラスのフィールドインスタンス
	 */
	private static Field getField(Class<?> clazz, String fieldName, boolean loopSearch, boolean suppressThrows) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			if (loopSearch) {
				if (clazz.getSuperclass() != null) {
					Field field2 = getField(clazz.getSuperclass(), fieldName, loopSearch, true);
					if (field2 != null) {
						field = field2;
						suppressThrows = true;
					}
				}
			}
			if (!suppressThrows) {
				throw new ReflectionUtilException(e);
			}
		}
		return field;
	}
	
	/**
	 * メソッドを指定の引数で呼び出します。<br>
	 * そのクラスのメソッドはどのアクセス修飾子でも問題ありません。<br>
	 * Rが例外の場合、メソッド実行中に例外が発生した場合で、Rと一致した場合は<br>
	 * その例外インスタンスが返されます。
	 * @param <C> そのメソッドのあるクラスの型
	 * @param <R> そのメソッドの返り値の型
	 * @param clazz そのメソッドのあるクラスのClassインスタンス
	 * @param invokeObject 実際にメソッドを呼び出すクラスのインスタンス
	 * @param returnClass そのメソッドの返り値のClassインスタンス
	 * @param methodName メソッド名
	 * @param methodTypes メソッドの期待値の型のClassインスタンスの配列
	 * @param methodParams メソッドに渡す実際の値のインスタンスの配列
	 * @throws IllegalArgumentException 実パラメータに問題がある場合。(詳しくは{@link Method#invoke(Object, Object...)}を確認)
	 * @throws NullPointerException <code>invokeObject</code>に問題がある場合。(詳しくは{@link Method#invoke(Object, Object...)}を確認)
	 * @throws ClassCastException メソッドが返したオブジェクト型とこのメソッドの返すオブジェクト型が一致しない場合。
	 * @throws NoSuchMethodException ({@link ReflectionUtilException}でラップ)メソッドが存在しない場合
	 * @throws InvocationTargetException ({@link ReflectionUtilException}でラップ)メソッドが例外をスローした場合
	 * @return 呼び出したメソッドが返したインスタンス
	 * @see Method#invoke(Object, Object...)
	 */
	public static <C, R> R invokeMethod(Class<C> clazz, C invokeObject, Class<R> returnClass, String methodName, Class<?>[] methodTypes, Object[] methodParams)
			throws IllegalArgumentException, NullPointerException, ClassCastException {
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(methodName, methodTypes);
		} catch (NoSuchMethodException e) {
			throw new ReflectionUtilException(e);
		}
        return invokeMethod(method, invokeObject, returnClass, methodParams);
	}

	/**
	 * メソッドを指定の引数で呼び出します。<br>
	 * そのクラスのメソッドはどのアクセス修飾子でも問題ありません。<br>
	 * Tが例外の場合、メソッド実行中に例外が発生した場合で、Tと一致した場合は<br>
	 * その例外インスタンスが返されます。
	 * @param <T> そのメソッドの返り値の型
	 * @param method 呼び出すメソッドのMethodオブジェクト
	 * @param invokeObject 呼び出すメソッドを持っているインスタンス
	 * @param returnClass メソッドの返り値の型を表現するClassオブジェクト
	 * @param params メソッドに渡す実際の値のインスタンスの配列
	 * @throws IllegalArgumentException 実パラメータに問題がある場合。(詳しくは{@link Method#invoke(Object, Object...)}を確認)
	 * @throws NullPointerException <code>invokeObject</code>に問題がある場合。(詳しくは{@link Method#invoke(Object, Object...)}を確認)
	 * @throws ClassCastException メソッドが返したオブジェクト型とこのメソッドの返すオブジェクト型が一致しない場合。
	 * @throws InvocationTargetException ({@link ReflectionUtilException}でラップ)メソッドが例外をスローした場合
	 * @return
	 */
	public static <T> T invokeMethod(Method method, Object invokeObject, Class<T> returnClass, Object... params) {
		try {
            method.setAccessible(true);
            Object returnObj = method.invoke(invokeObject, params);
			return returnClass.cast(returnObj);
        } catch (InaccessibleObjectException e) {
			throw new ReflectionUtilUnexpectedException("メソッドのアクセス制御の無効化に失敗しました。", e);
        } catch (IllegalAccessException e) {
            throw new ReflectionUtilUnexpectedException("アクセスエラーが発生しました", e);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (returnClass.isInstance(cause)) {
				return returnClass.cast(cause);
			} else {
				throw new ReflectionUtilException(e);
			}
		}
	}
	
	/**
	 * 指定されたクラスの新しいインスタンスを作成します。<br>
	 * クラスのコンストラクタはどの修飾子でも問題ありません。
	 * @param <T> 新しいインスタンスを作成するクラスの型
	 * @param clazz 新しいインスタンスを作成するクラスのClassインスタンス
	 * @param constTypes コンストラクタの期待する値の型の配列
	 * @param constParams コンストラクタに実際に渡す値の配列
	 * @throws IllegalArgumentException コンストラクタの期待するObjectの型と実際のObjectの型が一致しない場合
	 * @throws NoSuchFieldException ({@link ReflectionUtilException}でラップ)コンストラクタが存在しない場合
	 * @throws InstantiationException ({@link ReflectionUtilException}でラップ)クラスが抽象クラスの場合
	 * @throws InvocationTargetException ({@link ReflectionUtilException}でラップ)コンストラクタが例外をスローした場合
	 * @return 新しいインスタンス
	 */
	public static <T> T newInstance(Class<T> clazz, Class<?>[] constTypes, Object[] constParams) throws IllegalArgumentException {
		Constructor<T> constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor(constTypes);
		} catch (NoSuchMethodException e) {
			throw new ReflectionUtilException(e);
		}
		return newInstance(constructor, constParams);
	}

	/**
	 * 指定されたコンストラクタを使用して新しいインスタンスを作成します。<br>
	 * コンストラクタはどの修飾子でも問題ありません。
	 * @param <T> 新しいインスタンスを作成するクラスの型
	 * @param constructor コンストラクタ
	 * @param params コンストラクタに渡す実際の値の配列
	 * @throws IllegalArgumentException コンストラクタの期待するObjectの型と実際のObjectの型が一致しない場合
	 * @throws InstantiationException ({@link ReflectionUtilException}でラップ)クラスが抽象クラスの場合
	 * @throws InvocationTargetException ({@link ReflectionUtilException}でラップ)コンストラクタが例外をスローした場合
	 * @return 新しいインスタンス
	 * @throws IllegalArgumentException
	 */
	public static <T> T newInstance(Constructor<T> constructor, Object... params) throws IllegalArgumentException {
		try {
			constructor.setAccessible(true);
		} catch (InaccessibleObjectException e) {
			throw new ReflectionUtilUnexpectedException("フィールドのアクセス制御の無効化に失敗しました。", e);
		}
		try {
			T returnObj = constructor.newInstance(params);
			return returnObj;
		} catch (InstantiationException | InvocationTargetException e) {
			throw new ReflectionUtilException(e);
		} catch (IllegalAccessException e) {
			throw new ReflectionUtilUnexpectedException("アクセスエラーが発生しました。", e);
		}
	}
	
}
