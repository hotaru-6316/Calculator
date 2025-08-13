package input;

import java.io.InputStream;
import java.util.Scanner;

/**
 * CUIでの入力を処理するクラスです。
 */
public class CUIInputer extends AbstractInputer {
	
	/**
	 * クラスを初期化します
	 */
	public CUIInputer() {
	}

    @Override
    public String toString() {
        return "CUIInputer []";
    }

    /**
     * 入力に使用するInputStreamです
     */
    private static InputStream scannerIs = System.in;
    

    /**
     * 行入力に使用するScannerです
     */
    private Scanner scanner = new Scanner(scannerIs);

    @Override
    public String getLine() {
        return scanner.nextLine().trim();
    }

}
