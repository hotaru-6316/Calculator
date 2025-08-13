package item;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import item.FormulaItem.Builder;

/**
 * {@link FormulaItem.Builder}の動作をテストするクラスです。
 */
public final class FormulaItemBuilderTest {

	/**
	 * {@link Builder}の数式追加の処理をテストします。<br>
	 * {@link Builder#locked ロック}もテストします。
	 */
	@Test public void 数式追加処理のテスト() {
		FormulaItem.Builder itemBuilder = new FormulaItem.Builder();
		itemBuilder.add("16+12=");
		assertEquals("16+12=", itemBuilder.toFormula().get());
		itemBuilder.add("22");
		assertEquals("16+12=", itemBuilder.toFormula().get());
	}
	
	/**
	 * {@link Builder}の内容を数式({@link FormulaItem})に変換する処理をテストします。
	 */
	@Test public void 数式への変換処理テスト() {
		FormulaItem.Builder itemBuilder = new FormulaItem.Builder();
		itemBuilder.add("16+12=");
		assertEquals(new FormulaItem("16+12"), itemBuilder.toFormula());
	}

}
