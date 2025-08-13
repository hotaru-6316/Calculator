package gui.view.btn;

import java.io.IOException;
import java.io.StringReader;

import javax.swing.JLabel;
import javax.swing.JTextField;

import gui.view.ButtonPanel;

/**
 * 入力されている計算式から最後に入力された数字部分のみを削除するボタンを実装します。
 */
final class ButtonCE extends Button {

	/**
	 * このボタンを作成します。
	 * @param panel このボタンを表示するパネル
	 */
	protected ButtonCE(ButtonPanel panel) {
		super("CE", panel);
		this.addActionListener((e) -> {
			JTextField field = PANEL.getWindowFrame().getInputPanel().getTextField();
			JLabel label = PANEL.getWindowFrame().getInputPanel().getTextLabel();
			String text = field.getText();
			if (text.length() <= 0) {
				return;
			}
			String reversed = new StringBuilder().append(text).reverse().toString();
			StringReader reader = new StringReader(reversed);
			try {
				int read = reader.read();
				while (read != -1) {
					char charRead = (char) read;
					try {
						if (charRead != '.') {
							Integer.parseInt(String.valueOf(charRead));
						}
						reversed = reversed.substring(1, reversed.length());
					} catch (NumberFormatException e1) {
						if((charRead == '+') || (charRead == '-')) {
							int nextRead = reader.read();
							if (nextRead == -1) {
								reversed = reversed.substring(1, reversed.length());
							} else {
								char nextChar = (char) nextRead;
								switch (nextChar) {
									case '+':
									case '-':
									case '*':
									case '/':
										reversed = reversed.substring(1, reversed.length());
										break;
								}
							}
						}
						break;
					}
					read = reader.read();
				}
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
			text = new StringBuilder().append(reversed).reverse().toString();
			field.setText(text);
			label.setText(" ");
		});
	}

}
