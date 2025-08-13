package parse;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.DoubleBinaryOperator;

import calc.Calculator;
import item.CalcResult;
import item.FormulaItem;

/**
 * 計算式を解析して随時計算するための各種メソッドを実装しています。
 */
public abstract class AbstractParser implements Parser {
	
	/**
	 * クラスを初期化します
	 */
	public AbstractParser() {
	}

    /**
	 * このオブジェクトの文字列表現を返します
	 * @return このオブジェクトの文字列表現
	 */
    @Override
    public abstract String toString();

    /**
	 * 何を計算するかを指定するのに使用します
	 */
    protected enum ParseMode {

        /**
		 * 掛け算を計算します。
		 */
        MULTIPLY,

        /**
         * 割り算を計算します。
         */
        DIVIDE,

        /**
         * 足し算を計算します。
         */
        PLUS,

        /**
         * 引き算を計算します。
         */
        MINUS,
    	
        /**
         * 括弧を解釈し、括弧内の数式を計算します。
         * この定数を指定した場合は、他の定数を同時に使用してはいけません。
         */
    	PARENTHESES;
    }

	/**
	 * 計算式を解析して、計算機を使用して計算します。<br>
	 * modesには足し算引き算掛け算割り算の中から何を計算するかを指定します。<br>
	 * 指定されていないものは計算されず、無視されます。<br>
	 * ただし、括弧のみ、指定されていない場合に'('や')'を検知した場合、ParseExceptionをスローします。
	 * 
	 * @param item 計算式
	 * @param calc 使用する計算機
	 * @param modes 計算する物
	 * @return 計算された数式
	 * @throws ParseException 計算中にエラーが発生した場合
	 */
    protected FormulaItem parseAndCalc(FormulaItem item, Calculator calc, ParseMode... modes) throws ParseException {
        FormulaItem.Builder returnItem = new FormulaItem.Builder();
		StringReader reader = new StringReader(item.get());
		boolean parentheses = Arrays.asList(modes).contains(ParseMode.PARENTHESES);
		if (parentheses && (Arrays.asList(modes).size() != 1)) {
			throw new IllegalArgumentException("ParseMode... modesにParseMode.PARENTHESESを指定する場合、それ以外は指定できません。");
		}
		try {
			int read = reader.read();
			StringBuilder doubleBuilder = new StringBuilder();
			DoubleBinaryOperator calcOperator = null;
			double previusDouble = 0;
			boolean firstInput = true;
			boolean parenthesesClosed = false;
			LOOP: while (read != -1) {
				char text = (char) read;
                try {
                    if ((!(firstInput == true && (text == '+' || text == '-'))) && text != '.') {
                        Integer.parseInt(String.valueOf(text));
                    }
                    if (parenthesesClosed) {
						throw new IllegalArgumentException("括弧の終わりの後に数字が入力されました。");
					}
                    doubleBuilder.append(text);
                    firstInput = false;
                } catch (NumberFormatException e) {
                	parenthesesClosed = false;
                	firstInput = true;
					double inputNumber;
					try {
						inputNumber = Double.parseDouble(doubleBuilder.toString());
					} catch (NumberFormatException e1) {
						if ((parentheses) && (text == '(')) {
							inputNumber = 1;
						} else {
							throw new IllegalArgumentException("入力された計算式が不正です。");
						}
					}
					doubleBuilder = new StringBuilder();
					if (calcOperator != null) {
						double calcResult = calcOperator.applyAsDouble(previusDouble, inputNumber);
						inputNumber = calcResult;
                    }
					calcOperator = null;
					switch (text) {
						case '+':
							if (Arrays.asList(modes).contains(ParseMode.PLUS)) {
								previusDouble = inputNumber;
								calcOperator = calc::plus;
							} else {
								returnItem.add(BigDecimal.valueOf(inputNumber).toPlainString() + "+");
							}
                            break;

                        case '-':
							if (Arrays.asList(modes).contains(ParseMode.MINUS)) {
								previusDouble = inputNumber;
								calcOperator = calc::minus;
							} else {
								returnItem.add(BigDecimal.valueOf(inputNumber).toPlainString() + "-");
							}
                            break;
                        
                        case '*':
							if (Arrays.asList(modes).contains(ParseMode.MULTIPLY)) {
								previusDouble = inputNumber;
                            	calcOperator = calc::multiply;
							} else {
								returnItem.add(BigDecimal.valueOf(inputNumber).toPlainString() + "*");
							}
                            break;
                        
                        case '/':
							if (Arrays.asList(modes).contains(ParseMode.DIVIDE)) {
								previusDouble = inputNumber;
								calcOperator = calc::divide;
							} else {
								returnItem.add(BigDecimal.valueOf(inputNumber).toPlainString() + "/");
							}
                            break;
                            
                        case '(':
                        	if (parentheses) {
								double result = this.parseParenthesesAndCalc(reader, calc).get();
								parenthesesClosed = true;
								doubleBuilder.append(calc.multiply(result, inputNumber));
								firstInput = false;
								break;
							}
                        
                        case ')':
                        	if (parentheses) {
                        		throw new IllegalArgumentException("括弧の始まりが来る前に括弧の終わりが来ました");
                        	}

                        case '=':
							returnItem.add(BigDecimal.valueOf(inputNumber).toPlainString() + "=");
                            break LOOP;
                    
                        default:
                            throw new IllegalArgumentException("使用できない文字が含まれています");
                    }
				}
				read = reader.read();
			}
			return returnItem.toFormula();
		} catch (Exception e) {
            throw new ParseException("解析中にエラーが発生しました", e);
        }
    }
    
