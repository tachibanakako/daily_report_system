package actions;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import constants.PropertyConst;
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
    public void create()throws ServletException, IOException{
        if(checkToken()) {
            EmployeeView ev = new EmployeeView(
                    null,
                    getRequestParam(AttributeConst.EMP_CODE),
                    getRequestParam(AttributeConst.EMP_NAME),
                    getRequestParam(AttributeConst.EMP_PASS),
                    toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                    null,
                    null,
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue());
            //pepper
            String pepper = getContextScope(PropertyConst.PEPPER);
            //従業員情報登録
            List<String> errors = service.create(ev, pepper);
            
            if(errors.size() > 0) {
                //登録中エラーあり
                putRequestScope(AttributeConst.TOKEN, getTokenId());
                putRequestScope(AttributeConst.EMPLOYEE, ev);
                putRequestScope(AttributeConst.ERR, errors);
                //新規登録画面再表示
                forward(ForwardConst.FW_EMP_NEW);
            }else {
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());
                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
            }
        }
    }
    public void show()throws ServletException, IOException{
        //idでデータ取得
        EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));
        
        if(ev ==  null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {
            //データが取得できない、論理削除されている場合エラー表示
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return;
        }
        putRequestScope(AttributeConst.EMPLOYEE, ev);
        forward(ForwardConst.FW_EMP_SHOW);
    }
    
    public void edit()throws ServletException, IOException{
        EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));
        
        if(ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return;
        }
        
        putRequestScope(AttributeConst.TOKEN, getTokenId());
        putRequestScope(AttributeConst.EMPLOYEE, ev);
        
        forward(ForwardConst.FW_EMP_EDIT);
    }
    public void update()throws ServletException, IOException{
        if (checkToken()) {
            EmployeeView ev = new EmployeeView(
                    toNumber(getRequestParam(AttributeConst.EMP_ID)),
                    getRequestParam(AttributeConst.EMP_CODE),
                    getRequestParam(AttributeConst.EMP_NAME),
                    getRequestParam(AttributeConst.EMP_PASS),
                    toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                    null,
                    null,
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue());
            String pepper = getContextScope(PropertyConst.PEPPER);
            
            List<String> errors = service.update(ev, pepper);
            
            if(errors.size() > 0) {
                putRequestScope(AttributeConst.TOKEN, getTokenId());
                putRequestScope(AttributeConst.EMPLOYEE, ev);
                putRequestScope(AttributeConst.ERR, errors);
                
                forward(ForwardConst.FW_EMP_EDIT);
            }else {
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_UPDATED.getMessage());
                
                redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
            }
        }
    }
    public void destroy()throws ServletException, IOException{
        if(checkToken()) {
            service.destroy(toNumber(getRequestParam(AttributeConst.EMP_ID)));
            
            putSessionScope(AttributeConst.FLUSH, MessageConst.I_DELETED.getMessage());
            
            redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
        }
    }
    

}
