package gui.view.btn;

import gui.view.ButtonPanel;

/**
 * 足し算の文字列("+")をフィールドに追加するボタンを実装します。
 * ラベルは"＋"です。
 */
final class ButtonPlus extends ButtonPlain {

	/**
	 * ボタンを作成します。
	 * @param panel このボタンを表示するパネル
	 */
	public ButtonPlus(ButtonPanel panel) {
		super("+", panel, true);
		this.setText("＋", false);
	}

}
