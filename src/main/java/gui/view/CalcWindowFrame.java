package gui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import annotation.Initializable;
import parse.LogicFormulaParser;
import parse.Parser;
import parse.SimpleFormulaParser;

/**
 * 電卓ウィンドウを構築して表示するためのJFrameです。<br>
 * このクラスが使用するInputPanel等のクラスはこのクラスの準備が整う前にコンストラクタが実行されるため、<br>
 * それらのクラスはコンストラクタではなく、init()メソッドでこのクラスのメソッドにアクセスしてください。<br>
 * 対象のクラスには{@link Initializable}注釈が使用されています。
 */
final public class CalcWindowFrame extends JFrame {

	/**
	 * 電卓の動作モードを定義するクラスです。
	 */
	public static enum CalcMode {
		/**
		 * 通常通りの(掛け算割り算関係なく左から右に計算する)電卓です。
		 */
		Simple("通常電卓", SimpleFormulaParser.getParser()),
		
		/**
		 * 算数のルールに従って(掛け算や割り算割り算を先に)計算する電卓です。
		 */
		Logic("四則計算電卓", LogicFormulaParser.getParser());
		
		/**
		 * この電卓ウィンドウのタイトル
		 */
		final private String title;
		
		/**
		 * この電卓の計算時に使用するパーサ
		 */
		final private Parser parser;

		/**
		 * このクラスを初期化します
		 * @param title タイトル
		 * @param parser パーサ
		 */
		private CalcMode(String title, Parser parser) {
			this.title = title;
			this.parser = parser;
		}

		/**
		 * この電卓ウィンドウのタイトルを返します。
		 * @return title この電卓ウィンドウのタイトル
		 * @see #title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * この電卓の計算に使用するパーサを返します。
		 * @return parser この電卓の計算に使用するパーサを返します。
		 * @see #parser
		 */
		public Parser getParser() {
			return parser;
		}
	}
	
	/**
	 * 現在使用している電卓ウィンドウのインスタンスです。
	 */
	private static CalcWindowFrame window = null;
	
	/**
	 * このウィンドウに使用するレイアウトマネージャ
	 */
	final private static LayoutManager FRAME_LAYOUT = new BorderLayout(); 
	
	/**
	 * このウィンドウのウィンドウサイズ
	 */
	final private static Dimension WINDOW_SIZE = new Dimension(400, 500);
	
	/**
	 * 現在表示中のウィンドウがない場合にデフォルトで使用する電卓動作モード
	 */
	final private static CalcMode DEFAULT_CALC_MODE = CalcMode.Simple;
	
	/**
	 * このウィンドウに表示する入力パネル
	 */
	final private InputPanel INPUT_PANEL = new InputPanel(this);
	
	/**
	 * このウィンドウに表示するボタンパネル
	 */
	final private ButtonPanel BUTTON_PANEL = new ButtonPanel(this);
	
	/**
	 * このウィンドウの電卓の動作モード
	 */
	final private CalcMode CALC_MODE;
	
	/**
	 * 指定した計算モードのウィンドウを取得します。<br>
	 * もしその計算モードのウィンドウが作られていない場合、現在のウィンドウを破棄してから<br>
	 * 新しく指定した計算モードのウィンドウを作成します。<br>
	 * このメソッドはAWTのイベントディスパッチスレッド(EDT)で実行されていない場合、IllegalCallerExceptionをスローします。
	 * @param mode 計算モード
	 * @return 指定した計算モードの電卓ウィンドウ
	 * @throws IllegalCallerException このメソッドを呼び出したスレッドがEDTでない場合
	 */
	public static CalcWindowFrame getWindow(CalcMode mode) {
		checkEDT();
		if (window == null) {
			window = new CalcWindowFrame(mode, null);
		} else if (!mode.equals(window.getCalcMode())) {
			Point point = window.getLocation();
			window.dispose();
			window = new CalcWindowFrame(mode, point);
		}
		return window;
	}
	
	/**
	 * 現在表示中のウィンドウを取得します。<br>
	 * もし現在表示中のウィンドウがない場合は、デフォルト({@link #DEFAULT_CALC_MODE DEFAULT_CALC_MODE})を使用してウィンドウを作成します。<br>
	 * このメソッドはAWTのイベントディスパッチスレッド(EDT)で実行されていない場合、IllegalCallerExceptionをスローします。
	 * @return 現在表示中のウィンドウ
	 * @throws IllegalCallerException このメソッドを呼び出したスレッドがEDTでない場合
	 * @see #getWindow(CalcMode)
	 */
	static CalcWindowFrame getCurrentWindow() {
		checkEDT();
		if (window == null) {
			getWindow(DEFAULT_CALC_MODE);
		}
		return window;
	}
	
	/**
	 * 現在のスレッドがイベントディスパッチスレッド(EDT)かどうかを確認し、EDTでない場合はIllegalCallerExceptionをスローします。<br>
	 * このスレッドがEDTの場合は何もしません。
	 * @throws IllegalCallerException このメソッドを呼び出したスレッドがEDTでない場合
	 */
	private static void checkEDT() {
		if(!SwingUtilities.isEventDispatchThread()) {
			throw new IllegalCallerException("呼び出したスレッドはEDTではありません");
		}
	}

	/**
	 * 新しく電卓ウィンドウを作成し、表示します。
	 * @param calcMode 電卓動作モード
	 * @param point ウィンドウの位置。nullも可。
	 */
	private CalcWindowFrame(CalcMode calcMode, Point point) {
		super();
		this.setTitle(calcMode.getTitle());
		this.CALC_MODE = calcMode;
		this.setLayout(FRAME_LAYOUT);
		this.add(INPUT_PANEL, BorderLayout.NORTH);
		this.add(BUTTON_PANEL, BorderLayout.CENTER);
		this.setSize(WINDOW_SIZE);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		if (point != null) {
			this.setLocation(point);
		}
		this.initPanel();
		this.setVisible(true);
	}
	
	/**
	 * コンポーネントの準備を行います。<br>
	 * WindowFrameの準備が終わった後、表示の直前で呼び出されるため、<br>
	 * 各コンポーネントのinit()メソッド内では安全にWindowFrameのメソッドが呼び出せます。<br>
	 * WindowFrameにアクセスする必要のある処理はこのメソッド内で行ってください。
	 */
	private void initPanel() {
		BUTTON_PANEL.init();
		INPUT_PANEL.init();
	}

	/**
	 * このウィンドウの電卓の動作モードを返します。
	 * @return このウィンドウの電卓の動作モード
	 */
	public CalcMode getCalcMode() {
		return CALC_MODE;
	}

	/**
	 * このウィンドウに表示している入力パネルを返します。
	 * @return このウィンドウに表示している入力パネル
	 */
	public InputPanel getInputPanel() {
		return INPUT_PANEL;
	}

	/**
	 * このウィンドウに表示しているボタンパネルを返します。
	 * @return このウィンドウに表示しているボタンパネル
	 */
	public ButtonPanel getButtonPanel() {
		return BUTTON_PANEL;
	}

}
