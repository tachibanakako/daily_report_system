package actions;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import services.EmployeeService;

public class EmployeeAction extends ActionBase {
    private EmployeeService service;
    
    //メゾット実行
    @Override
    public void process() throws ServletException, IOException{
        service = new EmployeeService();
        invoke();
        service.close();
    }
    
    //一覧画面を表示
    public void index() throws ServletException, IOException{
        //データ取得
        int page = getPage();
        List<EmployeeView> employees = service.getPerPage(page);
        //データ件数取得
        long employeeCount = service.countAll();
        
        putRequestScope(AttributeConst.EMPLOYEES, employees);
        putRequestScope(AttributeConst.EMP_COUNT, employeeCount);
        putRequestScope(AttributeConst.PAGE, page);
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE);
        
        //セッションにフラッシュメッセージが設定される場合、リクエストスコープに移し、セッションからは削除
        String flush = getSessionScope(AttributeConst.FLUSH);
        if(flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }
        //一覧画面表示
        forward(ForwardConst.FW_EMP_INDEX);
    }
    //新規登録画面
    public void entryNew() throws ServletException, IOException{
        putRequestScope(AttributeConst.TOKEN,getTokenId());
        putRequestScope(AttributeConst.EMPLOYEE, new EmployeeView());
        
        //表示
        forward(ForwardConst.FW_EMP_NEW);
    }

}
