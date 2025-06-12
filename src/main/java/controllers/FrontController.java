package controllers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import actions.ActionBase;
import actions.UnknownAction;
import constants.ForwardConst;

/**
 * Servlet implementation class FrontController
 */
@WebServlet("/")
public class FrontController extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FrontController() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Actionクラスのインスタンス
        ActionBase action = getAction(request, response);
        
        //サーブレットコンテキスト、リクエスト、レスポンスをActionインスタンスのフィールドに設定
        action.init(getServletContext(), request, response);
        
        //Actionクラスの処理を呼び出す
        action.process();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
    
    //リクエストパラメーターの値から該当インスタンスを作成し返却する
    @SuppressWarnings({ "rawtypes", "unchecked"})//コンパイラ警告抑制
    private ActionBase getAction(HttpServletRequest request, HttpServletResponse response) {
        Class type = null;
        ActionBase action = null;
        try {
            //リクエストからパラメーターactionの値取得
            String actionString = request.getParameter(ForwardConst.ACT.getValue());
            
            //該当Actionオブジェクト作成
            type = Class.forName(String.format("actions.%sAction", actionString));
            
            //ActionBaseオブジェクトにキャスト
            action = (ActionBase)(type.asSubclass(ActionBase.class).getDeclaredConstructor().newInstance());
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | SecurityException
                | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
            //リクエストパラメータのactionの値が不正の時、エラー処理を行うactionオブジェクトを作成
            action = new UnknownAction();
        }
        return action;
    }
    

}
