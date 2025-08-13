package run;

import calc.Calculator;

/**
 * Javaで実行された時の初期化処理にのみ使うクラスです
 */
class Main {
	
	/**
	 * このクラスはインスタンス化できません
	 * @throws UnsupportedOperationException 常に
	 */
	private Main() {
		throw new UnsupportedOperationException(this.getClass().getName() + "はインスタンス化できません");
	}

	/**
	 * Javaで実行されたときに電卓を開始するためのメソッドです。
	 * 引数に"nogui"を渡すとCUIモードで開始します。
	 * @param args 引数
	 */
	public static void main(String[] args) {
		// 動作をGUIにするか決めます
		boolean gui = true;
		if((args.length != 0) && (args[0].equals("nogui"))) {
			gui = false;
		}
		
		// 電卓を表示します
		Calculator calc = null;
		if(gui) {
			calc = Calculator.getGUICalc();
		} else {
			calc = Calculator.getCUICalc();
		}
		calc.display();
		System.exit(0);
	}

}
