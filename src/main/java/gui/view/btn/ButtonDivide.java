package gui.view.btn;

import gui.view.ButtonPanel;

/**
 * 割り算の文字列("/")をフィールドに追加するボタンを実装します。
 * ラベルは"÷"です。
 */
final class ButtonDivide extends ButtonPlain {

	/**
	 * ボタンを作成します。
	 * @param panel このボタンを表示するパネル
	 */
	public ButtonDivide(ButtonPanel panel) {
		super("/", panel, true);
		super.setText("÷", false);
	}

}
