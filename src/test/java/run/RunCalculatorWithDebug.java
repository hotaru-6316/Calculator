package run;

import java.time.Duration;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import calc.Calculator;
import item.CalcResult;
import item.FormulaItem;
import parse.LogicFormulaParser;
import parse.ParseException;
import parse.Parser;
import parse.SimpleFormulaParser;
import util.ReflectionUtility;

/**
 * 電卓アプリを開発途中の不安定な機能を有効にして起動します。
 */
public final class RunCalculatorWithDebug {

	/**
	 * 開発途中の不安定な機能を有効にしてCUICalculaterを起動します。
	 * @deprecated このメソッドでテストする機能は既に実装が完了したため、ここでテストする必要はありません。
	 */
	//@Test
	void runCUICalculator() {
		SimpleFormulaParser simpleFormulaParser = new SimpleFormulaParser() {

			@Override
			public CalcResult parseAndCalc(FormulaItem item, Calculator calc) throws ParseException {
				item = this.parseAndCalc(item, calc, ParseMode.PARENTHESES);
				return super.parseAndCalc(item, calc);
			}
			
		};
		LogicFormulaParser logicFormulaParser = new LogicFormulaParser() {

			@Override
			public CalcResult parseAndCalc(FormulaItem item, Calculator calc) throws ParseException {
				item = this.parseAndCalc(item, calc, ParseMode.PARENTHESES);
				return super.parseAndCalc(item, calc);
			}
			
		};
		SimpleFormulaParser oldSimpleFormulaParser = SimpleFormulaParser.getParser();
		LogicFormulaParser oldLogicFormulaParser = LogicFormulaParser.getParser();
		this.setParserUsingReflection(simpleFormulaParser);
		this.setParserUsingReflection(logicFormulaParser);
		JDialog dialog = new JDialog();
		dialog.setTitle("CUICalculatorを実行");
		JButton button = new JButton("<html>CUICalculatorを開発途中の不安定な機能を<br>有効にして起動するにはクリック</html>");
		Thread currentThread = Thread.currentThread();
		button.addActionListener((e) -> {
			dialog.dispose();
			currentThread.interrupt();
		});
		dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
		dialog.add(button);
		dialog.add(new JLabel("※このダイアログは5秒後に閉じます。"));
		dialog.pack();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		try {
			Thread.sleep(Duration.ofSeconds(5));
			SwingUtilities.invokeLater(() -> {
				dialog.dispose();
			});
		} catch (InterruptedException e1) {
			Calculator.getCUICalc().display();
		}
		this.setParserUsingReflection(oldSimpleFormulaParser);
		this.setParserUsingReflection(oldLogicFormulaParser);
	}
	
	private void setParserUsingReflection(Parser parser) {
		ReflectionUtility.setFieldValueClassLoop(parser.getClass(), null, "parser", parser);
	}

}
