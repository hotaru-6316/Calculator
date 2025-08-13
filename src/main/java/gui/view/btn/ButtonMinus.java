package gui.view.btn;

import gui.view.ButtonPanel;

/**
 * 引き算の文字列("-")をフィールドに追加するボタンを実装します。
 * ラベルは"‐"です。
 */
final class ButtonMinus extends ButtonPlain {
	
	/**
	 * ボタンを作成します。
	 * @param panel このボタンを表示するパネル
	 */
	public ButtonMinus(ButtonPanel panel) {
		super("-", panel, true);
		this.setText("‐", false);
	}

}
