package gui.view.btn;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;

import gui.view.ButtonPanel;
import gui.view.CalcWindowFrame;

/**
 * ButtonPanelに表示するボタンの基となるクラスです。<br>
 * このクラスを継承したクラスのインスタンスをボタンの数だけ生成します。<br>
 * このクラス、及び継承した全クラスは{@link CalcWindowFrame CalcWindowFrameの問題}の影響を受けません。
 */
public abstract class Button extends JButton {
	
	/**
	 * このボタンを表示しているパネル
	 */
	final protected ButtonPanel PANEL;

	/**
	 * このボタンを準備します。
	 * @param text このボタンのラベル
	 * @param panel このボタンを表示するパネル
	 */
	protected Button(String text, ButtonPanel panel) {
		super(text);
		this.PANEL = panel;
		Font newFont = this.getFont().deriveFont(20F);
		this.setFont(newFont);
		Insets insets = this.getInsets();
		insets.set(10, insets.left, 10, 10);
	}
	
	/**
	 * ボタンのインスタンスを取得します。<br>
	 * 次のラベルのボタンは、専用に設計されたボタンを返します。<br>
	 * <p style="padding: 20px">"CE"、"C"、"⌫"、"S⇔L"</p>
	 * 次のラベルのボタンは、ラベルと入力内容が違う通常のボタンを返します。<br>
	 * <p style="padding: 20px">"÷"、"×"、"‐"、"＋"、"＝"</p>
	 * それ以外のラベルのボタンはすべて、ラベルと入力内容が同じ通常のボタンを返します。
	 * @param text ボタンに表示するラベル
	 * @param panel ボタンを表示するパネル
	 * @return ボタン
	 */
	public static Button getButton(String text, ButtonPanel panel) {
		return switch (text) {
			case "CE" -> new ButtonCE(panel);
		
			case "C" -> new ButtonC(panel);
			
			case "⌫" -> new ButtonBS(panel);
			
			case "÷" -> new ButtonDivide(panel);
			
			case "×" -> new ButtonMultiply(panel);
			
			case "‐" -> new ButtonMinus(panel);
			
			case "＋" -> new ButtonPlus(panel);
			
			case "＝" -> new ButtonEqual(panel);
			
			case "S⇔L" -> new ButtonChangeMode(panel);
		
			default -> new ButtonPlain(text, panel);
		};
	}
	
}
