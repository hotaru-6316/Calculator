package item;

import java.util.Objects;

import annotation.Unsupported;

/**
 * 計算結果を格納するためのクラスです
 */
public class CalcResult extends AbstractItem<Double> {

    /**
     * 結果を格納しています。
     */
    private final Double RESULT;

    @Override
    public int hashCode() {
        return Objects.hash(RESULT);
    }

    @Override
    public boolean equals(Object obj) {
    	if (obj == null) {
			return false;
		}
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        CalcResult other = (CalcResult) obj;
        if (RESULT == null) {
            if (other.RESULT != null)
                return false;
        } else if (!RESULT.equals(other.RESULT))
            return false;
        return true;
    }

    /**
     * 計算結果を保存します。
     * @param result 計算結果
     * @throws IllegalArgumentException 計算結果がnullの場合
     */
    public CalcResult(double result) {
    	this.checkNull(result);
        this.RESULT = result;
    }

    /**
     * 計算結果を返します。エラーの場合はnullが返ります
     * @return 計算結果
     */
    @Override
    public Double get() {
        return this.RESULT;
    }

    /**
     * 保存されている結果データを書き換えることは出来ません。<br />
     * そのため、常にUnsupportedOperationExceptionをスローします。
     * @param t 保存する結果データ
     * @throws java.lang.UnsupportedOperationException 常に
     */
    @Override
    @Unsupported
    public void set(Double t) {
        throw new UnsupportedOperationException("保存されているデータを上書きすることは出来ません");
    }

    @Override
    public String toString() {
        return "CalcResult [RESULT=" + RESULT + "]";
    }

}
