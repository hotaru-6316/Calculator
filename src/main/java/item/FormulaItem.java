package item;

import java.util.Objects;

import annotation.Unsupported;

/**
 * 数式を保存します
 */
public class FormulaItem extends AbstractItem<String> {

    /**
     * 数式を保存するためのクラスを作成するためのクラスです。
     */
    public static class Builder {
    	/**
    	 * クラスを初期化します
    	 */
    	public Builder() {
		}
    	
        /**
         * 数式をテキスト形式で一時的に保管します
         */
        private StringBuilder formulaTextBuilder = new StringBuilder();

        /**
         * このクラスが読み取り専用になっているかどうか
         */
        private boolean locked = false;

        /**
         * 指定の数式を追加します。<br>
         * (例えば、"12+5"に"-6"を追加する等)<br>
         * 最後の文字列に"="が入っている場合、このクラスは以後変更不可になります
         * @param formula 追加する数式テキスト
         * @throws IllegalArgumentException formulaがnullの場合
         */
        public void add(String formula) {
            if (locked) {
                return;
            }
            if (formula == null) {
				throw new IllegalArgumentException("追加する数式テキストがnullです");
			}
            formulaTextBuilder.append(formula);
            if(formulaTextBuilder.charAt(formulaTextBuilder.length() - 1) == '=') {
                locked = true;
            }
        }

        /**
         * 数式クラスに変換します。<br>
         * このクラスに一時的に保存されているテキスト形式の数式を数式クラスに変換します。<br>
         * 返される数式クラスはこのクラスの数式データと同期していないため、
         * このメソッドで変換した後にこのクラスのメソッドで書き換えた場合は再度変換が必要です。
         * @return 変換後の数式クラス
         */
        public FormulaItem toFormula() {
            return new FormulaItem(formulaTextBuilder.toString());
        }
    }

    /**
     * 数式を保存している定数
     */
    private final String FORMULA;

    @Override
    public int hashCode() {
        return Objects.hash(FORMULA);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FormulaItem other = (FormulaItem) obj;
        if (FORMULA == null) {
            if (other.FORMULA != null)
                return false;
        } else if (!FORMULA.equals(other.FORMULA))
            return false;
        return true;
    }

    /**
     * 数式を保存して読みだせるようにします。<br />
     * 一度保存すると書き換えできないため、書き換える場合はBuilderを使用します。
     * 
     * @param formula 数式
     * @see item.FormulaItem.Builder Builder
     * @throws IllegalArgumentException formulaがnullの場合
     */
    public FormulaItem(String formula) {
    	this.checkNull(formula);
        if (!formula.endsWith("=")) {
            formula += "=";
        }
        FORMULA = formula;
    }

    /**
     * 保存されている数式を読み出します
     * @return 保存されている数式
     */
    @Override
    public String get() {
        return this.FORMULA;
    }

    /**
     * 保存されている数式を書き換えることは出来ません。<br />
     * そのため、常にUnsupportedOperationExceptionをスローします。
     * @param formula 保存する数式
     * @throws java.lang.UnsupportedOperationException 常に
     */
    @Override
    @Unsupported
    public void set(String formula) {
        throw new UnsupportedOperationException("保存されているデータを上書きすることは出来ません");
    }

    @Override
    public String toString() {
        return "FormulaItem [FORMULA=" + FORMULA + "]";
    }

}
