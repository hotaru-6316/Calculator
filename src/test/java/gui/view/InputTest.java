package gui.view;

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.junit.jupiter.api.Test;

import gui.doc.InputTextFieldDocument;
import util.ReflectionUtility;
/**
 * 入力フィールドとフィルタするドキュメント、およびラベルの動作のテストを行います。
 */
public final class InputTest extends GUIFrameTest {

	/**
	 * 入力フィールドに不正な文字と正しい文字を混ぜて入れると、正しい文字だけ保存されるかテストします。
	 */
	@Test public void 通常時の入力フィールド動作テスト() {
		if (SwingUtilities.isEventDispatchThread()) {
			JTextField field = CalcWindowFrame.getCurrentWindow().getInputPanel().getTextField();
			field.setText("1234567890-^\\qwertyuiop@[asdfghjkl;:]zxcvbnm,./!\"#$%&'()~|QWERTYUIOP`{ASDFHJL+*}ZXCVBNM<>?_");
			assertEquals("1234567890-./()+*", field.getText());
		} else {
			runInEDT(this::通常時の入力フィールド動作テスト);
		}
	}
	
	/**
	 * 最初の計算が行われた後、最初の入力(firstInput)が行われると正しい動作をするかテストします。<br>
	 * どんな動作をするかはfirstInputを確認してください。
	 * @see InputTextFieldDocument#firstInput
	 */
	@Test public void 最初の入力時の入力フィールドの動作テスト() {
		if (SwingUtilities.isEventDispatchThread()) {
			JTextField field = CalcWindowFrame.getCurrentWindow().getInputPanel().getTextField();
			JLabel label = CalcWindowFrame.getCurrentWindow().getInputPanel().getTextLabel();
			field.setText("20");
			label.setText("12+8=");
			assertEquals("20", field.getText());
			assertEquals("12+8=", label.getText());
			if (!(field.getDocument() instanceof InputTextFieldDocument)) {
				fail("フィールドのドキュメントにInputTextFieldDocument以外が使用されています");
			}
			InputTextFieldDocument doc = (InputTextFieldDocument) field.getDocument();
			setInputTextFieldDocumentFirstInput(doc);
			try {
				doc.insertString(doc.getLength(), "2", null);
			} catch (BadLocationException e) {
				fail("追記に失敗しました", e);
			}
			assertEquals("2", field.getText());
			assertEquals(" ", label.getText());
			field.setText("20");
			label.setText("12+8=");
			setInputTextFieldDocumentFirstInput(doc);
			try {
				doc.insertString(doc.getLength(), "+2", null);
			} catch (BadLocationException e) {
				fail("追記に失敗しました", e);
			}
			assertEquals("20+2", field.getText());
			assertEquals(" ", label.getText());
			field.setText("20");
			label.setText("12+8=");
			setInputTextFieldDocumentFirstInput(doc);
			try {
				doc.remove(field.getText().length() - 1, 1);
			} catch (BadLocationException e) {
				fail("削除に失敗しました", e);
			}
			assertEquals("", field.getText());
			assertEquals(" ", label.getText());
		} else {
			runInEDT(this::最初の入力時の入力フィールドの動作テスト);
		}
	}

	private void setInputTextFieldDocumentFirstInput(InputTextFieldDocument doc) {
		ReflectionUtility.setFieldValue(InputTextFieldDocument.class, doc, "firstInput", true);
	}
	
