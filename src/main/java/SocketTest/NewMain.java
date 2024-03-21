/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SocketTest;

/**
 *
 * @author keita
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SQL_OPE sql = new SQL_OPE("jdbc:h2:tcp://localhost/./serverlog","junapp","");
        sql.executeUpdate("create table if not exists log("
                + "id INT auto_increment PRIMARY KEY,"
                + "logtime date,"
                + "eid varchar,"
                + "sign varchar "
                + ")"
        );
    }
    
}
