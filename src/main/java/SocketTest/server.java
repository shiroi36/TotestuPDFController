/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SocketTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author keita
 */
public class server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Logger log = LogManager.getLogger(server.class);
        // TODO code application logic here
        //サーバーのポート番号を指定
        

        String port=null;
        MyRunnable[] myrunnable=null;
        try {
            client c = new client();
            port = c.getIPandPortnum()[1];
            server s = new server();
            String[][] val = s.getSign();
            
            log.info("----------受信テストパッケージ開始----------");
            log.info("    myIPsetting.xml：ポート番号：" + port);
            log.info("    myIPsetting.xml：実行プログラム一覧&Runnable作成");
            myrunnable=new MyRunnable[val[0].length];
            for (int i = 0; i < val[0].length; i++) {
                log.info("    myIPsetting.xml：("+val[0][i]+" , "+val[1][i]+" , "+val[2][i]+")");
                myrunnable[i]=new MyRunnable(val[0][i],val[1][i],val[2][i],log);
            }
            
            
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            log.error(ex.getMessage());
            System.exit(1);
        }
        ServerSocket svSock=null;
        while (true) {
            try {

                log.info("---------------------------------------------------------------------");
                log.info("サーバー       ：一応1秒待ちます");
                Thread.sleep(1000); // 1秒(1万ミリ秒)間だけ処理を止める
                log.info("サーバー       ：接続開始します");
                svSock = new ServerSocket(Integer.parseInt(port));
                log.info("サーバー       ：接続開始しました。信号を待ちます。");

                //アクセスを待ち受け
                Socket sock = svSock.accept();

                //受信データバッファ
                byte[] data = new byte[1024];

//                log.info("サーバー       ：：受信しました：" );
                //受信ストリームの取得
                InputStream in = sock.getInputStream();

                //データを受信
                int readSize = in.read(data);

                //受信データを読み込んだサイズまで切り詰め
                data = Arrays.copyOf(data, readSize);
                String content = new String(data, "UTF-8");

                //バイト配列を文字列に変換して表示
                log.info("サーバー       ：「" + content + "」を受信しました。");
                for (int i = 0; i < myrunnable.length; i++) {
                    if (content.trim().toLowerCase().
                            equals(myrunnable[i].getSign().trim().toLowerCase())) {
                        log.info("  ファイル      ：[" + myrunnable[i].getSign() + "]["
                                + myrunnable[i].getEID() + "]を認識しました");
                        log.info("  **割り込み    ：trueで過去実行スレッドが作業中・・・" + myrunnable[i].isWorking());
                        //ここにsignと対応する実行batファイルを実行する。

                        if (myrunnable[i].isWorking()) {
                            log.info("  **割り込み    ：同じファイルが動作中のため、信号["
                                    + myrunnable[i].getSign() + "]はスキップ");
                            break;
                        }
                        //synchronized入れてるから、上のifなくても処理まって実行してくれるが
//                        連打したら連打した分だけ実行するからちょっと危険
                        Thread th = new Thread(myrunnable[i]);
                        th.start();
                    }

                }


                //受信ストリームの終了
                in.close();

                //サーバー終了
                svSock.close();
                
                
                log.info("サーバー       ：終了します。");

            } catch (IOException e) {
                log.error(e.getMessage());
//            e.printStackTrace();
            } catch (InterruptedException | IllegalThreadStateException ex) {
                log.error("サーバー       ："+ex.getMessage());
                log.info("サーバー       ：スレッドが正常に実行されませんでした。");
                log.info("サーバー       ：信号は正常に処理されていません。");
                log.info("サーバー       ：サーバ終了します。");
                log.info("---------------------------------------------------------------------");
                break;
            }
        }
    }
    
//    public server(){
//        SQL_OPE sql=new SQL_OPE("jdbc:h2:./serverlog","junapp","");
//        sql.executeUpdate("create table if not exists log("
//                + "id INT auto_increment PRIMARY KEY,"
//                + "logtime date,"
//                + "eid varchar,"
//                + "sign varchar "
//                + ")");
//        
//    }

    public String[][] getSign() throws ParserConfigurationException, FileNotFoundException, SAXException, IOException {

        //DOMの準備をする
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        //ローカルに配置したXML文書を読み込む
//            <?xml version="1.0" encoding="UTF-8" ?>
//            <div>
//              <!-- SocketTest.clientで絶対必要 -->
//                <ip>localhost</ip> 
//            </div>
        Document doc = db.parse(new FileInputStream("myIPsetting.xml"));

        //要素をノードリストとして取り出す
        NodeList lst = doc.getElementsByTagName("sign");
        String[][] val1 = new String[3][lst.getLength()];
        for (int i = 0; i < lst.getLength(); i++) {
            Node n = lst.item(i);
            for (Node ch = n.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
                val1[0][i] = ch.getNodeValue();
            }
        }

        lst = doc.getElementsByTagName("batfile");
        for (int i = 0; i < lst.getLength(); i++) {
            Node n = lst.item(i);
            for (Node ch = n.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
                val1[1][i] = ch.getNodeValue();
            }
        }

        lst = doc.getElementsByTagName("eid");
        for (int i = 0; i < lst.getLength(); i++) {
            Node n = lst.item(i);
            for (Node ch = n.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
                val1[2][i] = ch.getNodeValue();
            }
        }
        return val1;

    }
    
    private static class MyRunnable implements Runnable {
    
        private final String file;
        private final Logger log;
        private final String sign;
        private boolean flag;
        private Date date;
        private final String eid;
        private MyRunnable(String sign,String file,String eid,Logger log){
            this.file=file;
            this.log=log;
            this.sign=sign;
            this.flag=false;
            this.eid=eid;
            
        }
        public String getSign(){
            return sign;
        }

        public String getFile() {
            return file;
        }
        public String getEID() {
            return eid;
        }
        
        public boolean isWorking(){
//            2023年3月10日ちょっと同期の処理がわからんからこのflag設けた
            return flag;
        }

        public String getInfo(){
//            2023年3月10日ちょっと同期の処理がわからんからこのflag設けた
            return "["+ sign+ "]["+ eid+ "]";
        }
        @Override
        public void run() {
            synchronized (this) {
                date=new Date();
                log.info("  **割り込み    ：実行します" + this.getInfo() +date.toString());
                flag = true;
                try {
                    ProcessBuilder pb = new ProcessBuilder();
                    pb.command(file);
                    //javaを更新したら使用できなくなってた2024年3月26日
//                    pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
//                    pb.redirectError(ProcessBuilder.Redirect.DISCARD);
//                    以下の設定はstdoutをjava上に表示する
                    pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                    pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                    Process process;
                    process = pb.start();
                    process.waitFor();
                    log.info("  **割り込み    ：実行終了しました："+ this.getInfo() +date.toString());
                } catch (IOException ex) {
                    log.error(ex);
//                java.util.logging.Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    log.error(ex);
                }
                flag = false;
            }

//                        System.out.println(ret);
//                        int ret = process.waitFor();
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        
}

}
