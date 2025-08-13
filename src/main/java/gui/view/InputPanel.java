package gui.view;

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JTextField;

import annotation.Initializable;

/**
 * 電卓ウィンドウのBorderLayout.NORTHに配置する入力等を表示するためのJPanelです。<br>
 * このクラスでは、コンストラクタの内部でCalcWindowFrameのgetter等を実行すると、nullが返る可能性があります。<br>
 * 詳しくは{@link CalcWindowFrame}を確認してください。
 */
@Initializable
final public class InputPanel extends AbstractPanel {
	
	/**
	 * このパネルのレイアウトマネージャ
	 */
	final private static LayoutManager PANEL_LAYOUT = new BorderLayout();
	
	/**
	 * 入力された数式を表示するためのラベル
	 */
	final private InputTextLabel TEXT_LABEL = new InputTextLabel();
	
	/**
	 * 数式を入力するためのテキストフィールド
	 */
	final private InputTextField TEXT_FIELD = new InputTextField(this);

	/**
	 * このクラスを初期化します。
	 * @param windowFrame このパネルを使用するウィンドウフレーム
	 */
	InputPanel(CalcWindowFrame windowFrame) {
		super(windowFrame, PANEL_LAYOUT);
		this.add(TEXT_LABEL, BorderLayout.NORTH);
		this.add(TEXT_FIELD, BorderLayout.SOUTH);
	}

	@Override
	void init() {
		TEXT_LABEL.init();
		TEXT_FIELD.init();
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
