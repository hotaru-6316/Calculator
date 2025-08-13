package gui.view.btn;

import gui.view.ButtonPanel;

/**
 * 入力されている計算式を削除するボタンを実装します。
 */
final class ButtonC extends Button {

	/**
	 * このボタンを作成します。
	 * @param panel このボタンを表示するパネル
	 */
	protected ButtonC(ButtonPanel panel) {
		super("C", panel);
		this.addActionListener((e) -> {
			PANEL.getWindowFrame().getInputPanel().getTextField().setText("");
			PANEL.getWindowFrame().getInputPanel().getTextLabel().setText(" ");
		});
	}

}
