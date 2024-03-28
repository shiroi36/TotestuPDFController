/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SocketTest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import totetsu1.TotetsuPDFController2;
import java.io.*;
import java.util.logging.Level;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author keita
 */
public class client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  {
        // TODO code application logic here
        
        args=new String[]{"f00"};
        Logger log = LogManager.getLogger(client.class);
        if(args.length!=1){
            log.error("コマンドライン引数がありません。");
            System.exit(0);
        }
        
        try {
            client c =new client();
            String[] con=c.getIPandPortnum();
            log.info("コマンドライン引数がありました。");
            log.info("myIPsetting.xml：ip:"+con[0]+"\tportnum:"+con[1]);
            //送信先のIPアドレス(ドメインなどの名前)とポートを指定
            Socket sock = new Socket(con[0], Integer.parseInt(con[1]));
            //送信ストリームの取得
            OutputStream out = sock.getOutputStream();
            //送信データ
//            String sendData = "てすとですよ";
            String sendData = args[0];
            //文字列をUTF-8形式のバイト配列に変換して送信
            out.write(sendData.getBytes("UTF-8"));
            //送信データの表示
            log.info("サーバー　　　：「" + sendData + "」を送信しました。");
            //送信ストリームを表示
            out.close();
            //終了
            sock.close();

        } catch (IOException e) {
            log.error(e.getMessage());
//            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            log.error(e.getMessage());
//            e.printStackTrace();
        } catch (SAXException ex) {
            log.error(ex.getMessage());
        }
    }
    
    public String[] getIPandPortnum() throws ParserConfigurationException, FileNotFoundException, SAXException, IOException{
        
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
            NodeList lst = doc.getElementsByTagName("ip");
            String ip="";
            Node n = lst.item(0);
            for (Node ch = n.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
                ip=ch.getNodeValue();
            }
            lst = doc.getElementsByTagName("port");
            String port="";
            n = lst.item(0);
            for (Node ch = n.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
                port=ch.getNodeValue();
            }
            
            return new String[]{ip,port};

    }

}