    /**
     * 括弧を解析し、計算します。
     * @param reader 現在解析処理に使用しているStringReader
     * @param calc 計算に使用する計算機クラス
     * @return 計算結果
     * @throws ParseException 解析中にエラーが発生した場合
     */
    private CalcResult parseParenthesesAndCalc(StringReader reader, Calculator calc) throws ParseException {
    	try {
			FormulaItem.Builder itemBuilder = new FormulaItem.Builder();
			int read = reader.read();
			long parenthesesCount = 1;
			while (read != -1) {
				char text = (char) read;
				switch (text) {
					case '(':
						parenthesesCount++;
					break;
					
					case ')':
						parenthesesCount--;
						if (parenthesesCount == 0) {
							FormulaItem item = itemBuilder.toFormula();
							return this.parseAndCalc(item, calc);
						}
					break;
				}
				itemBuilder.add(String.valueOf(text));
				read = reader.read();
			}
			throw new IllegalArgumentException("括弧の終わりが来る前に数式の終わりが来ました");
		} catch (Exception e) {
			if (e instanceof ParseException) {
				throw (ParseException)e;
			}
			throw new ParseException("解析中にエラーが発生しました", e);
		}
    }
    
    /* 予備のため残しています
    private FormulaItem parseParentheses(FormulaItem item, Calculator calc) throws ParseException {
    	FormulaItem.Builder returnItem = new FormulaItem.Builder();
		StringReader reader = new StringReader(item.get());
		try {
			int read = reader.read();
			FormulaItem.Builder parenthesesBuilder = new FormulaItem.Builder();
			int parentheses = 0;
			boolean firstInput = true;
			StringBuilder doubleBuilder = new StringBuilder();
			Double previousDouble = null;
			boolean previousParenthesesClosed = false;
			while (read != -1) {
				char text = (char) read;
				try {
                    if ((!(firstInput == true && (text == '+' || text == '-'))) && text != '.') {
                        Integer.parseInt(String.valueOf(text));
                    }
                    doubleBuilder.append(text);
                    firstInput = false;
                } catch (NumberFormatException e) {
                	firstInput = true;
                	Double inputNumber;
                	if ((doubleBuilder.length() == 0) || (doubleBuilder.toString().equals("+")) || (doubleBuilder.toString().equals("-"))) {
						inputNumber = null;
					} else {
						try {
							inputNumber = Double.parseDouble(doubleBuilder.toString());
						} catch (NumberFormatException e1) {
							throw new IllegalArgumentException("入力された計算式が不正です。");
						}
						if (previousParenthesesClosed) {
							throw new IllegalArgumentException("括弧の後に数字が入力されています。");
						}
					}
					doubleBuilder = new StringBuilder();
                	if (text == '(') {
    					parentheses++;
    					previousDouble = inputNumber;
                	} else if (inputNumber == null) {
                    	throw new IllegalArgumentException("入力された計算式が不正です。");
                    } else if (text == ')') {
                    	if (parentheses == 0) {
    						throw new IllegalArgumentException("括弧の始まりが来る前に括弧の終わりが来ました");
    					}
						parenthesesBuilder.add(String.valueOf(inputNumber.doubleValue()));
    					parentheses--;
    					if (parentheses == 0) {
    						double result = this.parseAndCalc(parenthesesBuilder.toFormula(), calc).get();
    						if (previousDouble != null) {
    							result =  calc.multiply(previousDouble, result);
							}
    						returnItem.add(String.valueOf(result));
    					}
    					previousParenthesesClosed = true;
    				} else if (text == '=') {
    					if (!(parentheses == 0)) {
    						throw new IllegalArgumentException("全ての括弧の終わりが来る前に計算式の終わりが来ました");
    					} else {
    						returnItem.add(String.valueOf(inputNumber.doubleValue()));
    						break;
    					}
    				} else {
    					if (parentheses > 0) {
    						parenthesesBuilder.add(String.valueOf(inputNumber.doubleValue()));
    						parenthesesBuilder.add(String.valueOf(text));
    					} else {
    						returnItem.add(String.valueOf(inputNumber.doubleValue()));
    						returnItem.add(String.valueOf(text));
    					}
    				}
                }
				read = reader.read();
				previousParenthesesClosed = false;
			}
			return returnItem.toFormula();
		} catch (Exception e) {
            throw new ParseException("解析中にエラーが発生しました", e);
        }
    }*/

}
