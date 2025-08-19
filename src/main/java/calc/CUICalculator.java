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
	
	/**
	 * CUI電卓をスクリプト用に、最低限の出力にするか否かです。<br>
	 * スクリプトや他のプログラムで実行する際に、答え以外の出力を行いません。<br>
	 * 計算に成功した場合はSTDOUTに、計算結果のみ出力します。<br>
	 * 失敗した場合は、STDOUTに<code>"ERROR"</code>、STDERRにエラーの情報を出力します。<br>
	 * この機能はシステムプロパティ<code>calc.CUICalculator.scriptMode</code>が<code>true</code>である場合にのみ有効になります。
	 */
	private static boolean scriptMode = Boolean.getBoolean("calc.CUICalculator.scriptMode");

	@Override
	public String toString() {
		return "CUICalculator [parser=" + parser + "]";
	}

	@Override
	public void display() {
		Thread.setDefaultUncaughtExceptionHandler((th, e) -> {
			if (!scriptMode) {
				System.err.println("電卓実行中に致命的なエラーが発生したため終了します。");
			}
			Calculator.printStackTrace(e);
			if(exitVM) System.exit(1);
		});
		this.displayMessage();
		Inputer inputer = new CUIInputer();
		while (true) {
			if (!scriptMode) {
				System.out.print("> ");
			}
			String inputLine = inputer.getLine();
			switch (inputLine) {
				case "exit":
					if (!scriptMode) {
						System.out.println("電卓を終了します。");
					}
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
						if (!scriptMode) {
							System.err.println("計算中にエラーが発生しました: " + e.getCause().getLocalizedMessage());
						} else {
							System.out.println("ERROR");
							System.err.println(e.getCause().getLocalizedMessage());
						}
					}
					break;
			}
		}
	}

	/**
	 * メッセージをコンソールに出力します
	 */
	private void displayMessage() {
		if (!scriptMode) {
			System.out.println("数字(少数も含む)とこれらの記号+-*/を入力してEnterを押すと計算します。");
		}
		this.changeParser();
		if (!scriptMode) {
			System.out.println("\"exit\"と入力すると電卓プログラムを終了します。");
		}
	}

	/**
	 * 計算モードを変更します
	 */
	private void changeParser() {
		if (this.parser instanceof SimpleFormulaParser) {
			this.parser = LogicFormulaParser.getParser();
			if (!scriptMode) {
				System.out.println("現在四則計算モードで動作しています。(掛け算や割り算を先に計算します)");
			}
		} else {
			this.parser = SimpleFormulaParser.getParser();
			if (!scriptMode) {
				System.out.println("現在通常モードで動作しています。(掛け算や割り算に関係なく左から右に計算します)");
			}
		}
		if (!scriptMode) {
			System.out.println("モードを変更する場合は\"change-mode\"と入力してください。");
		}
	}

}