	/**
	 * 入力のチェックがスキップに設定されている場合、不正な文字が入力時に混ざっていてもそのまま全て入力されるかテストします。
	 */
	@Test public void 入力スキップ時に不正な文字入力テスト() {
		if (SwingUtilities.isEventDispatchThread()) {
			JTextField field = CalcWindowFrame.getCurrentWindow().getInputPanel().getTextField();
			if (!(field.getDocument() instanceof InputTextFieldDocument)) {
				fail("フィールドのドキュメントにInputTextFieldDocument以外が使用されています");
			}
			InputTextFieldDocument doc = (InputTextFieldDocument) field.getDocument();
			ReflectionUtility.setFieldValue(InputTextFieldDocument.class, doc, "skipCheck", true);
			field.setText("1234567890-^\\qwertyuiop@[asdfghjkl;:]zxcvbnm,./!\"#$%&'()~|QWERTYUIOP`{ASDFHJL+*}ZXCVBNM<>?_");
			assertEquals("1234567890-^\\qwertyuiop@[asdfghjkl;:]zxcvbnm,./!\"#$%&'()~|QWERTYUIOP`{ASDFHJL+*}ZXCVBNM<>?_", field.getText());
		} else {
			runInEDT(this::入力スキップ時に不正な文字入力テスト);
		}
	}
	
	/**
	 * 計算開始の文字("=")を入れたときに正しく計算が行われるかテストします。
	 */
	@Test public void 計算開始の文字入力時に計算が行われるかテスト() {
		if (SwingUtilities.isEventDispatchThread()) {
			JTextField field = CalcWindowFrame.getCurrentWindow().getInputPanel().getTextField();
			JLabel label = CalcWindowFrame.getCurrentWindow().getInputPanel().getTextLabel();
			field.setText("12+90=");
			assertEquals("12+90=", label.getText());
			assertEquals("102.0", field.getText());
			field.setText("20+20=400");
			assertEquals("20+20=", label.getText());
			assertEquals("40.0", field.getText());
			field.setText("=");
			assertEquals("", field.getText());
			assertEquals(" ", label.getText());
		} else {
			runInEDT(this::計算開始の文字入力時に計算が行われるかテスト);
		}
	}
	
	/**
	 * 最初の入力時と通常時に正しく1文字削除(BackSpace)が動作するかテストします。
	 */
	@Test public void バックスペース動作テスト() {
		if (SwingUtilities.isEventDispatchThread()) {
			JTextField field = CalcWindowFrame.getCurrentWindow().getInputPanel().getTextField();
			JLabel label = CalcWindowFrame.getCurrentWindow().getInputPanel().getTextLabel();
			field.setText("12+90=");
			assertEquals("12+90=", label.getText());
			assertEquals("102.0", field.getText());
			try {
				field.getDocument().remove(field.getDocument().getLength() - 1, 1);
			} catch (BadLocationException e) {
				fail("1文字削除に失敗しました", e);
			}
			assertEquals("", field.getText());
			assertEquals(" ", label.getText());
			field.setText("12+90");
			try {
				field.getDocument().remove(field.getDocument().getLength() - 1, 1);
			} catch (BadLocationException e) {
				fail("1文字削除に失敗しました", e);
			}
			assertEquals("12+9", field.getText());
		} else {
			runInEDT(this::バックスペース動作テスト);
		}
	}
	
	/**
	 * 入力フィールドに不正な計算式を入力するとフィールドに「エラー」と表示されるかテストします。
	 */
	@Test public void 不正な数式入力テスト() {
		if (SwingUtilities.isEventDispatchThread()) {
			JTextField field = CalcWindowFrame.getCurrentWindow().getInputPanel().getTextField();
			field.setText("12*9+*8=");
			assertEquals("エラー!", field.getText());
		} else {
			runInEDT(this::不正な数式入力テスト);
		}
	}
	
	/**
	 * 入力フィールドでEnterを押すと計算が行われるかテストします。
	 */
	@Test public void Enterでの計算開始テスト() {
		if (SwingUtilities.isEventDispatchThread()) {
			JTextField field = CalcWindowFrame.getCurrentWindow().getInputPanel().getTextField();
			field.setText("12+90");
			field.postActionEvent();
			assertEquals("102.0", field.getText());
		} else {
			runInEDT(this::Enterでの計算開始テスト);
		}
	}
	
	

}
