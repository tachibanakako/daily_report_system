package actions;

import java.io.IOException;

import jakarta.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.MessageConst;
import constants.PropertyConst;
import services.EmployeeService;

public class AuthAction extends ActionBase{
    private EmployeeService service;
    //メソッド実行
    @Override
    public void process()throws ServletException, IOException{
        service = new EmployeeService();
        
        invoke();
        
        service.close();
    }
    //ログイン画面表示
    public void showLogin()throws ServletException, IOException{
        putRequestScope(AttributeConst.TOKEN, getTokenId());
        
        String flush = getSessionScope(AttributeConst.FLUSH);
        if(flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }
        forward(ForwardConst.FW_LOGIN);
    }
    
    public void login() throws ServletException, IOException {

        String code = getRequestParam(AttributeConst.EMP_CODE);
        String plainPass = getRequestParam(AttributeConst.EMP_PASS);
        String pepper = getContextScope(PropertyConst.PEPPER);

        //有効な従業員か認証する
        Boolean isValidEmployee = service.validateLogin(code, plainPass, pepper);
        if (isValidEmployee) {
            //認証成功の場合

            //CSRF対策 tokenのチェック
            if (checkToken()) {

                //ログインした従業員のDBデータを取得
                EmployeeView ev = service.findOne(code, plainPass, pepper);
                //セッションにログインした従業員を設定
                putSessionScope(AttributeConst.LOGIN_EMP, ev);
                //セッションにログイン完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_LOGINED.getMessage());
                //トップページへリダイレクト
                redirect(ForwardConst.ACT_TOP, ForwardConst.CMD_INDEX);
            }
        } else {
            //認証失敗の場合

            //CSRF対策用トークンを設定
            putRequestScope(AttributeConst.TOKEN, getTokenId());
            //認証失敗エラーメッセージ表示フラグをたてる
            putRequestScope(AttributeConst.LOGIN_ERR, true);
            //入力された従業員コードを設定
            putRequestScope(AttributeConst.EMP_CODE, code);

            //ログイン画面を表示
            forward(ForwardConst.FW_LOGIN);
        }
    }
    //ログアウト処理
    public void logout() throws ServletException, IOException{
        //ログイン従業員のパラメータ削除
        removeSessionScope(AttributeConst.LOGIN_EMP);
        //セッションにログアウト時のフラッシュメッセージ
        putSessionScope(AttributeConst.FLUSH, MessageConst.I_LOGOUT.getMessage());
        //ログイン画面へ
        redirect(ForwardConst.ACT_AUTH, ForwardConst.CMD_SHOW_LOGIN);
    }
    
}
