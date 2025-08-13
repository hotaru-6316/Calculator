package gui.view;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.JTextField;

import annotation.Initializable;
import gui.doc.InputTextFieldDocument;

/**
 * InputPanelに「計算式入力フィールド」を表示するJTextFieldです。<br>
 * このクラスでは、コンストラクタの内部でCalcWindowFrameのgetter等を実行すると、nullが返る可能性があります。<br>
 * 詳しくは{@link CalcWindowFrame}を確認してください。
 */
@Initializable
final public class InputTextField extends JTextField {
	
	/**
	 * このフィールドを表示しているパネル
	 */
	final private InputPanel PANEL;
	
	/**
	 * このフィールドをインスタンス化します。
	 * @param panel このテキスト入力フィールドを使用するパネル
	 */
	InputTextField(InputPanel panel) {
		super();
		this.PANEL = panel;
	}
	
	/**
	 * フィールドの準備を行います。
	 */
	void init() {
		this.setHorizontalAlignment(RIGHT);
		this.setDocument(new InputTextFieldDocument(PANEL));
		Insets insets = this.getInsets();
		insets.set(10, insets.left, 10, 10);
		this.setMargin(insets);
		Font newFont = this.getFont().deriveFont(20F);
		this.setFont(newFont);
		this.addActionListener((e) -> {
			Object obj = e.getSource();
			if (!(obj instanceof InputTextField)) {
				throw new IllegalArgumentException("このイベントが発生したオブジェクトがInputTextFieldではありません。");
			}
			InputTextField field = (InputTextField) obj;
			String text = field.getText();
			if (text.length() == 0) {
				return;
			}
			field.setText(text + "=");
		});
	}

}
