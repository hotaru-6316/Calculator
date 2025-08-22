package gui.view;

import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import gui.view.btn.Button;

/**
 * 電卓ウィンドウのBorderLayout.CENTERに配置するボタンを表示するためのJPanelです。
 */
final public class ButtonPanel extends AbstractPanel {
	
	/**
	 * このパネルのレイアウト
	 */
	final static private LayoutManager PANEL_LAYOUT = new GridLayout(5, 4);
	
	/**
	 * このパネルをどのように配置するか
	 */
	final static private ComponentOrientation COMP_ORIENTATION = ComponentOrientation.LEFT_TO_RIGHT;
	
	/**
	 * このパネルに配置するボタンのラベルの一覧
	 */
	final static private String[] BUTTON_TEXTS = {
	          "CE", "C", "⌫", "÷",
			  "7" , "8", "9" , "×",
			  "4" , "5", "6" , "‐",
			  "1" , "2", "3" , "＋",
			"S⇔L", "0", "." , "＝"
	};
	
	/**
	 * このパネルで表示しているボタンのインスタンスのリスト
	 */
	final private List<Button> BUTTONS = new ArrayList<Button>();

	/**
	 * このクラスを初期化します。
	 * @param calcWindowFrame このパネルを表示しているWindowFrame
	 */
	ButtonPanel(CalcWindowFrame calcWindowFrame) {
		super(calcWindowFrame, PANEL_LAYOUT);
		this.setComponentOrientation(COMP_ORIENTATION);
		for (int i = 0; i < BUTTON_TEXTS.length; i++) {
			Button button = Button.getButton(BUTTON_TEXTS[i], this);
			this.add(button);
			this.BUTTONS.add(button);
		}
	}
	
	/**
	 * このパネルで表示しているボタンのインスタンスのリストを返します。
	 * @return このパネルで表示しているボタンのインスタンスのリスト
	 */
	public List<Button> getButtons() {
		return BUTTONS;
	}

	

}
