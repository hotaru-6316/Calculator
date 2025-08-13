package calc;

import input.CUIInputer;
import input.Inputer;
import item.CalcResult;
import item.FormulaItem;
import parse.LogicFormulaParser;
import parse.ParseException;
import parse.Parser;
import parse.SimpleFormulaParser;

/**
 * CUIで動作する計算機クラスです。
 */
final class CUICalculator implements Calculator {
	
	/**
	 * クラスを初期化します
	 */
	public CUICalculator() {
	}

	/**
	 * 解析に使用するパーサクラスを格納します
	 */
	protected Parser parser;
	
	/**
	 * 捕捉されない例外が発生したときにJVMを終了するかどうか。<br>
	 * JUnitテスト実行中にJVMが停止しないようにするために使います。
	 */
	private static boolean exitVM = true;

	@Override
	public String toString() {
		return "CUICalculator [parser=" + parser + "]";
	}

	@Override
	public void display() {
		Thread.setDefaultUncaughtExceptionHandler((th, e) -> {
			System.err.println("電卓実行中に致命的なエラーが発生したため終了します。");
			Calculator.printStackTrace(e);
			if(exitVM) System.exit(1);
		});
		this.displayMessage();
		Inputer inputer = new CUIInputer();
		while (true) {
			System.out.print("> ");
			String inputLine = inputer.getLine();
			switch (inputLine) {
				case "exit":
					System.out.println("電卓を終了します。");
					return;

				case "change-mode":
					this.changeParser();
					break;
					
				case "":
					break;
			
				default:
					FormulaItem item = new FormulaItem(inputLine);
					try {
						CalcResult result = this.parser.parseAndCalc(item, this);
						System.out.println(result.get());
					} catch (ParseException e) {
						System.err.println("計算中にエラーが発生しました: " + e.getCause().getLocalizedMessage());
					}
					break;
			}
		}
	}

	/**
	 * メッセージをコンソールに出力します
	 */
	private void displayMessage() {
		System.out.println("数字(少数も含む)とこれらの記号+-*/を入力してEnterを押すと計算します。");
		this.changeParser();
		System.out.println("\"exit\"と入力すると電卓プログラムを終了します。");
	}

	/**
	 * 計算モードを変更します
	 */
	private void changeParser() {
		if (this.parser instanceof SimpleFormulaParser) {
			this.parser = LogicFormulaParser.getParser();
			System.out.println("現在四則計算モードで動作しています。(掛け算や割り算を先に計算します)");
		} else {
			this.parser = SimpleFormulaParser.getParser();
			System.out.println("現在通常モードで動作しています。(掛け算や割り算に関係なく左から右に計算します)");
		}
		System.out.println("モードを変更する場合は\"change-mode\"と入力してください。");
	}

}
