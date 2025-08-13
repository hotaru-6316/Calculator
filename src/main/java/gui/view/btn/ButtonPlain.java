package gui.view.btn;

import javax.swing.JTextField;

import gui.doc.InputTextFieldDocument;
import gui.view.ButtonPanel;

/**
 * 押すとラベルの文字列がフィールドに追加されるボタンの実装です。<br>
 * ラベルの文字列とフィールドに追加する文字列を別にすることもできます。
 */
public class ButtonPlain extends Button {
	
	/**
	 * 押すとフィールドに追加する文字列
	 */
	private String pushButtonString;
	
	/**
	 * 計算後の最初の入力を確認するボタンを作成します。
	 * @param text このボタンのラベルの文字
	 * @param panel このボタンを表示するパネル
	 */
	public ButtonPlain(String text, ButtonPanel panel) {
		this(text, panel, false);
	}

	/**
	 * ボタンを作成します。<br>
	 * skipCheckFirstInputがfalseの場合、{@link InputTextFieldDocument#firstInput InputTextFieldDocumentのfirstInput}の値が確認され、<br>
	 * trueの場合は数字入力時や文字削除時に入力フィールドとラベルを空にした後に処理を行います。
	 * @param text このボタンのラベルの文字
	 * @param panel このボタンを表示するパネル
	 * @param skipCheckFirstInput 計算後の最初の入力の確認をスキップする
	 */
	public ButtonPlain(String text, ButtonPanel panel, boolean skipCheckFirstInput) {
		super(text, panel);
		this.setText(text);
		this.addActionListener((e) -> {
			JTextField field = PANEL.getWindowFrame().getInputPanel().getTextField();
			if ((!skipCheckFirstInput) && (((InputTextFieldDocument) field.getDocument()).isFirstInput())) {
				field.setText("");
			}
			field.setText(field.getText() + this.pushButtonString);
		});
	}

	/**
	 * ボタンのラベルとフィールドに追加する文字列を設定します。
	 * @param text ボタンの新しいラベル
	 */
	@Override
	public void setText(String text) {
		this.setText(text, true);
	}
	
	/**
	 * ボタンのラベルとフィールドに追加する文字列を設定します。<br>
	 * ボタンのラベルのみ変更することも可能です。
	 * @param text ボタンの新しいラベル
	 * @param changePushButtonString ボタンのフィールドに追加する文字列も変更する
	 */
	protected void setText(String text, boolean changePushButtonString) {
		super.setText(text);
		if (changePushButtonString) {
			this.pushButtonString = text;
		}
	}

	/**
	 * 押すとフィールドに追加する文字列を返します。
	 * @return 押すとフィールドに追加する文字列
	 */
	public String getPushButtonString() {
		return pushButtonString;
	}

}
