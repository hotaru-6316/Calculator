package gui.view.btn;

import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import gui.view.ButtonPanel;

/**
 * バックスペースのボタンを実装します。
 */
final class ButtonBS extends Button {

	/**
	 * バックスペースのボタンを作成します。
	 * @param panel ボタンを表示するパネル
	 */
	public ButtonBS(ButtonPanel panel) {
		super("⌫", panel);
		this.addActionListener((e) -> {
			JTextField field = PANEL.getWindowFrame().getInputPanel().getTextField();
			Document doc = field.getDocument();
			if (doc.getLength() <= 0) {
				return;
			}
			try {
				doc.remove(doc.getLength() - 1, 1);
			} catch (BadLocationException e1) {
				throw new RuntimeException(e1);
			}
		});
	}

}
