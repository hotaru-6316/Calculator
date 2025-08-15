package gui.view;

import static org.junit.jupiter.api.Assertions.*;
import static util.ReflectionUtility.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import history.HistoryDAO;
import item.CalcResult;
import item.FormulaItem;
import item.History;
import parse.SimpleFormulaParser;
import util.ReflectionUtility;

class HistoryButtonTest {
	
	@BeforeAll
	static void resetState() {
		setFieldValue(HistoryDAO.class, null, "dbDir", "./target/");
		((CalcWindowFrame) ReflectionUtility.getFieldValue(CalcWindowFrame.class, null, "window")).dispose();
		ReflectionUtility.setFieldValue(CalcWindowFrame.class, null, "window", null);
	}

	@Test
	void testClick() {
		AtomicReference<Throwable> th = new AtomicReference<>();
		AtomicBoolean finished = new AtomicBoolean(false);
		Object wait = new Object();
		SwingUtilities.invokeLater(() -> {
			CalcWindowFrame frame = CalcWindowFrame.getCurrentWindow();
			try {
				InputPanel panel = frame.getInputPanel();
				HistoryButton button = (HistoryButton) ReflectionUtility.getFieldValue(InputPanel.class, panel, "HISTORY_BUTTON");
				deleteDB();
				button.doClick();
				createDB();
				HistoryDAO.saveHistory(new History(-1, new FormulaItem("12+8+5"), new CalcResult(25), SimpleFormulaParser.getParser()));
				JOptionPane.showMessageDialog(frame, "履歴エントリを必ず選択してください。");
				button.doClick();
				assertEquals(new FormulaItem("12+8+5").get(), panel.getTextLabel().getText());
				assertEquals(String.valueOf(new CalcResult(25).get()), panel.getTextField().getText());
				panel.getTextLabel().setText(" ");
				panel.getTextField().setText("");
				JOptionPane.showMessageDialog(frame, "「取消」を押してください。");
				button.doClick();
				assertEquals(" ", panel.getTextLabel().getText());
				assertEquals("", panel.getTextField().getText());
			} catch (Throwable e) {
				th.set(e);
			} finally {
				frame.dispose();
				ReflectionUtility.setFieldValue(CalcWindowFrame.class, null, "window", null);
				finished.set(true);
				synchronized (wait) {
					wait.notifyAll();
				}
			}
		});
		synchronized (wait) {
			try {
				wait.wait();
			} catch (InterruptedException e) {}
		}
		if (th.get() != null) {
			if (th.get() instanceof RuntimeException e) {
				throw e;
			} else if (th.get() instanceof Error e) {
				throw e;
			} else {
				throw new RuntimeException(th.get());
			}
		}
	}

	/**
	 * HistoryDAOを使用して、DBファイルを作成します。
	 */
	private static void createDB() {
		try {
			HistoryDAO.getHistories();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * DBファイルを削除します。
	 */
	private static void deleteDB() {
		try {
			Files.deleteIfExists(Path.of((String) getFieldValue(HistoryDAO.class, null, "dbDir"), ((String) getFieldValue(HistoryDAO.class, null, "TABLENAME")) + ".mv.db"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
