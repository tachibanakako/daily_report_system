package services;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.NoResultException;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import constants.JpaConst;
import models.Employee;
import models.validators.EmployeeValidator;
import utils.EncryptUtil;

public class EmployeeService extends ServiceBase {
    //指定ページの一覧画面のデータを取得、EViewリストで返却
    public List<EmployeeView> getPerPage(int page){
        List<Employee> employees = em.createNamedQuery(JpaConst.Q_EMP_GET_ALL, Employee.class)
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page -1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return EmployeeConverter.toViewList(employees);
    }
    //従業員データ件数を取得、返却
    public long countAll() {
        long empCount = (long) em.createNamedQuery(JpaConst.Q_EMP_COUNT, Long.class)
                .getSingleResult();
        return empCount;
    }
    //取得データをEmployeeViewのインスタンスで返却
    
    public EmployeeView findOne(String code, String plainPass, String pepper) {
        Employee e = null;
        try {
            //ハッシュ化
            String pass = EncryptUtil.getPasswordEncrypt(plainPass, pepper);
            //社員番号とハッシュ化パスワードを条件に未削除の従業員を取得
            e = em.createNamedQuery(JpaConst.Q_EMP_GET_BY_CODE_AND_PASS, Employee.class)
                    .setParameter(JpaConst.JPQL_PARM_CODE, code)
                    .setParameter(JpaConst.JPQL_PARM_PASSWORD, pass)
                    .getSingleResult();
        }catch(NoResultException ex) {
            
        }
        return EmployeeConverter.toView(e);
    }
    
    //idを条件に取得したデータをEmployeeViewインスタンスで返却する
    public EmployeeView findOne(int id) {
        Employee e = findOneInternal(id);
        return EmployeeConverter.toView(e);
    }
    
    //社員番号を条件に該当データ件数を取得し返却
    public long countByCode(String code) {
        long employees_count = (long) em.createNamedQuery(JpaConst.Q_EMP_COUNT_REGISTERED_BY_CODE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_CODE, code)
                .getSingleResult();
        return employees_count;
    }
    //画面から入力された従業員の登録内容をもとにデータを作成、テーブルに登録
    public List<String> create(EmployeeView ev, String pepper){
        //passハッシュ化
        String pass = EncryptUtil.getPasswordEncrypt(ev.getPassword(), pepper);
        ev.setPassword(pass);
        
        //登録日時、更新日時＝現在時刻で設定
        LocalDateTime now = LocalDateTime.now();
        ev.setCreatedAt(now);
        ev.setUpdatedAt(now);
        
        //登録内容のバリデーション
        List<String> errors = EmployeeValidator.validate(this, ev, true, true);
        
        //バリデーションなし→データ登録
        if (errors.size() == 0) {
            create(ev);
        }
        //error 返却
        return errors;
    }
    //従業員テーブルの更新
    public List<String> update(EmployeeView ev, String pepper){
        //idで従業員情報取得
        EmployeeView savedEmp = findOne(ev.getId());
        
        boolean validateCode = false;
        if(!savedEmp.getCode().equals(ev.getCode())) {
            //社員番号更新、バリデーション
            validateCode = true;
            //変更後の番号設定
            savedEmp.setCode(ev.getCode());
        }
        
        boolean validatePass = false;
        if(ev.getPassword() != null && !ev.getPassword().equals("")) {
            //passに入力があるときのバリデーション
            validatePass = true;
            //変更後パスワードをハッシュ化して設定
            savedEmp.setPassword(
                    EncryptUtil.getPasswordEncrypt(ev.getPassword(), pepper));
        }
        
        savedEmp.setName(ev.getName());
        savedEmp.setAdminFlag(ev.getAdminFlag());
        
        //更新日時　現在時刻に設定
        LocalDateTime today = LocalDateTime.now();
        savedEmp.setUpdatedAt(today);
        
        //更新内容のバリデーション
        List<String> errors = EmployeeValidator.validate(this, savedEmp, validateCode, validatePass);
        if (errors.size() == 0) {
            update(savedEmp);
        }
        return errors;
    }
    
    //従業員データの論理削除
    public void destroy(Integer id) {
        //idから従業員情報取得
        EmployeeView savedEmp = findOne(id);
        //更新日時の設定
        LocalDateTime today = LocalDateTime.now();
        savedEmp.setUpdatedAt(today);
        //論理削除フラグ
        savedEmp.setDeleteFlag(JpaConst.EMP_DEL_TRUE);
        //更新処理
        update(savedEmp);
    }
    
    //認証
    public Boolean validateLogin(String code, String plainPass, String pepper) {
        boolean isValidEmployee = false;
        if(code != null && !code.equals("") && plainPass != null && !plainPass.equals("")) {
            EmployeeView ev = findOne(code, plainPass, pepper);
            
            if(ev != null && ev.getId() != null){
                //データ取得できた→認証成功
                isValidEmployee = true;
            }
            }
            return isValidEmployee;
        }
        
        //id条件でデータ取得、Empインスタンスで返却
        
        private Employee findOneInternal(int id) {
            Employee e = em.find(Employee.class, id);
                return e;
            }
        //データ登録
        private void create(EmployeeView ev) {
            em.getTransaction().begin();
            em.persist(EmployeeConverter.toModel(ev));
            em.getTransaction().commit();
        }
        //データ更新
        private void update(EmployeeView ev) {
            em.getTransaction().begin();
            Employee e = findOneInternal(ev.getId());
            EmployeeConverter.copyViewToModel(e, ev);
            em.getTransaction().commit();
        }
    
        
    }
    
    

    

