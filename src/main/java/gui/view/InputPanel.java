package gui.view;

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 電卓ウィンドウのBorderLayout.NORTHに配置する入力等を表示するためのJPanelです。
 */
final public class InputPanel extends AbstractPanel {
	
	/**
	 * このパネルのレイアウトマネージャ
	 */
	final private static LayoutManager PANEL_LAYOUT = new BorderLayout();
	
	/**
	 * 入力された数式を表示するためのラベル
	 */
	final private InputTextLabel TEXT_LABEL;
	
	/**
	 * 数式を入力するためのテキストフィールド
	 */
	final private InputTextField TEXT_FIELD;
	
	/**
	 * 履歴機能へアクセスするためのUIを提供するボタン
	 */
	final private HistoryButton HISTORY_BUTTON;

	/**
	 * このクラスを初期化します。
	 * @param windowFrame このパネルを使用するウィンドウフレーム
	 */
	InputPanel(CalcWindowFrame windowFrame) {
		super(windowFrame, PANEL_LAYOUT);
		TEXT_LABEL = new InputTextLabel();
		TEXT_FIELD = new InputTextField(this);
		HISTORY_BUTTON = new HistoryButton(windowFrame, TEXT_FIELD);
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(HISTORY_BUTTON);
		labelPanel.add(TEXT_LABEL);
		this.add(labelPanel, BorderLayout.NORTH);
		this.add(TEXT_FIELD, BorderLayout.SOUTH);
	}

	/**
	 * このパネルで表示しているラベルを返します。
	 * @return 入力された数式を表示するためのラベル
	 * @see #TEXT_LABEL
	 */
	public JLabel getTextLabel() {
		return TEXT_LABEL;
	}

	/**
	 * このパネルで表示しているテキストフィールドを返します。
	 * @return 数式を入力するためのテキストフィールド
	 * @see #TEXT_FIELD
	 */
	public JTextField getTextField() {
		return TEXT_FIELD;
	}

}
