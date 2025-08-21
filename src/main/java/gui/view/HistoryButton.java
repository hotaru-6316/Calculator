package gui.view;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import gui.doc.InputTextFieldDocument;
import history.HistoryDAO;
import item.History;

/**
 * 履歴機能へアクセスするためのボタンとそのUIを定義します。
 */
class HistoryButton extends JButton {
	
	/**
	 * リソースフォルダにあるボタンのアイコンに使用する画像ファイルへのURL
	 */
	private static final URL RES_ICON_URL = HistoryButton.class.getResource("/history_icon.png");
	
	/**
	 * このボタンを表示する親フレーム
	 */
	private final CalcWindowFrame parentFrame;
	
	/**
	 * 履歴から読み出した数式データを書き込むテキストフィールド
	 */
	private final InputTextField inputTextField;
	
	/**
	 * ボタンを作成し、セットアップします。
	 * @param parentFrame このボタンを表示している親フレーム
	 * @param inputTextField 履歴から読み出した数式データを書き込むテキストフィールド
	 */
	public HistoryButton(CalcWindowFrame parentFrame, InputTextField inputTextField) {
		super(new ImageIcon(RES_ICON_URL));
		addActionListener(this::onClick);
		this.parentFrame = parentFrame;
		this.inputTextField = inputTextField;
	}
	
	/**
	 * 履歴を取得し、全てのデータを表示し、選択されたデータをテキストフィールドに書き込みます。このデータの計算はテキストフィールド側({@link InputTextFieldDocument})で判定しています。
	 * @param e アクションイベント(使用しません)。この引数は、このメソッドをアクションリスナーとしてラムダ式で登録するために追加されています。
	 */
	private void onClick(ActionEvent e) {
		try {
			History[] histories = HistoryDAO.getHistories();
			if (histories.length == 0) {
				JOptionPane.showMessageDialog(parentFrame, "履歴はありません。", "履歴なし", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			History history = (History) JOptionPane.showInputDialog(parentFrame, "入力欄に再入力する計算式を選択してください。", "履歴表示・再入力選択", JOptionPane.QUESTION_MESSAGE, getIcon(), histories, null);
			if (history == null) {
				return;
			}
			inputTextField.setText(history.formula().get());
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(parentFrame, "履歴情報の取得に失敗しました。", "エラー", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}

}
