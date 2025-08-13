package gui.view.btn;

import gui.view.ButtonPanel;

/**
 * 掛け算の文字列("*")をフィールドに追加するボタンを実装します。
 * ラベルは"×"です。
 */
final class ButtonMultiply extends ButtonPlain {

	/**
	 * ボタンを作成します。
	 * @param panel このボタンを表示するパネル
	 */
	public ButtonMultiply(ButtonPanel panel) {
		super("*", panel, true);
		this.setText("×", false);
	}

}
