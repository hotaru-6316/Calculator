package input;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;

import util.ReflectionUtility;

/**
 * CUI用の入力処理クラスをテストします
 */
public class CUIInputerTest {

    /**
     * 1行の取得処理をテストします。<br>
     * このメソッドでは入力元のInputStreamを置き換えることでテストを行っています。
     */
    @Test
    void 一行取得処理テスト() {
        ReflectionUtility.setFieldValue(CUIInputer.class, null, "scannerIs", new ByteArrayInputStream("Apple".getBytes()));
        assertEquals("Apple", new CUIInputer().getLine());
    }

    /**
     * 入力処理クラスの文字列表現への変換の実装をテストするメソッドです。
     */
    @Test
    void 文字列変換テスト() {
        assertEquals("CUIInputer []", new CUIInputer().toString());
    }
}
