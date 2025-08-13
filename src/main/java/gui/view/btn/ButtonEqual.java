package gui.view.btn;

import gui.view.ButtonPanel;

/**
 * 計算開始の文字列("=")をフィールドに追加するボタンを実装します。
 * ラベルは"＝"です。
 */
final class ButtonEqual extends ButtonPlain {
	
	/**
	 * ボタンを作成します。
	 * @param panel このボタンを表示するパネル
	 */
	public ButtonEqual(ButtonPanel panel) {
		super("=", panel, true);
		this.setText("＝", false);
	}

}
