/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package totetsu1;

//import IO_LIB.TXT_OPE;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.Arrays;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
//import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 *
 * @author keita
 */
public class TotetsuPDFController {

    /**
     * @param args the command line arguments
     */
    public static final void main(String[] args)  {

        
//        System.out.println("総ページ数：" + document.getNumberOfPages());
//        System.out.println("Heightサイズ = " + rec.getHeight());
//        System.out.println("Witdhサイズ = " + rec.getWidth());
        Logger log = Logger.getLogger(TotetsuPDFController.class);
        DOMConfigurator.configure("log4j.xml");
        log.setLevel(Level.INFO);
        
         //取得する店舗一覧のページ
        File f = new File("TotetuPDF\\input");
        File[] fs = f.listFiles();
        XLS_OPE2 xls=new XLS_OPE2();
        
        try {
            xls.setInput("TotetuPDF\\マザーコイル台帳.xlsx", 0);
        } catch (Exception ex) {
            log.error(ex);
            System.exit(0);
        }
        
        int r=16;
        
        for (int j = 0; j < fs.length; j++) {
            if (fs[j].getPath().toLowerCase().contains(".pdf")) {

//                System.out.println("-------------------------------------------------------------");
//                    System.out.println(fs[j].getName());
                try {
                    //x1,y1,x2,y2(mm):(x1,y1)→(x2,y2)のboundingbox        
                    TotetsuPDFController tpc = new TotetsuPDFController(fs[j].getAbsolutePath());
                    String[][] val = tpc.getCoilInfo();
                    log.info(fs[j].getName());
//                System.out.println("CertificateNo = " + tpc.getKeiyakuNo());
                    String date = tpc.getDate();
                    String Kno = tpc.getKeiyakuNo();
                    for (int i = 0; i < val.length; i++) {
                        String[] v = val[i];
//                   寸法　幅　重量　コイルNo　σy　σt　のび　C　Si　Mn　P　S　備考
//                    System.out.println("v = " + Arrays.toString(v));
                        xls.setRow(r);
                        xls.writeString(2, date);
                        xls.writeString(3, Kno);
                        xls.writeString(4, v[0]);
                        xls.writeString(5, v[1]);
                        xls.writeString(6, "東鉄");
                        xls.writeString(7, v[2]);
                        xls.writeString(8, v[3]);
                        xls.writeString(9, "SS400");
                        xls.writeString(13, v[4]);
                        xls.writeString(14, v[5]);
                        xls.writeString(15, v[6]);
                        xls.writeString(16, v[7]);
                        xls.writeString(17, v[8]);
                        xls.writeString(18, v[9]);
                        xls.writeString(19, v[10]);
                        xls.writeString(20, v[11]);
                        xls.writeString(27, v[12]);
                        r++;

                    }

                    tpc.close();
                } catch (Exception ex) {
                    log.error(ex);
                    log.error("東鉄のPDFではありません。");
//                    System.exit(0);
                }

            }
        }
        xls.saveFile("TotetuPDF\\output\\東鉄マザーコイル台帳.xlsx");
//        



    }
    private PDDocument document;
    private PDRectangle rec;
    
    public TotetsuPDFController(String path) throws IOException {
        
        File file = new File(path);
            this.document = PDDocument.load(file);
            this.rec=document.getPage(0).getMediaBox();
    }
    
    public String Read(double x1,double y1,double x2,double y2) throws IOException{
        
            Rectangle2D area = new Rectangle2D.Double(
                    x1 / 210.0 * rec.getHeight(),
                    y1 / 297.0 * rec.getWidth(),
                    (x2 - x1) / 210.0 * rec.getHeight(),
                    (y2 - y1) / 297.0 * rec.getWidth()
            );

            
            int page = 1;
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            //抽出対象の範囲を指定する（名前は任意）
            stripper.addRegion("list", area);
            //抽出対象のページから範囲ごとにテキストを抽出する（getPageに渡すpageIndexは0〜）
            stripper.extractRegions(document.getPage(page - 1));
            //抽出結果を取得する
            String text = stripper.getTextForRegion("list");
//            if(!text.trim().isEmpty()) System.out.println(text.trim());
            
            return text.trim();
            
    }
    
