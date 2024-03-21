/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package totetsuH;

//import IO_LIB.TXT_OPE;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import java.util.Arrays;
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
//import org.apache.log4j.xml.DOMConfigurator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
//import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import totetsu1.XLS_OPE2;

/**
 *
 * @author keita
 */
public class TotetsuHsectionController {

    static Logger log = LogManager.getLogger(TotetsuHsectionController.class);

    /**
     * @param args the command line arguments
     */
    public static final void main(String[] args) {

//        System.out.println("総ページ数：" + document.getNumberOfPages());
//        System.out.println("Heightサイズ = " + rec.getHeight());
//        System.out.println("Witdhサイズ = " + rec.getWidth());
//        DOMConfigurator.configure("log4j.xml");
//        log.setLevel(Level.INFO);
        //取得する店舗一覧のページ
        File f = new File("TotetuH\\input");
        File[] fs = f.listFiles();
        FileWriter fw = null;
        PrintWriter pw = null;

        try {
            // 出力ファイルの作成
            fw = new FileWriter("TotetuH\\output\\csvdata.csv", false);
            // PrintWriterクラスのオブジェクトを生成
            pw = new PrintWriter(new BufferedWriter(fw));
        } catch (Exception ex) {
            log.error(ex);
            System.exit(0);
        }
        pw.print("date of issue,contract no,size,length,bundles,pieces,total pieces,weight,charge no,"
                + "yield point,tensile strength,yield ratio,elongation,"
                + "bending test,"
                + "impact test1,impact test2,impact test3,impact testAVE,"
                + "through-thickness characteristics1,"
                + "through-thickness characteristics2,"
                + "through-thickness characteristics3,"
                + "through-thickness characteristicsAVE,"
                + "ut,"
                + "gaikan,"
                + "charge no,"
                + "C,"
                + "SI,"
                + "Mn,"
                + "P,"
                + "S,"
                + "CEQ,"
        );
        pw.println();

        for (int j = 0; j < fs.length; j++) {
            if (fs[j].getPath().toLowerCase().contains(".pdf")) {

                log.info("-------------------------------------------------------------");
//                    System.out.println(fs[j].getName());
                try {
                    //x1,y1,x2,y2(mm):(x1,y1)→(x2,y2)のboundingbox        
                    TotetsuHsectionController tpc = new TotetsuHsectionController(fs[j].getAbsolutePath());
                    log.info(fs[j].getName());
                    log.info("発行日：" + tpc.getDate() + "＿受注番号：" + tpc.getKeiyakuNo());
                    String[][] val = tpc.getCoilInfo();
                    String[][] cno = tpc.getChargeNo();
                    for (int i = 0; i < val.length; i++) {
                        String[] val0 = val[i];
                        String flagchar=val0[0].trim().substring(0, 1).toLowerCase();
//                        log.info(flagchar);
                        if (flagchar.equals("h")) {

                            //以下からcsv入力です。
                            pw.print(tpc.getDate() + ",");
                            pw.print(tpc.getKeiyakuNo() + ",");
                            log.info("  " + Arrays.toString(val0).replace(" ", ""));
                            for (int k = 0; k < val0.length; k++) {
                                String val1 = val0[k];
                                pw.print(val1 + ",");
                            }
                            String cno0 = val0[6];
//                        log.info(cno0);
                            for (int k = 0; k < cno.length; k++) {
                                if (cno0.trim().equals(cno[k][0].trim())) {
                                    log.info("    chargeNo: " + Arrays.toString(cno[k]));
                                    for (int l = 0; l < cno[k].length; l++) {
                                        String cno2 = cno[k][l];
                                        pw.print(cno2 + ",");
                                    }
                                }
                            }

                            pw.println();
                        }
                    }

                    tpc.close();
                } catch (Exception ex) {
                    log.error(ex);
                    log.error("東鉄のPDFではありません。");
//                    System.exit(0);
                }

            }
        }
        pw.close();
//        
    }

    private PDDocument document;
    private PDRectangle rec;

    public TotetsuHsectionController(String path) throws IOException {

        File file = new File(path);
        this.document = PDDocument.load(file);
        this.rec = document.getPage(0).getMediaBox();
    }

    public String Read(double x1, double y1, double x2, double y2) throws IOException {

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
        if (text.trim().isEmpty()) {
            text = "-";
        }
        return text.trim();

    }

    public String getDate() throws IOException {
        return this.Read(180, 27, 208, 32).replace(".", "/").replace(" ", "").trim();
    }

