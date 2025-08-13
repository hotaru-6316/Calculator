package gui.view.btn;

import gui.view.ButtonPanel;
import gui.view.CalcWindowFrame;
import gui.view.CalcWindowFrame.CalcMode;

/**
 * 電卓の動作モードを切り替えるボタンを実装します。
 */
public class ButtonChangeMode extends Button {

	/**
	 * ボタンを作成します。
	 * @param panel このボタンを表示するパネル
	 */
	protected ButtonChangeMode(ButtonPanel panel) {
		super("S⇔L", panel);
		this.addActionListener((e) -> {
			switch (PANEL.getWindowFrame().getCalcMode()) {
			case CalcMode.Simple:
				CalcWindowFrame.getWindow(CalcMode.Logic);
				break;
			case CalcMode.Logic:
				CalcWindowFrame.getWindow(CalcMode.Simple);
				break;
			}
		});
	}

}