    public String getDate() throws IOException{
        return this.Read(180, 27,208, 32).replace(".", "/").replace(" ", "").trim();
    }
    
    public String getKeiyakuNo() throws IOException{
        return this.Read(179, 23,205, 27).replace("-", "").trim();
    }
    
    public String[][] getCoilInfo() throws IOException{
        
        double[][] val={
            {10, 80,250, 86},//コイル1行目
            {10, 88,250, 94},//コイル2行目
            {10, 96,250,103},//コイル3行目
            {10,105,250,112},//コイル4行目
            {10,114,250,120},//コイル5行目
            {10,122,250,128},//コイル6行目
            {10,130,250,136},//コイル7行目
            {10,139,250,145},//コイル8行目
        };
        ArrayList<String> l0=new ArrayList();
        for (int i = 0; i < val.length; i++) {
            double[] v = val[i];
            String v0=this.Read(v[0], v[1], v[2], v[3]);
//            System.out.println("v0 = " + v0);
            if(v0.trim().toLowerCase().contains("total")){
                continue;
            }
            if(!v0.trim().isEmpty()){
                l0.add(v0);
            }
        };
        
        String[] pn0=(String[])l0.toArray(new String[l0.size()]);
        String[][] pn1=new String[pn0.length][];
        for (int i = 0; i < pn0.length; i++) {
            String cn = pn0[i];
            pn1[i]=cn.split(" ");
        }
        
        
        val=new double[][]{
            {10,182,150,186},//鋼番1行目
            {10,188,150,192},//鋼番2行目
            {10,192,150,195},//鋼番3行目
            {10,197,150,200},//鋼番4行目
            {10,199,150,203},//鋼番5行目
            {10,208,150,208},//鋼番6行目
        };
        
        ArrayList<String> l1=new ArrayList();
        for (int i = 0; i < val.length; i++) {
            double[] v = val[i];
            String v0=this.Read(v[0], v[1], v[2], v[3]);
            if(!v0.trim().isEmpty()){
                l1.add(v0);
            }
        };
        String[] cn0=(String[])l1.toArray(new String[l1.size()]);
        String[][] cn1=new String[cn0.length][];
        for (int i = 0; i < cn0.length; i++) {
            String cn = cn0[i];
            cn1[i]=cn.split(" ");
        }
//         [3.02X1,080XCOIL, 1, 16320, AC3210541, AC8589, 318, 426, 40, G]
//        寸法　幅　重量　コイルNo　σy　σt　のび　C　Si　Mn　P　S　備考

        String [][] cinfo=new String[pn1.length][13];
        for (int i = 0; i < cinfo.length; i++) {
            cinfo[i][0]=this.getThickness(pn1[i][0].split("X")[0].trim());//寸法
            cinfo[i][1]=pn1[i][0].split("X")[1].replace(",", "").trim();//寸法
            cinfo[i][2]=pn1[i][2];//重量
            cinfo[i][3]=pn1[i][3];//コイルNo
            cinfo[i][4]=pn1[i][5];//σy
            cinfo[i][5]=pn1[i][6];//σt
            cinfo[i][6]=pn1[i][7];//のび
            cinfo[i][12]=pn1[i][4];//備考

            for (int j = 0; j < cn1.length; j++) {
                if (cinfo[i][12].toLowerCase().equals(cn1[j][0].toLowerCase())) {
                    cinfo[i][7] = cn1[j][1];//C
                    cinfo[i][8] = cn1[j][2];//Si
                    cinfo[i][9] = cn1[j][3];//Mn
                    cinfo[i][10] = cn1[j][4];//P
                    cinfo[i][11] = cn1[j][5];//S
                }
            }
            
        }
        return  cinfo;
        
    }
    
    public String getThickness(String a){
        String t="0";
        String b=""+a.charAt(0);
//        System.out.println("b = " + b);
        if(b.contains("1")){
            t="1.6";
        }else if(b.contains("2")){
            t="2.3";
        }else if (b.contains("3")){
            t="3.2";
        }
        return t;
    }
    
    public void close() throws IOException{
        document.close();
    }
    
    
}
