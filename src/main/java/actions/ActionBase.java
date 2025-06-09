package actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import constants.AttributeConst;
import constants.ForwardConst;
import constants.PropertyConst;

public abstract class ActionBase {
    protected ServletContext context;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    
    //初期化処理　サーブレットコンテキスト、リクエスト、レスポンスをフィールドに設定
    
    public void init(
            ServletContext servletContext,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        this.context = servletContext;
        this.request = servletRequest;
        this.response = servletResponse;
    }
    
    //フロントコントローラから呼び出されるメソッド
    public abstract void process() throws ServletException, IOException;
    
    //commandの値に該当のメソッドを実行
    protected void invoke() throws ServletException, IOException{
        Method commandMethod;
        try {
            //command取得
            String command = request.getParameter(ForwardConst.CMD.getValue());
            
            //command実行
            commandMethod = this.getClass().getDeclaredMethod(command, new Class[0]);
            commandMethod.invoke(this, new Object[0]);
        }catch(NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NullPointerException e) {
            //発生した例外をコンソールに表示
            e.printStackTrace();
            //commandの値が不正で実行できない場合エラー画面呼び出し
            forward(ForwardConst.FW_ERR_UNKNOWN);
        }
        
    }
    
    //jsp呼び出し
    protected void forward(ForwardConst target) throws ServletException, IOException{
        //jspファイルの相対パス作成
        String forward = String.format("/WEB-INF/views/%s.jsp", target.getValue());
        RequestDispatcher dispatcher = request.getRequestDispatcher(forward);
        //jspファイル呼び出し
        dispatcher.forward(request, response);
    }
    //URL構築、リダイレクト
    protected void redirect(ForwardConst action, ForwardConst command) throws ServletException, IOException{
        //URL構築
        String redirectUrl = request.getContextPath() + "/?action=" + action.getValue();
        if(command != null) {
            redirectUrl = redirectUrl + "&command=" + command.getValue();
        }
        
        //urlへリダイレクト
        response.sendRedirect(redirectUrl);
    }
    
    //CSRF対策
    protected boolean checkToken() throws ServletException, IOException{
        String _token = getRequestParam(AttributeConst.TOKEN);
        if(_token == null || !(_token.equals(getTokenId()))) {
            //エラー画面表示
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return false;
        }else {
            return true;
        }
    }
    //セッションIDの取得
    protected String getTokenId() {
        return request.getSession().getId();
    }
    
    //リクエストから表示を要求されているページ数を取得し返却
    protected int getPage() {
        int page;
        page = toNumber(request.getParameter(AttributeConst.PAGE.getValue()));
        if(page == Integer.MIN_VALUE) {
            page = 1;
        }
        return page;
    }
    //文字列を数値に変換
    protected int toNumber(String strNumber) {
        int number = 0;
        try {
            number = Integer.parseInt(strNumber);
        }catch(Exception e) {
            number = Integer.MIN_VALUE;
        }
        return number;
    }
    //LocalData型へ変換
    protected LocalDate toLocalDate(String strDate) {
        if(strDate == null || strDate.equals("")) {
            return LocalDate.now();
        }
        return LocalDate.parse(strDate);
    }
    //リクエストパラメータから引数で指定したパラメータの値を返却する
    protected String getRequestParam(AttributeConst key) {
        return request.getParameter(key.getValue());
    }
    //リクエストスコープにパラメータを設定する
    protected <V> void putRequestScope(AttributeConst key, V value) {
        request.setAttribute(key.getValue(), value);
    }
    
    //セッションスコープからパラメータの値取得、返却
    @SuppressWarnings("unchecked")
    protected <R> R getSessionScope(AttributeConst key) {
        return (R) request.getSession().getAttribute(key.getValue());
    }
    
    //セッションスコープにパラメータ設定
    protected <V> void putSessionScope(AttributeConst key, V value) {
        request.getSession().setAttribute(key.getValue(), value);
    }
    
    //セッションスコープから指定された名前のパラメーターを除去する
    protected void removeSessionScope(AttributeConst key) {
        request.getSession().removeAttribute(key.getValue());
    }
    
    //アプリケーションスコープから指定されたパラメータの値を取得、返却
    @SuppressWarnings("unchecked")
    protected <R> R getContextScope(PropertyConst key) {
        return (R) context.getAttribute(key.getValue());
    }


}
