package annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import gui.view.CalcWindowFrame;

/**
 * この注釈が使用されているクラスは、init()メソッドで初期化が必要である事を表します。<br>
 * この注釈が使用されているクラスでは、コンストラクタの内部でCalcWindowFrameのgetter等を実行すると、nullが返る可能性があります。<br>
 * 詳しくは{@link CalcWindowFrame}を確認してください。
 */
@Retention(SOURCE)
@Target(TYPE)
public @interface InitMethodRequired {

}
