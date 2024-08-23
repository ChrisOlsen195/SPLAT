/**************************************************
 *               Cell_Information                 *
 *                    10/15/23                    *
 *                     09:00                      *
 *************************************************/
package dataObjects;

public class Cell_Information {
    int row, col;
    String contents;
    
    public Cell_Information() { row = 0; col = 0; contents = ""; }
    
    // The -1's are so that a col or row can be individually (re)set
    public void setColAndRow(int toThisCol, int toThisRow) {
            col = toThisCol;
            row = toThisRow;
    }
    
    public void setCol_Row_Contents(int toThisCol, int toThisRow, String toThisContent) {
            col = toThisCol;
            row = toThisRow;
            contents = toThisContent;
    }
    
    public Cell_Information getRowAndCol() {return this; }
    
    public int getRow() { return this.row; }
    public void setRow(int toThisRow) { row = toThisRow; }
    
    public int getCol() { return this.col; }
    public void setCol(int toThisCol) { col = toThisCol; }
    
    public String getContents() { return contents; }
    public void setContents(String toThisContent) { contents = toThisContent; }
    
    public String toString() {
        String cpiString = "col = " + String.valueOf(col) 
                          + "; row = " + String.valueOf(row)
                          + "; contents = " + contents;
        System.out.println(cpiString);
        return cpiString;
    }  
}
