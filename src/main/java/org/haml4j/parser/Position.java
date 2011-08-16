package org.haml4j.parser;

/**
 * Position of the parsing cursor inside the HAML template. This is used for error reporting
 * This class is immutable
 * @author icoloma
 *
 */
public class Position {

	/** row index, starting at 1 */
	private int row;
	
	/** column index, starting at 1 */
	private int col;

	/** position inside the input String, starting at 0 */
	private int inputOffset;
	
	public Position() {
		row = 1;
		col = 1;
		inputOffset = 0;
	}
	
	public Position(int row, int col, int inputOffset) {
		this.row = row;
		this.col = col;
		this.inputOffset = inputOffset;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public int getInputOffset() {
		return inputOffset;
	}

	/**
	 * Creates a {@link Position} at the same line of this one, applying the provided input offset
	 */
	public Position positionAtSameLine(int inputOffset) {
		return new Position(getRow(), getCol() + (inputOffset - getInputOffset()), getInputOffset());
	}
	
	public void incColumn(int columnOffset) {
		col += columnOffset;
	}
	
}
