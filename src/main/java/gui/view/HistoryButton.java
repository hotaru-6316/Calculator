package gui.view;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import history.HistoryDAO;
import item.History;

class HistoryButton extends JButton {
	
	private static final URL RES_ICON_URL = HistoryButton.class.getResource("/history_icon.png");
	
	private final CalcWindowFrame parentFrame;
	
	private final InputTextField inputTextField;
	
	public HistoryButton(CalcWindowFrame parentFrame, InputTextField inputTextField) {
		super(new ImageIcon(RES_ICON_URL));
		addActionListener(this::onClick);
		this.parentFrame = parentFrame;
		this.inputTextField = inputTextField;
	}
	
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
