package annotation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * この操作はサポートされていないことを表します。<br>
 * このアノテーションが使用されているメソッドは、その操作をサポートしていません。<br>
 * テスト中の動作をこのアノテーションがあるかどうかで変更しているため、実行時に保持しています。
 */
@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Unsupported {

}
