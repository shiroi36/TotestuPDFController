/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * ここはPOIをつかってエクセルファイルを入出力するクラスです
 */

package totetsu1;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class XLS_OPE2 implements Closeable{
///このクラスを入れる時にはjacarta-POIをライブラリに入れておくこと
/////////////////////////////インプットに関するメソッド///////////////////////////
    
    XSSFWorkbook wb;
    XSSFSheet sheetin;
    XSSFRow rowout;
    XSSFCell cellout;

    /**
     * 入力するファイルを指定するメソッドです。
     *
     * @param path xlsファイルがあるパスをストリング型で表します
     * @param pagenum 指定したファイルのシートナンバー(はじめは0～)です。
     * @throws java.io.IOException
     */
    public void setInput(String path, int pagenum) throws IOException {
        wb = new XSSFWorkbook(path);
        sheetin = wb.getSheetAt(pagenum);
    }

    
    /**
     *書き込むセルの行(縦)を指定するメソッドです
     * @param i 書き込むセルの行(縦)カウントは上から0から
     */
    public void setRow(int i){
        rowout = sheetin.createRow(i);
//        rowout.setHeightInPoints(20);
    }
    
    /**
     * 最終行の行番号を返します。最初の行は0番です。
     *
     * @return 最終行の番号
     */
    public int getLastRowNum() {
        return sheetin.getLastRowNum();
    }
    public void writeString(int col,String s){
        cellout = rowout.createCell(col);
        try {
            cellout.setCellValue(Double.parseDouble(s));
        } catch (Exception e) {
            cellout.setCellValue(s);
        }
    }
    
    public void writeDoubleSeparate(int col,double s){
        cellout = rowout.createCell(col);
        CellStyle cs = wb.createCellStyle();    
        DataFormat fm = wb.createDataFormat();
        cs.setDataFormat(fm.getFormat("#,##0"));  //数値カンマ編集
        cellout.setCellStyle(cs);
        cellout.setCellValue(s);
        
    }

    /**
     * 指定された行の最後の列番号を返します。最初の列は0です。
     *
     * @param rowId 行番号
     * @return 指定された行の最後の列番号
     */
    public int getLastCellNum(int rowId) {
        // Apache POIのgetLastCellNumは1-basedな番号を返すので、1引いてから返す
        return sheetin.getRow(rowId).getLastCellNum() - 1;
    }

    /**
     * ある行に数列があった場合に、その数列が入っているセルの数を計算するメソッドです。
     *
     * @param iniRow 数列が始まる行（縦）を指定する
     * @param iniShort 数列が始まるセルの列（横）を指定する
     */
    public int getRowNum(int iniRow, int iniShort) {
        int num = 0;
        while (num >= 0) {
//            System.out.println("num = " + num);
            XSSFRow row = sheetin.getRow(num + iniRow);
            if(row==null)break;
            XSSFCell cell = row.getCell((short) iniShort);
            CellType ct = cell.getCellTypeEnum();
            String val;
            if (ct == CellType.NUMERIC) {
                val=""+cell.getNumericCellValue();
            } else {
                val=cell.getStringCellValue();
            }

            if (val.trim().isBlank()) {
                break;
            }
            num++;
//            System.out.println(num);
        }
        return num;
    }

    /**
     * ある行に数列があった場合に、その数列が入っているセルの数を計算するメソッドです。
     *
     * @param iniRow 数列が始まる行（縦）を指定する
     * @param iniShort 数列が始まるセルの列（横）を指定する
     */
    public double[][] getMatrix(int iniRow, int iniShort) {
        int rnum = 0;
        int snum = 0;
        ArrayList<Double> s = new ArrayList<Double>();
//        ArrayList<double[]> r=new ArrayList<double[]>();
        ArrayList<ArrayList<Double>> r = new ArrayList<ArrayList<Double>>();
        boolean flag = true;
        int pres = 0;
        while (flag) {
            XSSFRow row = sheetin.getRow(rnum + iniRow);
            try {
                while (true) {
                    XSSFCell cell = row.getCell((short) (iniShort + snum));
                    s.add(cell.getNumericCellValue());
                    snum++;
                }
            } catch (Exception e) {
                System.out.println(s.size());
                if (rnum != 0 && s.size() == pres) {
                    flag = false;
                } else {
                    pres = s.size();
                    r.add(s);
                    s = new ArrayList<Double>();
                    snum = 0;
                }
            }
            rnum++;
            //            System.out.println(num);
        }
        System.out.println(r.size());
        double[][] r2 = new double[r.get(0).size()][r.size()];
        for (int i = 0; i < r.size(); i++) {
            for (int t = 0; t < r2.length; t++) {
                r2[t][i] = r.get(i).get(t);
            }
        }
        return r2;
    }

    /**
     * 特定の行(row)列(short)のセルから始まる数列の数値を取得するメソッドです。
     *
     * @param Row 数値配列の最初の値があるセルの行(縦)セルのカウントは上から、0から
     * @param Short 数値配列の最初の値があるセルの列(横)セルのカウントは上から、0から
     * @param imputnum　その数値配列を読み込む長さを表す数
     */
    public double[] getSequenceDouble(int Row, int Short, int inputnum) {
        double N[] = new double[inputnum];
        XSSFRow row;
        XSSFCell cell;
        for (int i = 0; i < inputnum; i++) {
            row = sheetin.getRow(i + Row);
//            System.out.println(i);
            cell = row.getCell((short) Short);
//            System.out.println(cell.toString());
            N[i] = cell.getNumericCellValue();
        }
        return N;
    }

    /**
     * 特定の行(row)列(short)のセルから始まる数列の数値を取得するメソッドです。
     *
     * @param Row 数値配列の最初の値があるセルの行(縦)セルのカウントは上から、0から
     * @param Short 数値配列の最初の値があるセルの列(横)セルのカウントは上から、0から
     * @param imputnum　その数値配列を読み込む長さを表す数
     */
    public int[] getSequenceInt(int Row, int Short, int inputnum) {
        int N[] = new int[inputnum];
        XSSFRow row;
        XSSFCell cell;
        for (int i = 0; i < inputnum; i++) {
            row = sheetin.getRow(i + Row);
            cell = row.getCell((short) Short);
            N[i] = (int) cell.getNumericCellValue();
        }
        return N;
    }

    /**
     * 特定の行(row)列(short)のセルから始まる数列の数値を取得するメソッドです。
     *
     * @param Row 数値配列の最初の値があるセルの行(縦)セルのカウントは上から、0から
     * @param Short 数値配列の最初の値があるセルの列(横)セルのカウントは上から、0から
     * @param imputnum　その数値配列を読み込む長さを表す数
     */
    public String[] getSequenceString(int Row, int Short, int inputnum) {
        String N[] = new String[inputnum];
        XSSFRow row;
        XSSFCell cell;
        for (int i = 0; i < inputnum; i++) {
//            row = sheetin.getRow(i + Row);
//            cell = row.getCell((short) Short);
            try {
                N[i] = this.getString(i+Row, Short);
            } catch (NullPointerException e) {
                N[i] = "";
            }
//            System.out.println(i);
        }
        return N;
    }

    /**
     * ある特定のセルをstring型で取得するメソッド
     *
     * @param row 数値配列の最初の値があるセルの行(縦)セルのカウントは上から、0から
     * @param col 数値配列の最初の値があるセルの列(横)セルのカウントは上から、0から
     * @return
     */
    public String getString(int row, int col) {
        XSSFRow xssfRow = sheetin.getRow(row);
        XSSFCell cell = xssfRow.getCell((short) col);

        if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
//            throw new NullPointerException("セル (" + (row + 1) + ", " + (col + 1) + ") が空欄です。");
            return "";
        }

        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            return String.format("%.2f", cell.getNumericCellValue());
        }

        if (cell.getCellTypeEnum() == CellType.FORMULA) {
            return String.format("%.2f", cell.getNumericCellValue());
        }

        XSSFRichTextString xrts = cell.getRichStringCellValue();

        String address = new CellReference(cell).formatAsString();
        if (xrts == null) {
            throw new NullPointerException("セル\"" + address + "\"が空欄です。");
        }

        String value = xrts.getString();

        if (value.isEmpty()) {
            throw new NullPointerException("セル\"" + address + "\"が空欄です。");
        }

        return value;
    }

    public int getNumberOfSheets(String path) {
        int num = 0;

        InputStream is = null;
        Workbook wb = null;

        try {
            //Excelファイル読込
            is = new FileInputStream(path);
            wb = WorkbookFactory.create(is);

            //シート数を取得して表示する
            num = wb.getNumberOfSheets();
//            System.out.println("シート数：" + a);
//            return a;
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            try {
                wb.close();
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }

        return num;
    }
    
    
    public double getDouble(int row, int col) {
        XSSFRow xssfRow = sheetin.getRow(row);
        XSSFCell cell = xssfRow.getCell((short) col);

        if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
            throw new NullPointerException("セル (" + (row + 1) + ", " + (col + 1) + ") が空欄です。");
        }

        String address = new CellReference(cell).formatAsString();

        if (cell.getCellTypeEnum() == CellType.BOOLEAN || cell.getCellTypeEnum() == CellType.ERROR) {
            throw new NumberFormatException("セル\"" + address + "\"に数値が入力されていません。");
        }

        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }

        if (cell.getCellTypeEnum() == CellType.STRING) {
            String value = cell.getStringCellValue();
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException ex) {
                throw new NumberFormatException("セル\"" + address + "\"に数値が入力されていません。");
            }
        }

        if (cell.getCellTypeEnum() == CellType.FORMULA) {
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
            if (evaluator.evaluateFormulaCellEnum(cell) == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            }
            if (evaluator.evaluateFormulaCellEnum(cell) == CellType.STRING) {
                String value = cell.getStringCellValue();
                try {
                    return Double.parseDouble(value);
                } catch (NumberFormatException ex) {
                    throw new NumberFormatException("セル\"" + address + "\"に数値が入力されていません。");
                }
            }
        }

        throw new IllegalArgumentException("セル\"" + address + "\"に数値が入力されていません。");
    }

    public Date getDate(int row, int col) {
        XSSFRow xssfRow = sheetin.getRow(row);
        XSSFCell cell = xssfRow.getCell(col);
        if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
            throw new NullPointerException("セル (" + (row + 1) + ", " + (col + 1) + ") が空欄です。");
        }
        if (cell.getCellTypeEnum() != CellType.NUMERIC) {
            String address = new CellReference(cell).formatAsString();
            throw new IllegalArgumentException("セル\"" + address + "\"に数値が入力されていません。");
        }
        return new Date(cell.getDateCellValue().getTime());
    }

    public boolean isEmpty(int row, int col) {
        XSSFRow xssfRow = sheetin.getRow(row);

        if (xssfRow == null) {
            return true;
        }

        XSSFCell cell = xssfRow.getCell((short) col);

        if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
            return true;
        }

        if (cell.getCellTypeEnum() != CellType.STRING) {
            return false;
        }

        XSSFRichTextString xrts = cell.getRichStringCellValue();

        if (xrts == null) {
            return true;
        }

        return xrts.getString().isEmpty();
    }

    @Override
    public void close() throws IOException {
        if (wb != null) {
            wb.close();
            
        }
    }
    
    public void saveFile(String pass){
        FileOutputStream out = null;
            try{
              out = new FileOutputStream(pass);
              wb.write(out);
            }catch(IOException e){
              System.out.println(e.toString());
            }finally{
              try {
                out.close();
              }catch(IOException e){
                System.out.println(e.toString());
              }
            }
    }
    
    
}
