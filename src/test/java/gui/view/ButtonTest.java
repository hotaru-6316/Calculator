package gui.view;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

import gui.view.CalcWindowFrame.CalcMode;
import gui.view.btn.Button;
import gui.view.btn.ButtonPlain;

/**
 * 各種ボタンのテストを行うクラスです。
 */
public class ButtonTest extends GUIFrameTest {
	
	/**
	 * テストするボタンのラベルの配列。
	 */
	final static private String[] BUTTON_LABELS = {
	          "CE", "C", "⌫", "÷",
			  "7" , "8", "9" , "×",
			  "4" , "5", "6" , "‐",
			  "1" , "2", "3" , "＋",
			"S⇔L", "0", "." , "＝"
	};
	
	/**
	 * ボタンを押すとフィールドにどんな値が追加されるかの配列。<br>
	 * 特殊なボタン({@link gui.view.btn.ButtonCE CE}や{@link gui.view.btn.ButtonC C}等)のテストには使用されないので、該当する位置には空文字列が入っている。
	 */
	final private static String[] BUTTON_PLAINTEXT = {
			 "",  "",  "", "/",
			"7", "8", "9", "*",
			"4", "5", "6", "-",
			"1", "2", "3", "+",
			"" , "0", ".", ""
	};
	
	/**
	 * 各ボタンのクラスの名前の配列。<br>
	 * 実際の判定時にはこの文字列の前に"gui.view.btn."が追記されます。
	 */
	final private static String[] BUTTON_CLASS_NAMES = {
			"ButtonCE", "ButtonC", "ButtonBS", "ButtonDivide",
			"ButtonPlain", "ButtonPlain", "ButtonPlain", "ButtonMultiply",
			"ButtonPlain", "ButtonPlain", "ButtonPlain", "ButtonMinus",
			"ButtonPlain", "ButtonPlain", "ButtonPlain", "ButtonPlus",
			"ButtonChangeMode", "ButtonPlain", "ButtonPlain", "ButtonEqual"
	};
	
	/**
	 * 各ボタンを実装しているクラスの名前が正しいか確認します。
	 */
	@Test
	public void ボタンのクラス名チェック() {
		if (SwingUtilities.isEventDispatchThread()) {
			List<Button> buttons = CalcWindowFrame.getCurrentWindow().getButtonPanel().getButtons();
			for (int i = 0; i < BUTTON_CLASS_NAMES.length; i++) {
				String className = BUTTON_CLASS_NAMES[i];
				assertEquals("gui.view.btn." + className, buttons.get(i).getClass().getName());
			} 
		} else {
			runInEDT(this::ボタンのクラス名チェック);
		}
	}

	/**
	 * 各ボタンのラベルが正しいか確認します。
	 */
	@Test
	public void ボタンのラベルチェック() {
		if (SwingUtilities.isEventDispatchThread()) {
			List<Button> buttons = CalcWindowFrame.getCurrentWindow().getButtonPanel().getButtons();
			for (int i = 0; i < BUTTON_LABELS.length; i++) {
				String buttonExpectedText = BUTTON_LABELS[i];
				String buttonText = buttons.get(i).getText();
				assertEquals(buttonExpectedText, buttonText);
			}
		} else {
			runInEDT(this::ボタンのラベルチェック);
		}
	}
	
	/**
	 * 各ボタンの中から、{@link ButtonPlain 通常ボタン}が正しく動作するかテストします。
	 */
	@Test
	public void 通常ボタンの動作テスト() {
		if (SwingUtilities.isEventDispatchThread()) {
			List<Button> buttons = CalcWindowFrame.getCurrentWindow().getButtonPanel().getButtons();
			for (int i = 0; i < buttons.size(); i++) {
				Button button = buttons.get(i);
				if (button instanceof ButtonPlain) {
					String buttonString = ((ButtonPlain) button).getPushButtonString();
					if (buttonString.equals("=")) {
						continue;
					}
					JTextField field = CalcWindowFrame.getCurrentWindow().getInputPanel().getTextField();
					field.setText("");
					button.doClick();
					String currentString = field.getText();
					assertEquals(BUTTON_PLAINTEXT[i], currentString);
				}
			}
		} else {
			runInEDT(this::通常ボタンの動作テスト);
		}
	}
	
