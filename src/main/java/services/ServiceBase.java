package services;

import jakarta.persistence.EntityManager;

import utils.DBUtil;

public class ServiceBase {
    //DB接続にかかわる共通処理を行うクラス
    //EntityManagerインスタンス
    protected EntityManager em = DBUtil.createEntityManager();
    
    //EMクローズ
    public void close() {
        if(em.isOpen()) {
            em.close();
        }
    }

}