    public String getKeiyakuNo() throws IOException {
        return this.Read(179, 23, 205, 27).replace("-", "").trim();
    }

    public String[] oneline(double y1, double y2) {
        String[] val = null;
        try {
            String size = this.Read(10, y1, 60, y2);
            String length = this.Read(58, y1, 71, y2);
            String bundles = this.Read(71, y1, 81, y2);
            String piece = this.Read(81, y1, 91, y2);
            String tp = this.Read(91, y1, 109, y2);//totalpiece
            String weight = this.Read(109, y1, 127, y2);
            String cno = this.Read(127, y1, 149, y2);
            String yp = this.Read(149, y1, 160, y2);
            String ts = this.Read(160, y1, 171, y2);
            String yr = this.Read(171, y1, 179, y2);
            String el = this.Read(179, y1, 189, y2);
            String bending = this.Read(188, y1, 192, y2);
            String it1 = this.Read(192, y1, 198, y2);
            String it2 = this.Read(198, y1, 205, y2);
            String it3 = this.Read(204, y1, 212, y2);
            String ita = this.Read(211, y1, 220, y2);
            String ttc1 = this.Read(220, y1, 227, y2);
            String ttc2 = this.Read(227, y1, 234, y2);
            String ttc3 = this.Read(233, y1, 241, y2);
            String ttca = this.Read(240, y1, 250, y2);
            String ut = this.Read(249, y1, 251, y2);
            String gaikan = this.Read(251, y1, 255, y2);
            val = new String[]{
                size, length, bundles, piece, tp,
                weight, cno, yp, ts, yr, el, bending,
                it1, it2, it3, ita,
                ttc1, ttc2, ttc3, ttca,
                ut, gaikan
            };
//            log.info("size="+size);
//            log.info("length="+length);
//            log.info("bundles="+bundles);
//            log.info("piece="+piece);
//            log.info("totalpiece="+tp);
//            log.info("weight="+weight);
//            log.info("charge.No="+cno);
//            log.info("yield point="+yp);
//            log.info("tensile strength="+ts);
//            log.info("yieldratio="+yr);
//            log.info("elongation="+el);
//            log.info("bendingtest="+bending);
//            log.info("impacttest1="+it1);
//            log.info("impacttest2="+it2);
//            log.info("impacttest3="+it3);
//            log.info("impacttestAVG="+ita);
//            log.info("Through-thicknessCharacteristics1="+ttc1);
//            log.info("Through-thicknessCharacteristics2="+ttc2);
//            log.info("Through-thicknessCharacteristics3="+ttc3);
//            log.info("Through-thicknessCharacteristicsAVG="+ttca);
//            log.info("UT="+ut);
//            log.info("概観="+gaikan);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(TotetsuHsectionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public String[][] getCoilInfo() throws IOException {

        double dl = 8.5;
        int n = 8;
        String[][] val0 = new String[n][];

        for (int i = 0; i < n; i++) {
            val0[i] = this.oneline(76.5 + i * dl, 85 + i * dl);
//            if(val[0].trim().contains("TOTAL"))break;
//            log.info(Arrays.toString(val0[i]));
        }

        return val0;

    }

    public String[][] getChargeNo() {

        ArrayList<String[]> l1 = new ArrayList();

        double[][] val = new double[][]{
            {10, 182, 150, 186},//鋼番1行目
            {10, 188, 150, 192},//鋼番2行目
            {10, 192, 150, 195},//鋼番3行目
            {10, 197, 150, 200},//鋼番4行目
            {10, 199, 150, 203},//鋼番5行目
            {10, 204, 150, 208},//鋼番6行目
        };

        try {

            for (int i = 0; i < val.length; i++) {
                double[] v = val[i];
                String v0 = this.Read(v[0], v[1], v[2], v[3]);
                if (v0.trim().equals("-")) {
                    continue;
                }
                String[] v1 = v0.split(" ");
                l1.add(v1);
//                log.info("chargeNo: " + Arrays.toString(v1));

            };

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }

        String[][] val1 = new String[l1.size()][];
        for (int i = 0; i < val1.length; i++) {
            val1[i] = l1.get(i);
        }
        return val1;

    }

    public String getThickness(String a) {
        String t = "0";
        String b = "" + a.charAt(0);
//        System.out.println("b = " + b);
        if (b.contains("1")) {
            t = "1.6";
        } else if (b.contains("2")) {
            t = "2.3";
        } else if (b.contains("3")) {
            t = "3.2";
        }
        return t;
    }

    public void close() throws IOException {
        document.close();
    }

}
