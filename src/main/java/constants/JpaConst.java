package constants;

public interface JpaConst {
    //persistence-unit名
    String PERSISTENCE_UNIT_NAME = "daily_report_system";
    
    //データ取得件数最大値(ここでは1ぺージあたり15件まで)
    int ROW_PER_PAGE = 15;
    
    //従業員テーブル
    String TABLE_EMP = "employees";
    
    //従業員テーブルカラム
    String EMP_COL_ID = "id";//id
    String EMP_COL_CODE = "code"; //社員番号
    String EMP_COL_NAME = "name";//氏名
    String EMP_COL_PASS = "password";//パスワード
    String EMP_COL_ADMIN_FLAG = "admin_flag";//管理者権限
    String EMP_COL_CREATED_AT = "created_at";//登録日時
    String EMP_COL_UPDATED_AT = "updated_at";//更新日時
    String EMP_COL_DELETE_FLAG = "delete_flag";//削除フラグ
    
    int ROLE_ADMIN = 1;//管理者権限ON(管理者用)
    int ROLE_GENERAL = 0;//管理者権限OFF(一般)
    int EMP_DEL_TRUE = 1;//削除フラグON 削除済み
    int EMP_DEL_FALSE = 0;//削除フラグOFF 現役
    
    //日報テーブル
    String TABLE_REP = "reports";//テーブル名
    
    //カラム
    String REP_COL_ID = "id";//id
    String REP_COL_EMP = "employee_id";//日報を作成した従業員のid
    String REP_COL_REP_DATE = "report_date";//日報の日付
    String REP_COL_TITLE = "title";//日報タイトル
    String REP_COL_CONTENT = "content";//内容
    String REP_COL_CREATED_AT = "created_at";//登録日時
    String REP_COL_UPDATED_AT = "updated_at";//更新日時
    
    //entity
    String ENTITY_EMP = "employee";//従業員
    String ENTITY_REP = "report";//日報
    
    //JPQLパラメータ
    String JPQL_PARM_CODE = "code";//社員番号
    String JPQL_PARM_PASSWORD = "password";//パスワード
    String JPQL_PARM_EMPLOYEE = "employee";//従業員
    
    //NamedQueryのname,query
    //全従業員idの降順に取得
    String Q_EMP_GET_ALL = ENTITY_EMP + ".getAll";//name
    String Q_EMP_GET_ALL_DEF = "SELECT e FROM Employee AS e ORDER BY e.id DESC";//query
    
    //すべての従業員の件数を取得
    String Q_EMP_COUNT = ENTITY_EMP + ".count";
    String Q_EMP_COUNT_DEF = "SELECT COUNT(e) FROM Employee AS e";
    
    //社員番号とハッシュ化済パスワードを条件に未削除の従業員を取得する
    String Q_EMP_GET_BY_CODE_AND_PASS = ENTITY_EMP + ".getByCodeAndPass";
    String Q_EMP_GET_BY_CODE_AND_PASS_DEF = "SELECT e FROM Employee AS e WHERE e.deleteFlag = 0 AND e.code = :" + JPQL_PARM_CODE + " AND e.password = :" + JPQL_PARM_PASSWORD;
    
    //指定した社員番号を保持する従業員の件数を取得
    String Q_EMP_COUNT_REGISTERED_BY_CODE =ENTITY_EMP + ".countRegisteredByCode";
    String Q_EMP_COUNT_REGISTERED_BY_CODE_DEF = "SELECT COUNT(e) FROM Employee AS e WHERE e.code = :" + JPQL_PARM_CODE;
    
    //すべての日報をidの降順に取得
    String Q_REP_GET_ALL = ENTITY_REP + ".getAll";
    String Q_REP_GET_ALL_DEF = "SELECT r FROM Report AS r ORDER BY r.id DESC";
    
    //すべての日報件数を取得
    String Q_REP_COUNT = ENTITY_REP + ".count";
    String Q_REP_COUNT_DEF = "SELECT COUNT(r) FROM Report AS r";
    
    //指定した従業員が作成した日報を全件idの降順で取得
    String Q_REP_GET_ALL_MINE = ENTITY_REP + ".getAllMine";
    String Q_REP_GET_ALL_MINE_DEF = "SELECT r FROM Report AS r WHERE r.employee = :" + JPQL_PARM_EMPLOYEE + " ORDER BY r.id DESC";
    
    //指定した従業員が作成した日報件数を取得
    String Q_REP_COUNT_ALL_MINE = ENTITY_REP + ".countAllMine";
    String Q_REP_COUNT_ALL_MINE_DEF = "SELECT COUNT(r) FROM Report AS r WHERE r.employee = :" + JPQL_PARM_EMPLOYEE;
    

}
