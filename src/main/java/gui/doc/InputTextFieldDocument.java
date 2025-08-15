package gui.doc;

import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import calc.Calculator;
import gui.view.InputPanel;
import history.HistoryDAO;
import item.CalcResult;
import item.FormulaItem;
import item.History;
import parse.ParseException;
import parse.Parser;

/**
 * 入力フィールドに数式で使用できない文字が入らないようフィルタリングしたり、<br>
 * 「=」が入ると自動で計算を行うクラスです。
 */
public final class InputTextFieldDocument extends PlainDocument {

	/**
	 * 入力フィールドを表示しているInputPanel
	 */
	private final InputPanel PANEL;
	
	/**
	 * 計算が行われてから最初の入力であるかどうか。<br>
	 * trueになると数字入力時や文字削除時に入力フィールドとラベルを空にします。
	 */
	private boolean firstInput = false;
	
	/**
	 * フィルタ処理をスキップするかどうか。<br>
	 * 主に「エラー」を表示するときに使います。
	 */
	private boolean skipCheck = false;
	
	/**
	 * フィルタ処理が実行中かどうか。<br>
	 * 主にフィルタ処理実行中に(setText()等で)再度フィルタ処理が動くときに無限ループを防ぐために使います。
	 */
	private boolean running = false;
	
	/**
	 * クラスを初期化します。
	 * @param panel フィルタ処理を行うテキストフィールドを表示しているInputPanel
	 */
	public InputTextFieldDocument(InputPanel panel) {
		this.PANEL = panel;
	}

	/**
	 * フィルタ処理を行った上でテキストを指定位置に挿入します。<br>
	 * 条件に合わない文字が含まれている場合、それらは除外されて、条件に合った文字のみが挿入されます。<br>
	 * 例："1+9o-1"と入力すると"1+9-1"のみが挿入される。
	 */
	@Override
	public void insertString(int offs, String str, AttributeSet a) 
			throws BadLocationException {
		if (skipCheck) {
			super.insertString(offs, str, a);
			return;
		}
		boolean beforeRunning = false;
		if (running) {
			beforeRunning = true;
		}
		running = true;
		if ((str == null) || (str.length() == 0) || (!(offs >= 0))) {
			return;
		}
		StringReader newReader = new StringReader(str);
		StringBuilder newBuilder = new StringBuilder();
		boolean calc = false;
		boolean first = false;
		try {
			int read = newReader.read();
			boolean firstRead = true;
			while (read != -1) {
				boolean add = false;
				char input = (char) read;
				try {
					Integer.parseInt(String.valueOf(input));
					if(firstRead && this.firstInput) {
						first = true;
					}
					add = true;
				} catch (NumberFormatException e) {
					switch (input) {
					case '+':
					case '-':
					case '*':
					case '/':
					case '.':
					case '(':
					case ')':
						add = true;
						break;
						
					case '=':
						add = true;
						calc = true;
						break;

					default:
						break;
					}
				}
				if (add) {
					newBuilder.append(input);
				}
				firstRead = false;
				if (calc == true) {
					break;
				}
				read = newReader.read(); 
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "エラーが発生しました。", "エラー", JOptionPane.ERROR_MESSAGE);
			Calculator.printStackTrace(e);
			return;
		}
		super.insertString(offs, newBuilder.toString(), a);
		if (this.firstInput) {
			this.PANEL.getTextLabel().setText(" ");
		}
		this.firstInput = false;
		if (first) {
			this.PANEL.getTextField().setText(newBuilder.toString());
		}
		if ((!beforeRunning) && calc) {
			this.calcStart();
		}
		running = false;
	}

	@Override
	public void remove(int offs, int len) throws BadLocationException {
		boolean beforeRunning = false;
		if (running) {
			beforeRunning = true;
		}
		running = true;
		super.remove(offs, len);
		if ((!beforeRunning) && this.firstInput) {
			this.PANEL.getTextLabel().setText(" ");
			this.PANEL.getTextField().setText("");
			this.firstInput = false;
		}
		running = false;
	}

	/**
	 * 入力されたテキストの内容で計算を開始し、結果を入力フィールドに書き込みます。<br>
	 * (0除算や不正な計算式等で)エラーが発生した場合、入力フィールドに「エラー」と書き込みます。<br>
	 * エラーであるかどうかに関わらず入力フィールドに入力されていたテキストはラベルに移動します。
	 */
	private void calcStart() {
		String formulaString = PANEL.getTextField().getText();
		if (formulaString.equals("=")) {
			PANEL.getTextField().setText("");
			return;
		}
		PANEL.getTextLabel().setText(PANEL.getTextField().getText());
		PANEL.getTextField().setText("");
		FormulaItem item = new FormulaItem(formulaString);
		Parser parser = PANEL.getWindowFrame().getCalcMode().getParser();
		Calculator calculator = Calculator.getGUICalc();
		try {
			CalcResult result = parser.parseAndCalc(item, calculator);
			PANEL.getTextField().setText(BigDecimal.valueOf(result.get()).toPlainString());
			try {
				HistoryDAO.saveHistory(new History(-1, item, result, parser));
			} catch (SQLException e) {
				PANEL.getTextLabel().setText("(履歴に保存できませんでした) " + PANEL.getTextLabel().getText());
				e.printStackTrace();
			}
		} catch (ParseException e) {
			this.skipCheck = true;
			PANEL.getTextField().setText("エラー!");
			this.skipCheck = false;
			Calculator.printStackTrace(e);
		}
		this.firstInput = true;
	}
	
	/**
	 * 計算が行われてから最初の入力であるかどうかを返します。詳細はfirstInputを確認してください。
	 * @return 計算が行われてから最初の入力であるかどうか(firstInputの値)
	 * @see #firstInput
	 */
	public boolean isFirstInput() {
		return this.firstInput;
	}

}