	/**
	 * 各ボタンの中から、特殊なボタン({@link gui.view.btn.ButtonCE CE}や{@link gui.view.btn.ButtonC C}等)が正しく動作するかテストします。<br>
	 * ただし、一部の特殊なボタンは別のメソッド内でテストするため、それらはこのメソッドではテストしません。
	 */
	@Test
	public void 特殊ボタンの動作テスト() {
		if (SwingUtilities.isEventDispatchThread()) {
			List<Button> buttons = CalcWindowFrame.getCurrentWindow().getButtonPanel().getButtons();
			for (int i = 0; i < buttons.size(); i++) {
				JTextField field = CalcWindowFrame.getCurrentWindow().getInputPanel().getTextField();
				Button button = buttons.get(i);
				switch (button.getClass().getName()) {
				case "gui.view.btn.ButtonBS":
					field.setText("918");
					button.doClick();
					assertEquals("91", field.getText());
					field.setText("");
					assertDoesNotThrow(() -> button.doClick());
					assertEquals("", field.getText());
					break;
					
				case "gui.view.btn.ButtonCE":
					field.setText("139*1937+183");
					button.doClick();
					assertEquals("139*1937+", field.getText());
					field.setText("");
					assertDoesNotThrow(() -> button.doClick());
					assertEquals("", field.getText());
					field.setText("12+-99");
					button.doClick();
					assertEquals("12+", field.getText());
					field.setText("-91.9");
					button.doClick();
					assertEquals("", field.getText());
					break;
					
				case "gui.view.btn.ButtonC":
					field.setText("139*1937+183");
					button.doClick();
					assertEquals("", field.getText());
					break;

				default:
					break;
				}
			}
		} else {
			runInEDT(this::特殊ボタンの動作テスト);
		}
	}
	
	/**
	 * モードを変更するボタンを2回押してモードの切り替えボタンの動作をテストします。
	 */
	@Test
	public void モード変更ボタン2回押しテスト() {
		if (SwingUtilities.isEventDispatchThread()) {
			CalcMode mode = null;
			for(int i = 0; i < 2; i++) {
				Button button = CalcWindowFrame.getCurrentWindow().getButtonPanel().getButtons().get(16);
				assertEquals(button.getClass().getName(), "gui.view.btn.ButtonChangeMode");
				CalcWindowFrame frame = CalcWindowFrame.getCurrentWindow();
				mode = frame.getCalcMode();
				button.doClick();
				if (mode.equals(CalcMode.Simple)) {
					assertEquals(CalcMode.Logic, CalcWindowFrame.getCurrentWindow().getCalcMode());
				} else {
					assertEquals(CalcMode.Simple, CalcWindowFrame.getCurrentWindow().getCalcMode());
				}
			}
			
		} else {
			runInEDT(this::モード変更ボタン2回押しテスト);
		}
	}
	
	/**
	 * {@link gui.view.btn.ButtonEqual 計算を開始するボタン}を押して実際に計算が行われるかテストします。
	 */
	@Test
	public void 計算開始ボタン動作テスト() {
		if (SwingUtilities.isEventDispatchThread()) {
			List<Button> buttons = CalcWindowFrame.getCurrentWindow().getButtonPanel().getButtons();
			for (int i = 0; i < buttons.size(); i++) {
				JTextField field = CalcWindowFrame.getCurrentWindow().getInputPanel().getTextField();
				Button button = buttons.get(i);
				switch (button.getClass().getName()) {
				case "gui.view.btn.ButtonEqual":
					field.setText("918+193");
					button.doClick();
					assertEquals("1111.0", field.getText());
					break;

				default:
					break;
				}
			}
		} else {
			runInEDT(this::計算開始ボタン動作テスト);
		}
	}
	
	/**
	 * 計算を行われた後、次の動作をするかテストします。
	 * <p>・そのボタンが{@link ButtonPlain 通常ボタン}でない場合、無視します。</p>
	 * <p>・そのボタンが{@link ButtonPlain 通常ボタン}で、数字のボタンの場合、フィールドの値はボタンのラベルと同じになるはずです。</p>
	 * <p>・そのボタンが{@link ButtonPlain 通常ボタン}で、+-/*のボタンのいずれかの場合、フィールドの値は計算結果に<br>
	 * {@link ButtonPlain#pushButtonString 押すとフィールドに追加する文字列}が追記された値になっているはずです。</p>
	 */
	@Test
	public void 計算後の最初の入力テスト() {
		if (SwingUtilities.isEventDispatchThread()) {
			List<Button> buttons = CalcWindowFrame.getCurrentWindow().getButtonPanel().getButtons();
			for (int i = 0; i < buttons.size(); i++) {
				Button button = buttons.get(i);
				JTextField field = CalcWindowFrame.getCurrentWindow().getInputPanel().getTextField();
				if (button instanceof ButtonPlain) {
					boolean numeric = false;
					try {
						Integer.parseInt(button.getText());
						numeric = true;
					} catch (NumberFormatException e) { ; }
					if (numeric) {
						field.setText("126+193=");
						button.doClick();
						assertEquals(button.getText(), field.getText());
					}
					switch (button.getClass().getName()) {
					case "gui.view.btn.ButtonPlus":
					case "gui.view.btn.ButtonMultiply":
					case "gui.view.btn.ButtonMinus":
					case "gui.view.btn.ButtonDivide":
						field.setText("12+70=");
						button.doClick();
						assertEquals("82.0" + ((ButtonPlain) button).getPushButtonString(), field.getText());
						break;

					default:
						break;
					}
				}
			}
		} else {
			runInEDT(this::計算後の最初の入力テスト);
		}
	}

}
