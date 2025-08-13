package calc;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * System.out又はSystem.errへの出力を一時的に保持するためのクラスです。<br>
 * このクラスが開かれると、System.out又はSystem.errが上書きされ、開かれている間はコンソールへ出力されません。<br>
 * このクラスが閉じられると、System.out又はSystem.errが元に戻され、閉じた後は通常通りコンソールへ出力されます。
 */
class SystemOutErrWrapper extends PrintStream {
	
	/**
	 * 上書きする対象を指定するための列挙型です。
	 */
	enum WrapperEnum {
		
		/**
		 * System.outを表す型です。
		 */
		SYSTEM_OUT(() -> System.out, System::setOut),
		
		/**
		 * System.errを表す型です。
		 */
		SYSTEM_ERR(() -> System.err, System::setErr);
		
		/**
		 * 上書きする前のPrintStreamを取得するためのSupplierが入ります。
		 */
		final private Supplier<PrintStream> OLD;
		
		/**
		 * PrintStreamを上書きするためのConsumerが入ります。
		 */
		final private Consumer<PrintStream> NEW;
		
		/**
		 * この列挙型を初期化します
		 * @param oldPs 上書きする前のPrintStreamを取得するためのSupplier
		 * @param newPs PrintStreamを上書きするためのConsumer
		 */
		private WrapperEnum(Supplier<PrintStream> oldPs, Consumer<PrintStream> newPs) {
			OLD = oldPs;
			NEW = newPs;
		}

	}

	/**
	 * 上書きする前のPrintStreamが入ります。
	 */
	final private PrintStream OLD;
	
	/**
	 * 上書きする対象を指定したWrapperEnumが入ります。
	 */
	final private WrapperEnum TYPE;
	
	/**
	 * このStringBufferにprint()された全てが入ります。<br>
	 * このbufferは閉じられた後は使用できなくなります。
	 */
	private StringBuffer buffer = new StringBuffer();

	/**
	 * 指定されたPrintStreamを上書きし、StringBufferへ入れる準備をします
	 * @param type 上書きするPrintStreamを表す型
	 */
	SystemOutErrWrapper(WrapperEnum type) {
		super(OutputStream.nullOutputStream());
		TYPE = type;
		OLD = TYPE.OLD.get();
		TYPE.NEW.accept(this);
	}

	/**
	 * 出力をすべて保存しているStringBufferを返します。
	 * このメソッドで取得されたStringBufferに変更を書き込んでも内部のStringBufferを書き換えることは出来ません。
	 * @return 出力を保存しているStringBuffer
	 */
	StringBuffer getBuffer() {
		return new StringBuffer(getBuffer(null));
	}
	
	/**
	 * 出力をすべて保存しているStringBufferを返します。
	 * パッケージプライベートで公開されているメソッドと異なり、このメソッドで取得されたStringBufferに変更を書き込むと内部のStringBufferに書き込まれます。
	 * @param dummy {@link #getBuffer()}と区別するためのダミーパラメータです。動作に影響は与えないので、nullも可。
	 * @return 出力を保存しているStringBuffer
	 */
	private StringBuffer getBuffer(Object dummy) {
		if (buffer == null) {
			throw new IllegalStateException("ストリームが閉じられた後はStringBufferは使用できません。");
		}
		return buffer;
	}

	@Override
	public void print(boolean b) {
		getBuffer(null).append(b);
	}

	@Override
	public void print(char c) {
		getBuffer(null).append(c);
	}

	@Override
	public void print(int i) {
		getBuffer(null).append(i);
	}

	@Override
	public void print(long l) {
		getBuffer(null).append(l);
	}

	@Override
	public void print(float f) {
		getBuffer(null).append(f);
	}

	@Override
	public void print(double d) {
		getBuffer(null).append(d);
	}

	@Override
	public void print(char[] s) {
		getBuffer(null).append(s);
	}

	@Override
	public void print(String s) {
		getBuffer(null).append(s);
	}

	@Override
	public void print(Object obj) {
		getBuffer(null).append(obj);
	}

	@Override
	public void println() {
		getBuffer(null).append(System.lineSeparator());
	}

	@Override
	public void println(boolean x) {
		print(x);
		println();
	}

	@Override
	public void println(char x) {
		print(x);
		println();
	}

	@Override
	public void println(int x) {
		print(x);
		println();
	}

	@Override
	public void println(long x) {
		print(x);
		println();
	}

	@Override
	public void println(float x) {
		print(x);
		println();
	}

	@Override
	public void println(double x) {
		print(x);
		println();
	}

	@Override
	public void println(char[] x) {
		print(x);
		println();
	}

	@Override
	public void println(String x) {
		print(x);
		println();
	}

	@Override
	public void println(Object x) {
		print(x);
		println();
	}

	/**
	 * このストリームを閉じて、上書きしたPrintStreamを元に戻します。<br>
	 * このストリームを閉じた後でも、bufferは有効です。
	 */
	@Override
	public void close() {
		TYPE.NEW.accept(OLD);
		super.close();
	}

}
