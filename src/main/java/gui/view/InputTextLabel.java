package gui.view;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import annotation.Initializable;

/**
 * InputPanelに「計算式表示エリア」を表示するJLabelです。<br>
 * このクラスでは、コンストラクタの内部でCalcWindowFrameのgetter等を実行すると、nullが返る可能性があります。<br>
 * 詳しくは{@link CalcWindowFrame}を確認してください。
 */
@Initializable
final public class InputTextLabel extends JLabel {
	
	/**
	 * このラベルをインスタンス化します。
	 */
	InputTextLabel() {
		super(" ");
	}
	
	/**
	 * ラベルの準備を行います。
	 */
	void init() {
		this.setHorizontalAlignment(RIGHT);
		Font newFont = this.getFont().deriveFont(15F);
		this.setFont(newFont);
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 50));
	}
	
	

}
