package gui.view;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * InputPanelに「計算式表示エリア」を表示するJLabelです。
 */
final public class InputTextLabel extends JLabel {
	
	/**
	 * このラベルをインスタンス化します。
	 */
	InputTextLabel() {
		super(" ");
		this.setHorizontalAlignment(RIGHT);
		Font newFont = this.getFont().deriveFont(15F);
		this.setFont(newFont);
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 50));
	}
	
	

}
