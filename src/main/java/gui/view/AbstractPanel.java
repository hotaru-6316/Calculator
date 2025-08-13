package gui.view;

import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * このクラスでは、ButtonPanelやInputPanel等ウィンドウの部品を表示するのに使うパネルの共通の部分の実装を行っています。<br>
 * このクラスを継承したクラスでは、コンストラクタの内部でCalcWindowFrameのgetter等を実行すると、nullが返る可能性があります。<br>
 * 詳しくは{@link CalcWindowFrame}を確認してください。
 */
abstract class AbstractPanel extends JPanel {
	
	/**
	 * このパネルを表示しているウィンドウフレーム
	 */
	final private CalcWindowFrame WINDOW_FRAME;
	
	/**
	 * このパネルを初期化します。レイアウトマネージャの設定もここで行います。
	 * @param calcWindowFrame このパネルを表示しているWindowFrame
	 * @param layout このパネルに使用するレイアウトマネージャ
	 */
	AbstractPanel(CalcWindowFrame calcWindowFrame, LayoutManager layout) {
		super();
		this.WINDOW_FRAME = calcWindowFrame;
		this.setLayout(layout);
	}
	
	/**
	 * パネルの準備を行います。
	 */
	abstract void init();
	
	/**
	 * このパネルを表示しているWindowFrameを返します。
	 * @return このパネルを表示しているWindowFrame
	 */
	final public CalcWindowFrame getWindowFrame() {
		return WINDOW_FRAME;
	}
	
}
